package io.github.kdy05.physicalFighters.game

import io.github.kdy05.physicalFighters.PhysicalFighters
import io.github.kdy05.physicalFighters.util.TimerBase
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class InvincibilityManager(plugin: PhysicalFighters) : Listener {

    private var invincibilityBar: BossBar? = null
    private var timer: InvincibilityTimer? = null
    private var isActive = false
    private var customMinutes = 0

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun startInvincibility(minutes: Int) {
        if (minutes <= 0) {
            broadcast("${ChatColor.RED}무적 시간이 0분으로 설정되어 작동하지 않습니다.")
            return
        }
        if (isActive) {
            broadcast("${ChatColor.RED}이미 무적이 활성화되어 있습니다. 먼저 해제하세요.")
            return
        }

        customMinutes = minutes
        isActive = true
        isDamageGuard = true

        invincibilityBar = Bukkit.createBossBar("무적 시간", BarColor.GREEN, BarStyle.SEGMENTED_12).apply {
            progress = 1.0
            Bukkit.getOnlinePlayers().forEach { addPlayer(it) }
        }

        timer = InvincibilityTimer().apply {
            startTimer(minutes * 60, false)
        }

        broadcast("${ChatColor.GREEN}${minutes}분간 무적이 설정되었니다.")
    }

    fun stopInvincibility() {
        if (!isActive) return
        deactivate()
        broadcast("${ChatColor.GREEN}무적이 해제되었습니다. 이제 대미지를 입습니다.")
    }

    fun forceStop() {
        if (!isActive) return
        deactivate()
        broadcast("${ChatColor.GREEN}OP에 의해 무적이 해제되었습니다. 이제 대미지를 입습니다.")
    }

    fun toggle() {
        if (isDamageGuard) {
            if (isActive) {
                forceStop()
            } else {
                isDamageGuard = false
                broadcast("${ChatColor.GREEN}OP에 의해 무적이 해제되었습니다. 이제 대미지를 입습니다.")
            }
        } else {
            isDamageGuard = true
            broadcast("${ChatColor.GREEN}OP에 의해 무적이 설정되었습니다. 이제 대미지를 입지않습니다.")
        }
    }

    private fun deactivate() {
        isActive = false
        isDamageGuard = false
        timer?.stopTimer()
        invincibilityBar?.removeAll()
        invincibilityBar = null
    }

    private fun updateBossBar(remainingSeconds: Int) {
        val bar = invincibilityBar ?: return
        val totalSeconds = customMinutes * 60

        bar.progress = (remainingSeconds.toDouble() / totalSeconds).coerceAtLeast(0.0)
        bar.setTitle("무적 시간 %02d:%02d".format(remainingSeconds / 60, remainingSeconds % 60))
        bar.color = when {
            remainingSeconds <= 30 -> BarColor.RED
            remainingSeconds <= 60 -> BarColor.YELLOW
            else -> bar.color
        }
    }

    private fun broadcast(message: String) = Bukkit.broadcastMessage(message)

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (isActive) invincibilityBar?.addPlayer(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        invincibilityBar?.removePlayer(event.player)
    }

    private inner class InvincibilityTimer : TimerBase() {
        override fun onTimerStart() {}

        override fun onTimerRunning(count: Int) {
            val remaining = customMinutes * 60 - count
            updateBossBar(remaining)

            when (remaining) {
                0 -> stopInvincibility()
                in 1..5 -> broadcast("${ChatColor.YELLOW}${remaining}초 후${ChatColor.WHITE} 무적이 해제됩니다.")
                60 -> broadcast("${ChatColor.YELLOW}무적이 ${ChatColor.WHITE}1분 후 해제됩니다.")
            }
        }

        override fun onTimerEnd() = stopInvincibility()
    }

    companion object {
        @JvmStatic
        var isDamageGuard: Boolean = false
    }
}