package io.github.kdy05.physicalFighters.utils;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import io.github.kdy05.physicalFighters.PhysicalFighters;

public abstract class TimerBase {
    private BukkitTask task;
    private boolean running = false;
    private boolean reverse = false;
    private int count = 0;
    private int maxCount = 0;

    public abstract void EventStartTimer();

    public abstract void EventRunningTimer(int count);

    public abstract void EventEndTimer();

    public void FinalEventEndTimer() {
    }

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
        running = true;
        this.maxCount = maxCount;
        this.reverse = reverse;
        count = reverse ? maxCount : 0;
        
        task = new BukkitRunnable() {
            @Override
            public void run() {
                EventRunningTimer(count);
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
        
        EventStartTimer();
    }

    public final void endTimer() {
        stopTimer();
        EventEndTimer();
    }

    public final void stopTimer() {
        if (task != null)
            task.cancel();
        count = 0;
        running = false;
        FinalEventEndTimer();
    }

}
