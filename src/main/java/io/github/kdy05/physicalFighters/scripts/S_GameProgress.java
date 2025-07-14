package io.github.kdy05.physicalFighters.scripts;

import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.TimerBase;
import io.github.kdy05.physicalFighters.PhysicalFighters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class S_GameProgress {

    private final S_ScriptTimer stimer = new S_ScriptTimer();
    private final int EarlyInvincibleTime = PhysicalFighters.EarlyInvincibleTime * 60;

    public void GameProgress() {
        this.stimer.startTimer(99999999, false);
    }

    public void GameProgressStop() {
        this.stimer.stopTimer();
    }

    public final class S_ScriptTimer extends TimerBase {

        public void EventStartTimer() {
        }

        public void EventRunningTimer(int count) {
            if (PhysicalFighters.PrintTip)
                printTip(count);
            if (count > 0 && count % 600 == 0) {
                Bukkit.broadcastMessage(ChatColor.DARK_RED + "Physical Fighters");
                Bukkit.broadcastMessage(ChatColor.GRAY + "빌드 넘버 " + PhysicalFighters.BuildNumber);
            }
            if (PhysicalFighters.EarlyInvincibleTime == 0) return;
            int remainingSeconds = S_GameProgress.this.EarlyInvincibleTime - count;
            if (remainingSeconds == 0) {
                Bukkit.broadcastMessage(ChatColor.GREEN + "초반 무적이 해제되었습니다. 이제 데미지를 입습니다.");
                EventManager.DamageGuard = false;
            } else if (remainingSeconds <= 5 && remainingSeconds >= 1) {
                Bukkit.broadcastMessage(String.format(ChatColor.YELLOW +
                        "%d초 후" + ChatColor.WHITE + " 초반무적이 해제됩니다.", remainingSeconds));
            } else if (remainingSeconds == 60) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "초반 무적이 " + ChatColor.WHITE + "1분 후 해제됩니다.");
            }
        }

        public void EventEndTimer() {
        }

        public void printTip(int c) {
            switch (c) {
                case 60:
                    Bukkit.broadcastMessage(ChatColor.GOLD + "TIP. 본 플러그인은 제온님이 배포한 VisualAbility의 모듈을 사용합니다.\n" +
                            "http://cafe.naver.com/craftproducer 제온님이 운영하는 카페입니다.");
                    break;
                case 120:
                    Bukkit.broadcastMessage(ChatColor.GOLD + "TIP. 액티브 능력은 철괴나 금괴를 이용해 사용하며,\n" +
                            "패시브 능력은 사용할 필요 없이 자동으로 능력이 적용됩니다.");
                    break;
                case 180:
                    Bukkit.broadcastMessage(ChatColor.GOLD + "TIP. 불편 및 건의 사항은 디스코드 @kdy05_로 문의 해주세요.");
                    break;
            }
        }
    }

}
