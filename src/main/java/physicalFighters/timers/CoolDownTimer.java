package physicalFighters.timers;

import physicalFighters.core.AbilityBase;
import physicalFighters.utils.ACC;
import physicalFighters.utils.TimerBase;
import org.bukkit.ChatColor;

public final class CoolDownTimer extends TimerBase {
    private final AbilityBase ab;

    public CoolDownTimer(AbilityBase ab) {
        this.ab = ab;
    }

    public void EventStartTimer() {
        this.ab.A_CoolDownStart();
    }

    public void EventRunningTimer(int count) {
        if (((count <= 3) && (count >= 1) && (this.ab.getShowText() == AbilityBase.ShowText.All_Text)) || (this.ab.getShowText() == AbilityBase.ShowText.No_DurationText)) {
            this.ab.getPlayer().sendMessage(String.format(ChatColor.RED + "%d초 뒤" + ChatColor.WHITE + " 능력사용이 가능합니다.", count));
        }
    }

    public void EventEndTimer() {
        this.ab.A_CoolDownEnd();
        if (this.ab.getShowText() != AbilityBase.ShowText.Custom_Text) {
            this.ab.getPlayer().sendMessage(ACC.DefaultCoolDownEnd);
        }
    }
}
