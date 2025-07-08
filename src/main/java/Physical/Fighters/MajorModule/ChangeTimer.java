package Physical.Fighters.MajorModule;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MinerModule.AUC;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class ChangeTimer {
    public static Timer ct = new Timer();

    public static void start() {
        ct.schedule(new TimerTask() {
            public void run() {
                Bukkit.broadcastMessage(ChatColor.RED + "모든 플레이어의 능력이 랜덤으로 재추첨됩니다.");
                Player[] pl = Bukkit.getOnlinePlayers().toArray(new Player[0]);
                for (AbilityBase ab : AbilityList.AbilityList) {
                    ab.SetPlayer(null, true);
                }
                Random r = new Random();
                Player[] arrayOfPlayer1;
                int j = (arrayOfPlayer1 = pl).length;
                for (int k = 0; k < j; k++) {
                    Player p = arrayOfPlayer1[k];
                    AbilityBase a;
                    do {
                        int i;
                        do {
                            i = r.nextInt(AbilityList.AbilityList.size());
                        } while (i == 0);
                        a = AbilityList.AbilityList.get(i);
                    } while (a.GetPlayer() != null);
                    a.SetPlayer(p, true);
                    a.SetRunAbility(true);
                    AUC.InfoTextOut(p);
                }
            }
        }, 600000L, 600000L);
    }
}
