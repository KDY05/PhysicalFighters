package physicalFighters.scripts;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.AbilityList;
import physicalFighters.utils.TimerBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class S_GameWarnning {
    private final S_ScriptTimer stimer = new S_ScriptTimer();
    private final MainScripter ms;

    public S_GameWarnning(MainScripter ms) {
        this.ms = ms;
    }

    public void GameWarnningStart() {
        this.stimer.StartTimer(99999999);
    }

    public void GameWarnningStop() {
        this.stimer.EndTimer();
    }

    public final class S_ScriptTimer extends TimerBase {
        public S_ScriptTimer() {
        }

        public void EventStartTimer() {
        }

        public void EventRunningTimer(int count) {
            if ((count >= 20) && (count % 20 == 0)) {
                Bukkit.broadcastMessage(ChatColor.RED +
                        "경고, 게임이 올바르게 시작되지 않았습니다.");
                Bukkit.broadcastMessage(ChatColor.RED +
                        "/va yes나 /va no 명령으로 능력을 확정하세요.");
                for (int l = 0; l < AbilityList.AbilityList.size(); l++) {
                    if (AbilityList.AbilityList.get(l).getPlayer() != null) {
                        AbilityBase tempab = AbilityList.AbilityList.get(l);
                        if (!S_GameWarnning.this.ms.OKSign.contains(tempab.getPlayer())) {
                            tempab.getPlayer().sendMessage(
                                    "당신의 능력이 올바르게 확정되지 않았습니다.");
                        }
                    }
                }
            }
        }

        public void EventEndTimer() {
        }
    }
}
