package Physical.Fighters.MajorModule;

import Physical.Fighters.MainModule.AbilityBase;
import org.bukkit.ChatColor;

public final class CoolDownTimer extends Physical.Fighters.MinerModule.TimerBase {
    private final AbilityBase ab;

    public CoolDownTimer(AbilityBase ab) {
        this.ab = ab;
    }

    public void EventStartTimer() {
        this.ab.A_CoolDownStart();
    }

    public void EventRunningTimer(int count) {
        if (((count <= 3) && (count >= 1) && (this.ab.GetShowText() == AbilityBase.ShowText.All_Text)) || (this.ab.GetShowText() == AbilityBase.ShowText.No_DurationText)) {
            this.ab.GetPlayer().sendMessage(String.format(ChatColor.RED + "%d초 뒤" + ChatColor.WHITE + " 능력사용이 가능합니다.", count));
        }
    }

    public void EventEndTimer() {
        this.ab.A_CoolDownEnd();
        if (this.ab.GetShowText() != AbilityBase.ShowText.Custom_Text) {
            this.ab.GetPlayer().sendMessage(Physical.Fighters.MinerModule.ACC.DefaultCoolDownEnd);
        }
    }
}
