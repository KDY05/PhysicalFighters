package io.github.kdy05.physicalFighters.game

import io.github.kdy05.physicalFighters.BuildConfig
import io.github.kdy05.physicalFighters.PhysicalFighters
import io.github.kdy05.physicalFighters.util.TimerBase
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID

class GameManager(private val plugin: PhysicalFighters) {

    private val abilityDistributor = AbilityDistributor()

    // Game state
    var scenario: ScriptStatus = ScriptStatus.NoPlay
        private set
    private val exceptionList = mutableListOf<UUID>()
    private val playerList = mutableListOf<UUID>()
    private val okSign = mutableListOf<UUID>()

    // Timers
    private val gameReadyTimer = GameTimer(TimerType.READY)
    private val gameStartTimer = GameTimer(TimerType.START)
    private val gameProgressTimer = GameTimer(TimerType.PROGRESS)
    private val gameWarningTimer = GameTimer(TimerType.WARNING)

    val gameTime: Int get() = gameProgressTimer.count

    private enum class TimerType {
        READY, START, PROGRESS, WARNING
    }

    enum class ScriptStatus {
        NoPlay, ScriptStart, AbilitySelect, GameStart
    }

    // =========================== Public API ===========================

    fun gameReady(sender: CommandSender) {
        if (scenario != ScriptStatus.NoPlay) {
            sender.sendMessage("${ChatColor.RED}(!) 이미 게임이 시작되었습니다.")
            return
        }
        scenario = ScriptStatus.ScriptStart
        broadcastMessage("${ChatColor.YELLOW}(!) 잠시 후 게임을 시작합니다.")
        gameReadyTimer.startTimer(READY_DURATION, false)
    }

    fun startGame() {
        gameStartTimer.startTimer(COUNTDOWN_DURATION, false)
    }

    fun forceGameStart() {
        okSign.clear()
        okSign.addAll(playerList)
        startGame()
    }

    fun stopGame() {
        scenario = ScriptStatus.NoPlay
        gameReadyTimer.stopTimer()
        gameStartTimer.stopTimer()
        gameProgressTimer.stopTimer()
        gameWarningTimer.endTimer()
        plugin.invincibilityManager.forceStop()
        okSign.clear()
        playerList.clear()
    }

    // Player actions

    fun handleObserve(player: Player) {
        if (scenario != ScriptStatus.NoPlay) {
            player.sendMessage("${ChatColor.RED}게임 시작 이후는 옵저버 처리가 불가능합니다.")
            return
        }
        val uuid = player.uniqueId
        if (uuid in exceptionList) {
            playerList.add(uuid)
            exceptionList.remove(uuid)
            player.sendMessage("${ChatColor.GREEN}게임 예외 처리가 해제되었습니다.")
        } else {
            exceptionList.add(uuid)
            playerList.remove(uuid)
            player.sendMessage("${ChatColor.GOLD}게임 예외 처리가 완료되었습니다.")
            player.sendMessage("${ChatColor.GREEN}/va ob을 다시 사용하시면 해제됩니다.")
        }
    }

    fun handleYes(player: Player) {
        if (isValidAbilitySelection(player)) {
            confirmPlayerAbility(player)
            checkAllPlayersConfirmed()
        }
    }

    fun handleNo(player: Player) {
        if (isValidAbilitySelection(player)) {
            if (abilityDistributor.reassignRandomAbility(player, playerList.size)) {
                GameUtils.showInfo(player, plugin.configManager.isAbilityOverLap)
                confirmPlayerAbility(player)
                checkAllPlayersConfirmed()
            } else {
                player.sendMessage("${ChatColor.RED}(!) 능력의 개수가 부족하여 재추첨이 불가합니다.")
            }
        }
    }

    // =========================== Private Helper Methods ===========================

    private fun isValidAbilitySelection(player: Player): Boolean =
        scenario == ScriptStatus.AbilitySelect &&
            player.uniqueId !in exceptionList &&
            player.uniqueId !in okSign

    private fun confirmPlayerAbility(player: Player) {
        okSign.add(player.uniqueId)
        player.sendMessage("${ChatColor.GOLD}능력이 확정되었습니다. 다른 플레이어를 기다려주세요.")
        broadcastMessage("${ChatColor.YELLOW}${player.name}${ChatColor.WHITE}님이 능력을 확정했습니다.")
        broadcastMessage("${ChatColor.GREEN}남은 인원 : ${ChatColor.WHITE}${playerList.size - okSign.size}명")
    }

    private fun checkAllPlayersConfirmed() {
        if (okSign.size == playerList.size) {
            startGame()
        }
    }

    private fun initializePlayerList() {
        playerList.clear()
        okSign.clear()

        broadcastMessage("${ChatColor.AQUA}인식된 플레이어 목록")
        broadcastMessage("${ChatColor.GOLD}==========")

        var index = 0
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.uniqueId in exceptionList) continue
            playerList.add(player.uniqueId)
            broadcastMessage("${ChatColor.GREEN}$index. ${ChatColor.WHITE}${player.name}")
            index++
        }

        broadcastMessage("${ChatColor.YELLOW}총 인원수 : ${playerList.size}명")
        broadcastMessage("${ChatColor.GOLD}==========")

        if (playerList.isEmpty()) {
            broadcastMessage("${ChatColor.RED}경고, 실질 플레이어가 없습니다. 게임 강제 종료.")
            scenario = ScriptStatus.NoPlay
            gameReadyTimer.stopTimer()
            broadcastMessage("${ChatColor.GRAY}모든 설정이 취소됩니다.")
        }
    }

    private fun showGameInfo(full: Boolean) {
        broadcastMessage("${ChatColor.DARK_RED}Physical Fighters")
        broadcastMessage("${ChatColor.GRAY}VER. ${BuildConfig.BUILD_NUMBER}")
        if (full) {
            broadcastMessage("${ChatColor.GREEN}제작: ${ChatColor.WHITE}어라랍, 염료")
            broadcastMessage("${ChatColor.GREEN}원작(VisualAbility): ${ChatColor.WHITE}제온")
            broadcastMessage("${ChatColor.AQUA}공식 배포처: ${ChatColor.WHITE}https://github.com/KDY05/PhysicalFighters")
            broadcastMessage("${ChatColor.AQUA}원작자 카페: ${ChatColor.WHITE}https://cafe.naver.com/craftproducer")
        }
    }

    private fun handleAbilitySetup() {
        if (!plugin.configManager.isNoAbilitySetting) {
            broadcastMessage("${ChatColor.GRAY}능력 설정 초기화 및 추첨 준비...")
            abilityDistributor.resetAllAbilities()
        } else {
            broadcastMessage("${ChatColor.GOLD}능력을 추첨하지 않습니다.")
            broadcastMessage("시작전에 능력이 이미 부여되었다면 보존됩니다.")
            okSign.clear()
            okSign.addAll(playerList)
            abilityDistributor.enableAllAbilities()
            startGame()
        }
    }

    private fun processAbilitySelection() {
        scenario = ScriptStatus.AbilitySelect
        distributeAbilitiesWithChoice()
        gameWarningTimer.startTimer(MAX_TIMER_DURATION, false)
    }

    private fun distributeAbilitiesWithChoice() {
        val players = onlinePlayers()
        if (!abilityDistributor.distributeAbilities(players)) {
            broadcastMessage("${ChatColor.RED}경고, 할당 가능한 능력이 없습니다.")
            return
        }
        for (player in players) {
            player.sendMessage("${ChatColor.YELLOW}(!) /va check ${ChatColor.WHITE}= 능력 확인")
            player.sendMessage("${ChatColor.YELLOW}(!) /va yes ${ChatColor.WHITE}= 능력 사용.")
            player.sendMessage("${ChatColor.YELLOW}(!) /va no ${ChatColor.WHITE}= 능력 재추첨.(1회)")
        }
        for (uuid in exceptionList) {
            Bukkit.getPlayer(uuid)?.sendMessage("${ChatColor.GREEN}능력 추첨중입니다")
        }
    }

    private fun startGameLogic() {
        broadcastMessage("${ChatColor.GREEN}게임이 시작되었습니다.")
        plugin.invincibilityManager.startInvincibility(plugin.configManager.earlyInvincibleTime)
        setPlayerBase()
        abilityDistributor.enableAllAbilities()
        gameProgressTimer.startTimer(MAX_TIMER_DURATION, false)
    }

    private fun setPlayerBase() {
        for (uuid in playerList) {
            val player = Bukkit.getPlayer(uuid) ?: continue
            player.health = 20.0
            player.foodLevel = 20
            player.saturation = 10.0f
            player.exhaustion = 0.0f
            player.level = plugin.configManager.setLev
            if (plugin.configManager.isClearInventory) {
                player.inventory.clear()
            }
            plugin.baseKitManager.giveBasicItems(player)
        }
    }

    private fun showWarningMessage() {
        for (uuid in playerList) {
            if (uuid !in okSign) {
                val name = Bukkit.getPlayer(uuid)?.name ?: continue
                broadcastMessage("${ChatColor.YELLOW}${name}님의 능력이 확정되지 않았습니다.")
            }
        }
        broadcastMessage("${ChatColor.RED}경고, 게임이 올바르게 시작되지 않았습니다.")
        broadcastMessage("${ChatColor.RED}/va yes나 /va no 명령으로 능력을 확정하세요.")
    }

    private fun showPeriodicInfo(count: Int) {
        if (count > 0 && count % PROGRESS_INFO_INTERVAL == 0) {
            showGameInfo(false)
        }
    }

    private fun onlinePlayers(): List<Player> =
        playerList.mapNotNull { Bukkit.getPlayer(it) }

    private fun broadcastMessage(message: String) {
        Bukkit.broadcastMessage(message)
    }

    // =========================== Timer Class ===========================

    private inner class GameTimer(private val type: TimerType) : TimerBase() {

        override fun onTimerStart() {
            if (type == TimerType.START) {
                scenario = ScriptStatus.GameStart
            }
        }

        override fun onTimerRunning(count: Int) {
            when (type) {
                TimerType.READY -> handleReadyTimer(count)
                TimerType.START -> handleStartTimer(count)
                TimerType.PROGRESS -> handleProgressTimer(count)
                TimerType.WARNING -> handleWarningTimer(count)
            }
        }

        override fun onTimerEnd() {}

        private fun handleReadyTimer(count: Int) {
            when (count) {
                0 -> initializePlayerList()
                3 -> showGameInfo(true)
                7 -> handleAbilitySetup()
                9 -> processAbilitySelection()
            }
        }

        private fun handleStartTimer(count: Int) {
            when (count) {
                0 -> gameWarningTimer.endTimer()
                3 -> broadcastMessage("${ChatColor.WHITE}모든 플레이어들의 능력을 확정했습니다.")
                5 -> broadcastMessage("${ChatColor.YELLOW}잠시 후 게임이 시작됩니다.")
                in 10..14 -> broadcastMessage("${ChatColor.GOLD}${15 - count}초 전")
                15 -> startGameLogic()
            }
        }

        private fun handleProgressTimer(count: Int) {
            showPeriodicInfo(count)
        }

        private fun handleWarningTimer(count: Int) {
            if (count > 0 && count % WARNING_INTERVAL == 0) {
                showWarningMessage()
            }
        }
    }

    companion object {
        private const val COUNTDOWN_DURATION = 15
        private const val READY_DURATION = 9
        private const val WARNING_INTERVAL = 20
        private const val PROGRESS_INFO_INTERVAL = 600
        private const val MAX_TIMER_DURATION = 99999999
    }
}
