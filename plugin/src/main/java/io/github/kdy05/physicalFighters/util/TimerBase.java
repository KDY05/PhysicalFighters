package io.github.kdy05.physicalFighters.util;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import io.github.kdy05.physicalFighters.PhysicalFighters;

public abstract class TimerBase {
    private BukkitTask task;
    private boolean running = false;
    private boolean reverse = false;
    private int count = 0;
    private int maxCount = 0;

    public abstract void onTimerStart();

    public abstract void onTimerRunning(int count);

    public abstract void onTimerEnd();

    public void onTimerFinalize() {}

    public final int getCount() {
        return count;
    }

    public final void setCount(int c) {
        count = c;
    }

    public final boolean isRunning() {
        return running;
    }

    public final void startTimer(int maxCount, boolean reverse) {
        if (running) return;

        running = true;
        this.maxCount = maxCount;
        this.reverse = reverse;
        count = reverse ? maxCount : 0;
        
        task = new BukkitRunnable() {
            @Override
            public void run() {
                onTimerRunning(count);
                if (TimerBase.this.reverse) {
                    if (count <= 0) {
                        endTimer();
                        return;
                    }
                    --count;
                } else {
                    if (count >= TimerBase.this.maxCount) {
                        endTimer();
                        return;
                    }
                    ++count;
                }
            }
        }.runTaskTimer(PhysicalFighters.getPlugin(), 0L, 20L);
        
        onTimerStart();
    }

    public final void endTimer() {
        if (!running) return;
        stopTimer();
        onTimerEnd();
    }

    public final void stopTimer() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        count = 0;
        running = false;
        onTimerFinalize();
    }

}
