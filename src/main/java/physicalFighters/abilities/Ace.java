package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Ace extends Ability {
    public Ace() {
        InitAbility("에이스", Type.Active_Immediately, Rank.S,
                "철괴 왼쪽클릭시 20초간 자신의 주변에 있는 적들을 불태웁니다.");
        InitAbility(40, 0, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((!EventManager.DamageGuard) &&
                (isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem))) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Timer timer = new Timer();
        timer.schedule(new Pauck(Event.getPlayer().getName()), 500L, 1500L);
    }

    class Pauck extends TimerTask {
        private int num = 0;
        private final String name;

        public Pauck(String name1) {
            this.name = name1;
        }

        public void run() {
            Player[] p1 = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            Player p = Bukkit.getPlayer(this.name);
            if (p != null) {
                for (Player player : p1) {
                    if ((player != p) && (player.getGameMode() != GameMode.CREATIVE)) {
                        Location lo = player.getLocation();
                        if ((p.getLocation().distance(player.getLocation()) <= 10.0D) && (lo.getY() != 0.0D))
                            player.setFireTicks(80);
                    }
                }
            }
            if (this.num > 16) cancel();
            this.num += 1;
        }
    }
}
