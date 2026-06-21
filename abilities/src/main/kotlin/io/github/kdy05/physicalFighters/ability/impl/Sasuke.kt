package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

@AbilityMeta(
    name = "사스케",
    rank = Rank.S,
    guide = ["(철괴 공격) 엄청난 대미지로 감전시킵니다."]
)
class Sasuke(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : ActiveSkill() {
        override val cooldownTicks = 600L
        private var pendingAction: (() -> Unit)? = null

        override fun register() {
            onEntityDamage { e ->
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onEntityDamage
                val entity = e.entity as? LivingEntity ?: return@onEntityDamage
                pendingAction = {
                    entity.world.strikeLightning(entity.location)
                    entity.damage(20.0)
                }
                activate()
            }
        }

        override fun onActivate() {
            pendingAction?.invoke()
            pendingAction = null
        }
    })
}
