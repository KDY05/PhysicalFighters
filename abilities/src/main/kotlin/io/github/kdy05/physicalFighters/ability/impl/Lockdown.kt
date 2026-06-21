package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.AbilityAPI
import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent

@AbilityMeta(
    name = "봉인",
    rank = Rank.B,
    guide = [
        "특정 플레이어의 능력을 1분간 봉인하며 배고픔 수치를 0으로 만듭니다.",
        "\"/va lock <nickname>\" 명령어로 작동하며 대상이 60칸 이내에 있어야 합니다."
    ]
)
class Lockdown(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    companion object {
        private const val MAX_RANGE = 60.0
    }

    override fun buildSkills() = listOf(object : ActiveContinueSkill() {
        override val cooldownTicks = 1600L
        override val durationTicks = 1200L
        private var pendingTarget: Player? = null

        override fun register() {
            on(PlayerCommandPreprocessEvent::class) { e ->
                if (e.player != owner) return@on
                val parts = e.message.trim().split("\\s+".toRegex())
                if (parts.size != 3) return@on
                if (!parts[0].equals("/va", ignoreCase = true)) return@on
                if (!parts[1].equals("lock", ignoreCase = true)) return@on
                e.isCancelled = true

                val target = Bukkit.getPlayerExact(parts[2])
                if (target == null) {
                    owner.sendMessage("${ChatColor.RED}존재하지 않는 플레이어입니다.")
                    return@on
                }
                if (target == owner) {
                    owner.sendMessage("${ChatColor.RED}자기 자신에게 능력을 사용할 수 없습니다.")
                    return@on
                }
                if (owner.location.distance(target.location) > MAX_RANGE) {
                    owner.sendMessage("${ChatColor.RED}거리가 너무 멉니다.")
                    return@on
                }
                pendingTarget = target
                activate()
            }
        }

        override fun onActivate() {
            val target = pendingTarget ?: return
            pendingTarget = null
            owner.sendMessage("${ChatColor.YELLOW}${target.name}님의 능력을 60초간 봉인합니다.")
            target.sendMessage("${ChatColor.RED}경고, ${owner.name}님이 당신에게 봉인 능력을 사용했습니다.")
            target.sendMessage("${ChatColor.RED}1분간 능력 사용이 봉인되며 배고픔 수치가 0이 됩니다.")
            AbilityAPI.service.silencePlayer(target, durationTicks)
            target.foodLevel = 0
        }

        override fun onDeactivate() {}
    })
}
