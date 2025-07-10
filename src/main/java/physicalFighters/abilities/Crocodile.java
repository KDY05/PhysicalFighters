package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.PhysicalFighters;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Crocodile extends Ability {
    public Crocodile() {
        if (PhysicalFighters.SRankUsed) {
            InitAbility("크로커다일", Type.Active_Immediately, Rank.S, new String[]{
                    "철괴 왼쪽클릭시 자신의 주변의 있는 블력을 모래로 바꿔버립니다.",
                    "철괴 오른쪽클릭시 모래 주변에 있는 적에게 일정시간동안 모래바람을 일으킵니다."});
            InitAbility(30, 0, true);
            registerLeftClickEvent();
            registerRightClickEvent();
        }
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem)) && !EventManager.DamageGuard) {
                    return 0;
                }
            case 1:
                PlayerInteractEvent Event1 = (PlayerInteractEvent) event;
                if ((isOwner(Event1.getPlayer())) && (isValidItem(Ability.DefaultItem)) && !EventManager.DamageGuard)
                    return 1;
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                Player p = Event.getPlayer();
                World w = Event.getPlayer().getWorld();
                Location loc = p.getLocation();
                for (int i = -5; i < 6; i++) {
                    for (int j = -5; j < 6; j++) {
                        for (int k = -1; k < 6; k++) {
                            if (w.getBlockAt((int) loc.getX() + i, (int) loc.getY() + k, (int) loc.getZ() + j).getType() != Material.AIR)
                                w.getBlockAt((int) loc.getX() + i, (int) loc.getY() + k, (int) loc.getZ() + j).setType(Material.SAND);
                        }
                    }
                }
                break;
            case 1:
                PlayerInteractEvent Event1 = (PlayerInteractEvent) event;
                Timer timer = new Timer();
                timer.schedule(new Pauck(Event1.getPlayer().getName()), 500L, 1500L);
        }
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
                for (int i = 0; i < (Bukkit.getOnlinePlayers()).size(); i++) {
                    if ((p1[i] != p) && (p1[i].getGameMode() != GameMode.CREATIVE)) {
                        Location loc = p1[i].getLocation();
                        World w = p.getWorld();
                        for (int i1 = 0; i1 < 2; i1++) {
                            for (int j = -1; j < 2; j++)
                                for (int k = -1; k < 2; k++)
                                    if (w.getBlockAt((int) loc.getX() + i1, (int) loc.getY() + k, (int) loc.getZ() + j).getType() == Material.SAND) {
                                        p1[i].damage(2, p);
                                        p1[i].setVelocity(p1[i].getVelocity().add(
                                                p.getLocation().toVector()
                                                        .subtract(p1[i].getLocation().toVector()).normalize()
                                                        .multiply(-1)));
                                        p1[i].addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0), true);
                                    }
                        }
                    }
                }
                if (this.num > 10) {
                    cancel();
                    p.sendMessage(ChatColor.GREEN + "지속시간이 끝났습니다.");
                }
                this.num += 1;
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Crocodile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */