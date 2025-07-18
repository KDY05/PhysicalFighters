package io.github.kdy05.physicalFighters.scripts;

import io.github.kdy05.physicalFighters.utils.TimerBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class S_GameWarning {
    private final WarningTimer stimer = new WarningTimer();
    private final MainScripter ms;

    public S_GameWarning(MainScripter ms) {
        this.ms = ms;
    }

    public void GameWarnningStart() {
        this.stimer.startTimer(99999999, false);
    }

    public void GameWarnningStop() {
        this.stimer.endTimer();
    }

    public final class WarningTimer extends TimerBase {
        public WarningTimer() {}

        public void onTimerStart() {}

        public void onTimerRunning(int count) {
            if (count > 0 && count % 20 == 0) {
                Bukkit.broadcastMessage(ChatColor.RED + "경고, 게임이 올바르게 시작되지 않았습니다.");
                Bukkit.broadcastMessage(ChatColor.RED + "/va yes나 /va no 명령으로 능력을 확정하세요.");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!S_GameWarning.this.ms.OKSign.contains(player)) {
                        player.sendMessage(ChatColor.YELLOW + "당신의 능력이 올바르게 확정되지 않았습니다.");
                    }
                }
            }
        }

        public void onTimerEnd() {}
    }
}
