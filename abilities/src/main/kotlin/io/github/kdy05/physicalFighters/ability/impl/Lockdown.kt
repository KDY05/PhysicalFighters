package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.AbilityAPI
import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

@AbilityMeta(
    name = "봉인",
    rank = Rank.B,
    guide = [
        "(철괴 좌클릭) 반경 10미터 내 모든 플레이어의 능력을 1분간 봉인합니다.",
        "봉인된 플레이어는 배고픔 수치가 0이 됩니다."
    ]
)
class Lockdown(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    companion object {
        private const val RANGE = 10.0
    }

    override fun buildSkills() = listOf(object : ActiveContinueSkill() {
        override val cooldownTicks = 3600L
        override val durationTicks = 1200L
        private val silencedTargets = mutableListOf<Player>()

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                activate()
            }
        }

        override fun onActivate() {
            val targets = owner.world.getNearbyEntities(owner.location, RANGE, RANGE, RANGE)
                .filterIsInstance<Player>()
                .filter { it != owner }
            if (targets.isEmpty()) {
                owner.sendMessage("${ChatColor.RED}주변에 봉인할 대상이 없습니다.")
                return
            }
            silencedTargets.clear()
            silencedTargets.addAll(targets)
            targets.forEach { target ->
                AbilityAPI.service.silencePlayer(target, durationTicks)
                target.foodLevel = 0
                target.sendMessage("${ChatColor.RED}${owner.name}님에 의해 능력이 1분간 봉인되었습니다.")
            }
            val names = targets.joinToString(", ") { it.name }
            owner.sendMessage("${ChatColor.YELLOW}${names}님의 능력을 봉인했습니다.")
        }

        override fun onDeactivate() {
            silencedTargets.clear()
        }
    })
}
