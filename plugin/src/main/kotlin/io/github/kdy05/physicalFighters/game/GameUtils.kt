package io.github.kdy05.physicalFighters.game

import io.github.kdy05.physicalFighters.PhysicalFighters
import io.github.kdy05.physicalFighters.ability.Ability
import io.github.kdy05.physicalFighters.ability.AbilityRegistry
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GameUtils {

    @JvmStatic
    fun assignAbility(sender: CommandSender, abilityName: String, target: Player, abilityOverLap: Boolean) {
        val type = AbilityRegistry.getType(abilityName)
        if (type == null) {
            sender.sendMessage("${ChatColor.RED}존재하지 않는 능력입니다.")
            return
        }

        // 기존 능력 해제
        if (abilityOverLap) {
            // 중복 모드에서 액티브 능력 중복은 불가함.
            if (type.type.isActive) {
                AbilityRegistry.findAbilities(target)
                    .filter { it.abilityType.isActive }
                    .forEach { AbilityRegistry.deactivate(it) }
            }
        } else {
            AbilityRegistry.deactivateAll(target)
        }

        // 새로운 능력 적용
        val ability = AbilityRegistry.createAndActivate(abilityName, target)!!
        sender.sendMessage(
            "${ChatColor.GREEN}${target.name}${ChatColor.WHITE}님에게 " +
                "${ChatColor.GREEN}${ability.abilityName}${ChatColor.WHITE} 능력 할당이 완료되었습니다."
        )
        val senderName = if (sender is Player) sender.name else "Console"
        PhysicalFighters.plugin.logger.info(
            "${senderName}님이 ${target.name}님에게 ${ability.abilityName} 능력을 할당했습니다."
        )
    }

    @JvmStatic
    fun showInfo(player: Player, abilityOverLap: Boolean) {
        val ability = AbilityRegistry.findPrimaryAbility(player)
            ?: AbilityRegistry.findAbility(player)
        if (ability == null) {
            player.sendMessage("${ChatColor.RED}능력이 없거나 옵저버입니다.")
            return
        }
        buildList {
            add("${ChatColor.GREEN}---------------")
            add("${ChatColor.GOLD}- 능력 정보 -")
            if (abilityOverLap) {
                add("${ChatColor.DARK_AQUA}참고 : 능력 리스트중 가장 상단의 능력만 보여줍니다.")
            }
            add(
                "${ChatColor.AQUA}${ability.abilityName}${ChatColor.WHITE}" +
                    " [${ability.abilityType}] ${ability.rank}"
            )
            addAll(ability.guide)
            add(getTimerText(ability))
            add("${ChatColor.GREEN}---------------")
        }.forEach { player.sendMessage(it) }
    }

    private fun getTimerText(ability: Ability): String {
        val cooldown = if (ability.abilityType.isActive) "${ability.coolDown}초" else "없음"
        val duration = if (ability.abilityType == Ability.Type.ActiveContinue) "${ability.duration}초" else "없음"
        return "${ChatColor.RED}쿨타임 : ${ChatColor.WHITE}$cooldown / ${ChatColor.RED}지속시간 : ${ChatColor.WHITE}$duration"
    }

    /**
     * OnKill 설정에 따라 사망 처리를 실행합니다.
     * (관전자 모드 전환 / 킥 / 밴)
     */
    @JvmStatic
    fun applyDeathPenalty(victim: Player) {
        val plugin = PhysicalFighters.plugin
        val onKill = plugin.configManager.onKill
        if (onKill <= 0) return

        when (onKill) {
            1 -> {
                val deathLocation = victim.location.clone()
                Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                    victim.gameMode = GameMode.SPECTATOR
                    victim.spigot().respawn()
                    victim.teleport(deathLocation)
                    victim.sendTitle(
                        "${ChatColor.RED}사망하였습니다!",
                        "${ChatColor.YELLOW}관전자 모드로 전환합니다.", 10, 100, 10
                    )
                }, 1L)
            }

            2 -> victim.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.")

            3 -> {
                if (!victim.isOp) {
                    Bukkit.getBanList(BanList.Type.NAME).addBan(
                        victim.name,
                        "당신은 죽었습니다. 다시 들어오실 수 없습니다.", null, null
                    )
                    victim.kickPlayer("당신은 죽었습니다. 다시 들어오실 수 없습니다.")
                } else {
                    victim.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.")
                }
            }
        }
    }
}
