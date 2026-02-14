package io.github.kdy05.physicalFighters.command

import io.github.kdy05.physicalFighters.PhysicalFighters
import io.github.kdy05.physicalFighters.ability.AbilityRegistry
import io.github.kdy05.physicalFighters.game.GameManager
import io.github.kdy05.physicalFighters.game.GameUtils
import io.github.kdy05.physicalFighters.game.InvincibilityManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min

class GameCommand(
    private val plugin: PhysicalFighters,
    private val gameManager: GameManager
) : CommandInterface {

    override fun onCommandEvent(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        when (args[0].lowercase()) {
            "help" -> { handleHelp(sender); return true }
            "check" -> {
                if (filterConsole(sender)) return true
                GameUtils.showInfo(sender as Player, plugin.configManager.isAbilityOverLap)
                return true
            }
            "yes" -> {
                if (filterConsole(sender)) return true
                gameManager.handleYes(sender as Player)
                return true
            }
            "no" -> {
                if (filterConsole(sender)) return true
                gameManager.handleNo(sender as Player)
                return true
            }
        }

        // 운영자 권한 필터
        if (!sender.hasPermission("va.operate")) return false

        when (args[0].lowercase()) {
            "start" -> { gameManager.gameReady(sender); return true }
            "stop" -> { handleStop(sender); return true }
            "skip" -> { handleSkip(sender); return true }
            "ob" -> {
                if (filterConsole(sender)) return true
                gameManager.handleObserve(sender as Player)
                return true
            }
            "list" -> { handleList(sender, args); return true }
            "assign" -> { handleAssign(sender, args); return true }
            "reset" -> { handleReset(sender, args); return true }
        }

        return false
    }

    private fun handleHelp(sender: CommandSender) {
        sender.sendMessage("${ChatColor.GREEN}=== PhysicalFighters 명령어 목록 ===")
        sender.sendMessage("")

        sender.sendMessage("${ChatColor.YELLOW}■ 기본 명령어")
        sender.sendMessage("${ChatColor.GOLD}/va help${ChatColor.WHITE} - 이 도움말을 표시합니다.")
        sender.sendMessage("${ChatColor.GOLD}/va check${ChatColor.WHITE} - 자신의 능력 정보를 확인합니다.")
        sender.sendMessage("${ChatColor.GOLD}/va yes${ChatColor.WHITE} - 능력 선택을 확정합니다.")
        sender.sendMessage("${ChatColor.GOLD}/va no${ChatColor.WHITE} - 능력 선택을 거부합니다.")
        sender.sendMessage("")

        if (sender.hasPermission("va.operate")) {
            sender.sendMessage("${ChatColor.YELLOW}■ 게임 관리 명령어")
            sender.sendMessage("${ChatColor.GOLD}/va start${ChatColor.WHITE} - 게임을 시작합니다.")
            sender.sendMessage("${ChatColor.GOLD}/va stop${ChatColor.WHITE} - 게임을 중지합니다.")
            sender.sendMessage("${ChatColor.GOLD}/va skip${ChatColor.WHITE} - 능력 선택을 강제로 확정시킵니다.")
            sender.sendMessage("${ChatColor.GOLD}/va ob${ChatColor.WHITE} - 옵저버 설정을 합니다.")
            sender.sendMessage("")

            sender.sendMessage("${ChatColor.YELLOW}■ 능력 관리 명령어")
            sender.sendMessage("${ChatColor.GOLD}/va list [페이지]${ChatColor.WHITE} - 능력 목록을 표시합니다.")
            sender.sendMessage("${ChatColor.GOLD}/va assign [플레이어] [능력이름]${ChatColor.WHITE} - 플레이어에게 능력을 할당합니다.")
            sender.sendMessage("${ChatColor.GOLD}/va reset [플레이어]${ChatColor.WHITE} - 플레이어의 능력을 해제합니다.")
            sender.sendMessage("")

            sender.sendMessage("${ChatColor.YELLOW}■ 유틸리티 명령어")
            sender.sendMessage("${ChatColor.GOLD}/va util${ChatColor.WHITE} - 유틸리티 명령어 목록을 표시합니다.")
        }

        sender.sendMessage("${ChatColor.GREEN}================================")
    }

    private fun handleStop(sender: CommandSender) {
        if (gameManager.scenario == GameManager.ScriptStatus.NoPlay) {
            sender.sendMessage("${ChatColor.RED}아직 게임을 시작하지 않았습니다.")
            return
        }
        gameManager.stopGame()
        InvincibilityManager.isDamageGuard = false
        AbilityRegistry.deactivateAll()
        Bukkit.broadcastMessage("${ChatColor.GRAY}------------------------------")
        Bukkit.broadcastMessage("${ChatColor.YELLOW}관리자 ${sender.name}님이 게임 카운터를 중단시켰습니다.")
        Bukkit.broadcastMessage("${ChatColor.GRAY}모든 설정이 취소됩니다.")
        Bukkit.broadcastMessage("${ChatColor.GRAY}옵저버 설정은 초기화 되지 않습니다.")
    }

    private fun handleSkip(sender: CommandSender) {
        if (gameManager.scenario == GameManager.ScriptStatus.AbilitySelect) {
            Bukkit.broadcastMessage("${ChatColor.GRAY}관리자 ${sender.name}님이 능력을 강제로 확정시켰습니다.")
            gameManager.forceGameStart()
        } else {
            sender.sendMessage("${ChatColor.RED}능력 추첨중이 아닙니다.")
        }
    }

    private fun handleList(sender: CommandSender, args: Array<String>) {
        val page: Int = if (args.size >= 2) {
            args[1].toIntOrNull() ?: run {
                sender.sendMessage("${ChatColor.RED}페이지가 올바르지 않습니다.")
                return
            }
        } else {
            1
        }

        val itemsPerPage = 8
        val totalAbilities = AbilityRegistry.getTypeCount()
        val maxPage = max(1, (totalAbilities + itemsPerPage - 1) / itemsPerPage)

        if (page !in 1..maxPage) {
            sender.sendMessage("${ChatColor.RED}페이지 범위를 벗어났습니다. (1~$maxPage)")
            return
        }

        sender.sendMessage("${ChatColor.GOLD}==== 능력 목록 ====")
        sender.sendMessage("${ChatColor.AQUA}페이지 $page/$maxPage (총 ${totalAbilities}개 능력)")

        val types = AbilityRegistry.getAllTypes()
        val startIndex = (page - 1) * itemsPerPage
        val endIndex = min(startIndex + itemsPerPage, totalAbilities)

        for (i in startIndex until endIndex) {
            val type = types[i]
            sender.sendMessage("${ChatColor.GREEN}${type.name} ${ChatColor.GRAY}${type.rank}")
        }

        if (totalAbilities == 0) {
            sender.sendMessage("${ChatColor.YELLOW}등록된 능력이 없습니다.")
        }
    }

    private fun handleAssign(sender: CommandSender, args: Array<String>) {
        if (args.size != 3) {
            sender.sendMessage("${ChatColor.RED}명령이 올바르지 않습니다. [/va assign [플레이어] [능력이름]]")
            sender.sendMessage("${ChatColor.RED}띄어쓰기가 포함된 능력은 _로 대체합니다. (예: 갓_에넬)")
            return
        }

        val target = Bukkit.getServer().getPlayerExact(args[1])
        if (target == null) {
            sender.sendMessage("${ChatColor.RED}존재하지 않는 플레이어입니다.")
            return
        }

        val abilityName = args[2].replace('_', ' ')
        GameUtils.assignAbility(sender, abilityName, target, plugin.configManager.isAbilityOverLap)
    }

    private fun handleReset(sender: CommandSender, args: Array<String>) {
        if (args.size != 2) {
            sender.sendMessage("${ChatColor.RED}명령이 올바르지 않습니다. [/va reset [플레이어]]")
            return
        }

        val target = Bukkit.getServer().getPlayerExact(args[1])
        if (target == null) {
            sender.sendMessage("${ChatColor.RED}존재하지 않는 플레이어입니다.")
            return
        }

        AbilityRegistry.deactivateAll(target)
        target.sendMessage("${ChatColor.RED}당신의 능력이 모두 해제되었습니다.")
        sender.sendMessage("${ChatColor.GREEN}${target.name}${ChatColor.WHITE}님의 능력을 모두 해제했습니다.")
    }
}
