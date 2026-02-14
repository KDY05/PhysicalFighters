package io.github.kdy05.physicalFighters.util

import io.github.kdy05.physicalFighters.PhysicalFighters
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

abstract class TimerBase {
    private var task: BukkitTask? = null
    private var reverse = false
    private var maxCount = 0

    var count: Int = 0
        private set

    val isRunning: Boolean
        get() = task != null

    abstract fun onTimerStart()
    abstract fun onTimerRunning(count: Int)
    abstract fun onTimerEnd()
    open fun onTimerFinalize() {}

    fun startTimer(maxCount: Int, reverse: Boolean) {
        if (isRunning) return

        this.maxCount = maxCount
        this.reverse = reverse
        count = if (reverse) maxCount else 0

        task = object : BukkitRunnable() {
            override fun run() {
                onTimerRunning(count)
                val done = if (this@TimerBase.reverse) count <= 0 else count >= this@TimerBase.maxCount
                if (done) {
                    endTimer()
                } else {
                    count += if (this@TimerBase.reverse) -1 else 1
                }
            }
        }.runTaskTimer(PhysicalFighters.getPlugin(), 0L, 20L)

        onTimerStart()
    }

    fun endTimer() {
        if (!isRunning) return
        stopTimer()
        onTimerEnd()
    }

    fun stopTimer() {
        task?.cancel()
        task = null
        count = 0
        onTimerFinalize()
    }
}