package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.Vector;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Pagi extends Ability {
    public Pagi() {
        InitAbility("패기", Type.Active_Immediately, Rank.SS, new String[]{
                "능력 사용시 20초간 10칸 안에 있는 적에게 5초마다 강한데미지를 줍니다."});
        InitAbility(160, 0, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        if (!EventManager.DamageGuard) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem)))
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
        private String name = null;

        public Pauck(String name1) {
            this.name = name1;
        }

        public void run() {
            Player[] p1 = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            Player p = Bukkit.getPlayer(this.name);
            if (p != null) {
                for (int i = 0; i < (Bukkit.getOnlinePlayers()).size(); i++)
                    if ((p1[i] != p) && (p1[i].getGameMode() != GameMode.CREATIVE)) {
                        Location loc = p1[i].getLocation();
                        Location l = p.getLocation();
                        Vector targetvec = new Vector(loc.getX(), loc.getY(), loc.getZ());
                        Vector playervec = new Vector(l.getX(), l.getY(), l.getZ());
                        if ((playervec.distance(targetvec) <= 10.0D) && (loc.getY() != 0.0D)) {
                            p1[i].damage(5, p);
                            p1[i].addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 30, 0), true);
                        }
                    }
                if (this.num > 20) cancel();
                this.num += 1;
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Pagi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */