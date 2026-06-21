package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AbilityMeta(
    name = "키미마로",
    rank = Rank.SS,
    guide = [
        "뼈다귀로 상대를 공격할 시에 강한 대미지를 주고,",
        "40% 확률로 상대에게 5초간 독 효과를 겁니다."
    ]
)
class Kimimaro(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : BaseItemSkill() {
        override val baseItems = arrayOf(ItemStack(Material.BONE, 1))
        override val itemDisplayName = "뼈다귀"

        override fun register() {
            registerItemEvents()
            onEntityDamage { e ->
                if (owner.inventory.itemInMainHand.type != Material.BONE) return@onEntityDamage
                val entity = e.entity as? LivingEntity ?: return@onEntityDamage
                e.damage = owner.attackCooldown.toDouble() * 7.0
                if (Math.random() < 0.4) {
                    entity.addPotionEffect(PotionEffectFactory.createPoison(100, 0))
                }
            }
        }
    })
}
