package Physical.Fighters.Script;

import Physical.Fighters.MinerModule.TimerBase;
import Physical.Fighters.PhysicalFighters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class S_GameProgress {
    private final MainScripter ms;
    private final S_ScriptTimer stimer = new S_ScriptTimer();
    private final int EarlyInvincibleTime;
    private boolean gcon = false;

    public S_GameProgress(MainScripter ms) {
        this.ms = ms;
        this.EarlyInvincibleTime = (PhysicalFighters.EarlyInvincibleTime * 60);
    }

    public void GameProgress() {
        this.stimer.StartTimer(99999999);
    }

    public void GameProgressStop() {
        this.gcon = false;
        this.stimer.StopTimer();
    }

    public final class S_ScriptTimer extends TimerBase {
        public S_ScriptTimer() {
        }

        public void EventStartTimer() {
        }

        public void EventRunningTimer(int count) {
            if (PhysicalFighters.PrintTip) {
                PrintTip(count);
            }
            if ((count > 20) && (count % 15 == 0)) {
                S_GameProgress.this.ms.gameworld.setStorm(false);
                if (S_GameProgress.this.gcon) {
                    System.gc();
                }
            }
            if ((count > 20) && (count % 600 == 0)) {
                Bukkit.broadcastMessage(String.format(ChatColor.DARK_RED +
                        "Physical Fighters"));
                Bukkit.broadcastMessage(String.format(ChatColor.GRAY +
                        "빌드 넘버 : %d", PhysicalFighters.BuildNumber));
            }
            if ((PhysicalFighters.Invincibility) && (S_GameProgress.this.EarlyInvincibleTime > 60) &&
                    (S_GameProgress.this.EarlyInvincibleTime - 60 == count)) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "초반 무적이 " +
                        ChatColor.WHITE + "1분후 해제됩니다.");
            }
            if ((PhysicalFighters.Invincibility) &&
                    (S_GameProgress.this.EarlyInvincibleTime - 5 == count)) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "5초 후" +
                        ChatColor.WHITE + " 초반무적이 해제됩니다.");
            }
            if ((PhysicalFighters.Invincibility) &&
                    (S_GameProgress.this.EarlyInvincibleTime - 4 == count)) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "4초 후" +
                        ChatColor.WHITE + " 초반무적이 해제됩니다.");
            }
            if ((PhysicalFighters.Invincibility) &&
                    (S_GameProgress.this.EarlyInvincibleTime - 3 == count)) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "3초 후" +
                        ChatColor.WHITE + " 초반무적이 해제됩니다.");
            }
            if ((PhysicalFighters.Invincibility) &&
                    (S_GameProgress.this.EarlyInvincibleTime - 2 == count)) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "2초 후" +
                        ChatColor.WHITE + " 초반무적이 해제됩니다.");
            }
            if ((PhysicalFighters.Invincibility) &&
                    (S_GameProgress.this.EarlyInvincibleTime - 1 == count)) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "1초 후" +
                        ChatColor.WHITE + " 초반무적이 해제됩니다.");
            }
            if ((PhysicalFighters.Invincibility) && (S_GameProgress.this.EarlyInvincibleTime == count)) {
                Bukkit.broadcastMessage(ChatColor.GREEN +
                        "초반 무적이 해제되었습니다. 이제 데미지를 입습니다.");
                Physical.Fighters.MainModule.EventManager.DamageGuard = false;
                Physical.Fighters.MainModule.AbilityBase.restrictionTimer.StartTimer();
            }
        }

        public void EventEndTimer() {
        }

        public void PrintTip(int c) {
            switch (c) {
                case 30:
                    Bukkit.broadcastMessage("TIP. 본 플러그인은 제온님이 배포하신 VisualAbility의 모듈을 사용하고있습니다.");
                    break;
                case 60:
                    Bukkit.broadcastMessage("TIP. http://cafe.naver.com/craftproducer/1165 이 카페 정말 좋아요");
                    break;
                case 120:
                    Bukkit.broadcastMessage("TIP. 그저 게임을 재밌게 즐겨주셨으면 합니다.");
                    break;
                case 150:
                    Bukkit.broadcastMessage("TIP. 패시브 능력은 가만히 있으셔도 능력이 적용됩니다.");
                    break;
                case 160:
                    Bukkit.broadcastMessage("TIP. 액티브 능력은 철괴나 금괴를 이용해 적용 하실 수 있습니다.");
                    break;
                case 190:
                    Bukkit.broadcastMessage("TIP. 게임 시작 후 10분마다 플레이어들의 좌표가 공개됩니다.");
                    break;
                case 250:
                    Bukkit.broadcastMessage("TIP. 본 플러그인을 이용하시면서 불편한점이나 건의사항은 스카이프 ApplepieMod로 건의해주세요.");
                    break;
                case 310:
                    Bukkit.broadcastMessage("TIP. 낙법이란 낙하데미지를 받지 않기위해 나갔다 들어오는 것을 말합니다.");
                    break;
                case 340:
                    Bukkit.broadcastMessage("TIP. 싸움 도중에 나갈시에 경고없이 밴을 당하실 수 있습니다.");
            }
        }
    }
}
