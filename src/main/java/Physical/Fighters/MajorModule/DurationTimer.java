package Physical.Fighters.MajorModule;

import Physical.Fighters.MainModule.AbilityBase;
import org.bukkit.ChatColor;

public final class DurationTimer extends Physical.Fighters.MinerModule.TimerBase {
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
        if (((count <= 3) && (count >= 1) && (this.ab.GetShowText() == AbilityBase.ShowText.All_Text)) || (this.ab.GetShowText() == AbilityBase.ShowText.No_CoolDownText)) {
            this.ab.GetPlayer().sendMessage(String.format(ChatColor.GREEN + "지속 시간" + ChatColor.WHITE + " %d초 전", count));
        }
    }

    public void EventEndTimer() {
        this.ab.A_DurationEnd();
        if (this.ab.GetShowText() != AbilityBase.ShowText.Custom_Text)
            this.ab.GetPlayer().sendMessage(Physical.Fighters.MinerModule.ACC.DefaultDurationEnd);
        this.ctimer.StartTimer(this.ab.GetCoolDown(), true);
    }

    public void FinalEventEndTimer() {
        this.ab.A_FinalDurationEnd();
    }
}
