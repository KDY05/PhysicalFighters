package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.abilityAPI.AbilityAPI
import io.github.kdy05.physicalFighters.ability.PFAbility
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

@AbilityMeta(
    name = "클로킹",
    rank = Rank.A,
    guide = [
        "(철괴 좌클릭) 일정 시간동안 다른 사람에게 보이지 않습니다.",
        "클로킹 상태에서는 타인에게 공격 받지 않습니다."
    ]
)
class Clocking(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : ActiveContinueSkill() {
        override val cooldownTicks = 600L
        override val durationTicks = 100L

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                activate()
            }
        }

        override fun onActivate() {
            Bukkit.getOnlinePlayers().forEach { it.hidePlayer(AbilityAPI.plugin, owner) }
        }

        override fun onDeactivate() {
            Bukkit.getOnlinePlayers().forEach { it.showPlayer(AbilityAPI.plugin, owner) }
        }
    })
}
