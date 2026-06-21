package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

@AbilityMeta(
    name = "메딕",
    rank = Rank.B,
    guide = [
        "(철괴 공격) 타인의 체력을 6만큼 회복합니다.",
        "(철괴 우클릭) 자신의 체력을 6만큼 회복합니다."
    ]
)
class Medic(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : ActiveSkill() {
        override val cooldownTicks = 160L
        private var pendingAction: (() -> Unit)? = null

        override fun register() {
            onEntityDamage { e ->
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onEntityDamage
                val entity = e.entity as? LivingEntity ?: return@onEntityDamage
                pendingAction = {
                    AbilityUtils.healEntity(entity, 6.0)
                    entity.sendMessage("${ChatColor.GREEN}${owner.name}의 메딕 능력으로 체력을 6 회복했습니다.")
                    SoundUtils.playSuccessSound(owner)
                    owner.sendMessage("${ChatColor.GREEN}${entity.name}의 체력을 6 회복시켰습니다.")
                    e.isCancelled = true
                }
                activate()
            }
            onRightClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onRightClick
                pendingAction = {
                    AbilityUtils.healEntity(owner, 6.0)
                    SoundUtils.playSuccessSound(owner)
                    owner.sendMessage("${ChatColor.GREEN}자신의 체력을 6 회복했습니다.")
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
