package physicalFighters.timers;

import physicalFighters.core.AbilityBase;
import physicalFighters.utils.ACC;
import physicalFighters.utils.TimerBase;
import org.bukkit.ChatColor;

public final class DurationTimer extends TimerBase {
    private final AbilityBase ab;
    private final CoolDownTimer ctimer;

    public DurationTimer(AbilityBase ab, CoolDownTimer ctimer) {
        this.ab = ab;
        this.ctimer = ctimer;
    }

    public void EventStartTimer() {
        this.ab.A_DurationStart();
    }

    public void EventRunningTimer(int count) {
        if (((count <= 3) && (count >= 1) && (this.ab.getShowText() == AbilityBase.ShowText.All_Text)) || (this.ab.getShowText() == AbilityBase.ShowText.No_CoolDownText)) {
            this.ab.getPlayer().sendMessage(String.format(ChatColor.GREEN + "지속 시간" + ChatColor.WHITE + " %d초 전", count));
        }
    }

    public void EventEndTimer() {
        this.ab.A_DurationEnd();
        if (this.ab.getShowText() != AbilityBase.ShowText.Custom_Text)
            this.ab.getPlayer().sendMessage(ACC.DefaultDurationEnd);
        this.ctimer.StartTimer(this.ab.getCoolDown(), true);
    }

    public void FinalEventEndTimer() {
        this.ab.A_FinalDurationEnd();
    }
}
