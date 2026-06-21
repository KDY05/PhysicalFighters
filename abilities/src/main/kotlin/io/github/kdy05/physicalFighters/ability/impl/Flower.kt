package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

@AbilityMeta(
    name = "흡혈초",
    rank = Rank.SS,
    guide = [
        "(철괴 공격) 맞은 사람의 체력을 흡수합니다.",
        "(철괴 우클릭) 자신의 체력을 소비해 레벨을 얻습니다."
    ]
)
class Flower(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : ActiveSkill() {
        override val cooldownTicks = 600L
        private var pendingAction: (() -> Unit)? = null

        override fun register() {
            onEntityDamage { e ->
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onEntityDamage
                val entity = e.entity as? LivingEntity ?: return@onEntityDamage
                pendingAction = {
                    AbilityUtils.piercingDamage(entity, 4.0)
                    AbilityUtils.healEntity(owner, 4.0)
                    entity.sendMessage("${ChatColor.RED}${owner.name}(이)가 당신의 체력을 흡수했습니다.")
                    owner.sendMessage("${ChatColor.RED}${entity.name}의 체력을 흡수했습니다.")
                }
                activate()
            }
            onRightClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onRightClick
                if (owner.health < 16.0) {
                    owner.sendMessage("${ChatColor.RED}체력이 부족합니다.")
                    return@onRightClick
                }
                pendingAction = {
                    owner.level += 1
                    owner.health = maxOf(0.0, owner.health - 15.0)
                    owner.sendMessage("${ChatColor.GREEN}레벨을 얻었습니다.")
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
