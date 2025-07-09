package physicalFighters.timers;

import physicalFighters.utils.TimerBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class RestrictionTimer extends TimerBase {
    public void EventStartTimer() {
    }

    public void EventRunningTimer(int count) {
    }

    public void EventEndTimer() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "일부 능력의 제약이 해제되었습니다.");
    }
}
