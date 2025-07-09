package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Sunbi
        extends AbilityBase {
    public Sunbi() {
        InitAbility("나그네", Type.Active_Immediately, Rank.S, new String[]{
                "쉬프트를 누르고 앉으면 지나가던 나그네가 엣헴! 하며 주위의 사람들이 나그네에게 체력을 바칩니다. ",
                "이 떄, 체력을 빼앗긴 플레이어들은 어지러움증과 고통을 느끼게됩니다."});
        InitAbility(30, 0, true);
        EventManager.onPlayerToggleSneakEvent.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerToggleSneakEvent Event = (PlayerToggleSneakEvent) event;
        if ((!EventManager.DamageGuard) &&
                (isOwner(Event.getPlayer())) && (Event.isSneaking())) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
        Player[] arrayOfPlayer;
        int j = (arrayOfPlayer = Bukkit.getOnlinePlayers().toArray(new Player[0])).length;
        for (int i = 0; i < j; i++) {
            Player p = arrayOfPlayer[i];
            if ((p != e.getPlayer()) &&
                    (e.getPlayer().getLocation().distance(p.getLocation()) < 10.0D)) {
                if (((Damageable) p).getHealth() > 2.0D) {
                    p.setHealth(((Damageable) p).getHealth() - 2);
                } else
                    p.damage(1000, e.getPlayer());
                if (((Damageable) e.getPlayer()).getHealth() <= 18) {
                    e.getPlayer().setHealth(((Damageable) e.getPlayer()).getHealth() + 2);
                } else
                    e.getPlayer().setHealth(20);
                p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 3), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 3), true);
                p.sendMessage(ChatColor.WHITE + "<" + e.getPlayer().getDisplayName() + ">엣헴!");
            }
        }
        e.getPlayer().sendMessage(ChatColor.WHITE + "<" + e.getPlayer().getDisplayName() + ">엣헴!");
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
                for (int i = 0; i < (Bukkit.getOnlinePlayers().toArray(new Player[0])).length; i++)
                    if ((p1[i] != p) && (p1[i].getGameMode() != GameMode.CREATIVE)) {
                        Location lo = p1[i].getLocation();
                        if ((p.getLocation().distance(p1[i].getLocation()) <= 10.0D) && (lo.getY() != 0.0D)) {
                            Location loc2 = p1[i].getLocation();
                            p1[i].getWorld().createExplosion(loc2, 0.3F);
                        }
                    }
            }
            if (this.num > 16) cancel();
            this.num += 1;
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Sunbi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */