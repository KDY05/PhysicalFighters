package io.github.kdy05.physicalFighters.command

import io.github.kdy05.physicalFighters.PhysicalFighters
import io.github.kdy05.physicalFighters.ability.AbilityRegistry
import io.github.kdy05.physicalFighters.config.ConfigManager
import io.github.kdy05.physicalFighters.util.AbilityBook
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class UtilCommand(
    private val plugin: PhysicalFighters,
    private val configManager: ConfigManager
) : CommandInterface {

    override fun onCommandEvent(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("va.operate")) return false

        when (args[0].lowercase()) {
            "util" -> handleUtil(sender)
            "inv" -> handleInv(sender, args)
            "hung" -> handleHung()
            "dura" -> handleDura()
            "tc" -> handleTc(sender)
            "book" -> handleBook(sender, args)
            "scan" -> handleScan(sender)
            "reload" -> handleReload(sender)
            "kit" -> handleKit(sender, args)
            else -> return false
        }
        return true
    }

    private fun handleUtil(sender: CommandSender) {
        sender.sendMessage("${ChatColor.AQUA}=== 유틸리티 명령어 목록 ===")
        sender.sendMessage("")

        sender.sendMessage("${ChatColor.YELLOW}■ 게임 설정")
        sender.sendMessage("${ChatColor.GOLD}/va reload${ChatColor.WHITE} - 플러그인 설정(config.yml)을 다시 로드합니다.")
        sender.sendMessage("${ChatColor.GOLD}/va kit${ChatColor.WHITE} - 게임 시작 시 기본템을 설정합니다.")
        sender.sendMessage("${ChatColor.GOLD}/va inv [시간(분)]${ChatColor.WHITE} - 무적 모드를 토글하거나 지정 시간동안 무적을 시작합니다.")
        sender.sendMessage("${ChatColor.GOLD}/va hung${ChatColor.WHITE} - 배고픔 무한 모드를 토글합니다.")
        sender.sendMessage("${ChatColor.GOLD}/va dura${ChatColor.WHITE} - 내구도 무한 모드를 토글합니다.")
        sender.sendMessage("")

        sender.sendMessage("${ChatColor.YELLOW}■ 기타")
        sender.sendMessage("${ChatColor.GOLD}/va scan${ChatColor.WHITE} - 현재 능력자 목록을 확인합니다.")
        sender.sendMessage("${ChatColor.GOLD}/va tc${ChatColor.WHITE} - 모든 능력의 쿨타임과 지속시간을 초기화합니다.")
        sender.sendMessage("${ChatColor.GOLD}/va book [코드]${ChatColor.WHITE} - 능력 정보가 담긴 책을 생성합니다.")
        sender.sendMessage("")

        sender.sendMessage("${ChatColor.AQUA}==========================")
    }

    private fun handleInv(sender: CommandSender, args: Array<String>) {
        when (args.size) {
            1 -> plugin.invincibilityManager.toggle()
            2 -> {
                val minutes = args[1].toIntOrNull()
                if (minutes == null || minutes <= 0) {
                    sender.sendMessage("${ChatColor.RED}시간은 1 이상의 숫자를 입력하세요.")
                    return
                }
                plugin.invincibilityManager.startInvincibility(minutes)
            }
            else -> sender.sendMessage("${ChatColor.RED}명령어 사용법: /va inv [시간(분)]")
        }
    }

    private fun handleHung() {
        configManager.isNoFoodMode = !configManager.isNoFoodMode
        if (configManager.isNoFoodMode) {
            Bukkit.broadcastMessage("${ChatColor.GREEN}OP에 의해 배고픔무한이 설정되었습니다.")
        } else {
            Bukkit.broadcastMessage("${ChatColor.RED}OP에 의해 배고픔무한이 해제되었습니다.")
        }
    }

    private fun handleDura() {
        configManager.isInfinityDur = !configManager.isInfinityDur
        if (configManager.isInfinityDur) {
            Bukkit.broadcastMessage("${ChatColor.GREEN}OP에 의해 내구도무한이 설정되었습니다.")
        } else {
            Bukkit.broadcastMessage("${ChatColor.RED}OP에 의해 내구도무한이 해제되었습니다.")
        }
    }

    private fun handleTc(sender: CommandSender) {
        for (ability in AbilityRegistry.getActiveAbilities()) {
            ability.cancelDTimer()
            ability.cancelCTimer()
        }
        Bukkit.broadcastMessage("${ChatColor.GRAY}관리자 ${sender.name}님이 쿨타임 및 지속시간을 초기화했습니다.")
    }

    private fun handleBook(sender: CommandSender, args: Array<String>) {
        if (filterConsole(sender)) return
        val player = sender as Player

        if (args.size != 2) {
            player.sendMessage("${ChatColor.RED}명령이 올바르지 않습니다. [/va book [능력이름]]")
            return
        }

        val abilityName = args[1]
        val stack = AbilityBook.create(abilityName)
        if (stack == null) {
            player.sendMessage("${ChatColor.RED}존재하지 않는 능력입니다.")
            return
        }

        player.inventory.addItem(stack)
        player.sendMessage("능력서를 만들었습니다. ${ChatColor.GOLD}$abilityName")
    }

    private fun handleScan(sender: CommandSender) {
        sender.sendMessage("${ChatColor.GOLD}- 능력을 스캔했습니다. -")
        sender.sendMessage("${ChatColor.GREEN}---------------")
        var count = 0
        for (ability in AbilityRegistry.getActiveAbilities()) {
            val player = ability.player ?: continue
            sender.sendMessage(
                "${ChatColor.GREEN}$count. ${ChatColor.WHITE}${player.name} : " +
                    "${ChatColor.RED}${ability.abilityName} ${ChatColor.WHITE}[${ability.abilityType}]"
            )
            count++
        }
        if (count == 0) {
            sender.sendMessage("아직 능력자가 없습니다.")
        }
        sender.sendMessage("${ChatColor.GREEN}---------------")
    }

    private fun handleReload(sender: CommandSender) {
        try {
            configManager.reloadConfigs()
            sender.sendMessage("${ChatColor.GREEN}플러그인 설정이 성공적으로 다시 로드되었습니다.")
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}설정 로드 중 오류가 발생했습니다: ${e.message}")
            plugin.logger.warning("설정 로드 실패: ${e.message}")
        }
    }

    private fun handleKit(sender: CommandSender, args: Array<String>) {
        if (filterConsole(sender)) return
        if (args.size == 2) {
            val code = args[1].toIntOrNull()
            if (code == null) {
                sender.sendMessage("${ChatColor.RED}코드가 올바르지 않습니다.")
                return
            }
            plugin.baseKitManager.setKitbyPreset(code)
        }
        plugin.baseKitManager.openBasicItemGUI(sender as Player)
    }
}
