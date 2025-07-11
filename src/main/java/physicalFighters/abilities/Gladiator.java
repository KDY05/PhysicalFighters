package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Gladiator extends Ability {
    public Gladiator() {
        InitAbility("글레디에이터", Type.Active_Immediately, Rank.SSS, new String[]{
                "상대를 철괴로 타격할시에 천공의 투기장으로 이동하여 10초간 1:1 대결을 펼칩니다.",
                "이때 상대는 다양한 디버프를 받고, 당신은 버프를 받습니다."});
        InitAbility(60, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if ((!EventManager.DamageGuard) &&
                (isOwner(Event.getDamager())) && ((Event.getEntity() instanceof Player)) && (isValidItem(Ability.DefaultItem))) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Player p = (Player) Event.getEntity();
        Location l1 = p.getLocation();
        Location l2 = p.getLocation();
        Location l3 = p.getLocation();
        Location l = getPlayer().getLocation();
        Location ll = getPlayer().getLocation();
        l3.setY(l1.getY() + 52.0D);
        ll.setY(l.getY() + 52.0D);
        p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0), true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0), true);
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 0), true);
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 0), true);
        Timer timer = new Timer();
        timer.schedule(new Pauck(p, getPlayer(), l1, l), 10000L);
        for (int j = 0; j <= 8; j++) {
            l2.setY(l1.getY() + j + 50.0D);
            for (int i = 0; i <= 5; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.BEDROCK);
                for (int k = 0; k <= 5; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.BEDROCK);
                }
            }
            for (int i = 0; i <= 5; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.BEDROCK);
                for (int k = 0; k <= 5; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.BEDROCK);
                }
            }
            for (int i = 0; i <= 5; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.BEDROCK);
                for (int k = 0; k <= 5; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.BEDROCK);
                }
            }
            for (int i = 0; i <= 5; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.BEDROCK);
                for (int k = 0; k <= 5; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.BEDROCK);
                }
            }
        }
        for (int j = 1; j <= 7; j++) {
            l2.setY(l1.getY() + j + 50.0D);
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
        }
        l2.setY(l1.getY() + 7.0D + 50.0D);
        for (int n = 0; n <= 4; n++) {
            l2.setX(l1.getX() + 4.0D);
            l2.setZ(l1.getZ() + n);
            p.getWorld().getBlockAt(l2).setType(Material.TORCH);
        }
        for (int n = 0; n <= 4; n++) {
            l2.setX(l1.getX() - 4.0D);
            l2.setZ(l1.getZ() + n);
            p.getWorld().getBlockAt(l2).setType(Material.TORCH);
        }
        for (int n = 0; n <= 4; n++) {
            l2.setX(l1.getX() + 4.0D);
            l2.setZ(l1.getZ() - n);
            p.getWorld().getBlockAt(l2).setType(Material.TORCH);
        }
        for (int n = 0; n <= 4; n++) {
            l2.setX(l1.getX() - 4.0D);
            l2.setZ(l1.getZ() - n);
            p.getWorld().getBlockAt(l2).setType(Material.TORCH);
        }
        p.teleport(l3);
        getPlayer().teleport(ll);
    }

    class Pauck extends TimerTask {
        Location l1;
        Location l2;
        Player p1;
        Player p2;

        Pauck(Player pp1, Player pp2, Location locc, Location locc2) {
            this.p1 = pp1;
            this.p2 = pp2;
            this.l1 = locc;
            this.l2 = locc2;
        }

        public void run() {
            this.p1.teleport(this.l1);
            this.p2.teleport(this.l2);
            Location l4 = this.p1.getLocation();
            Location l5 = this.p1.getLocation();
            for (int j = 0; j <= 8; j++) {
                l5.setY(this.l1.getY() + j + 50.0D);
                for (int i = 0; i <= 5; i++) {
                    l5.setX(l4.getX() + i);
                    this.p1.getWorld().getBlockAt(l5).setType(Material.AIR);
                    for (int k = 0; k <= 5; k++) {
                        l5.setZ(l4.getZ() + k);
                        this.p1.getWorld().getBlockAt(l5).setType(Material.AIR);
                    }
                }
                for (int i = 0; i <= 5; i++) {
                    l5.setX(l4.getX() - i);
                    this.p1.getWorld().getBlockAt(l5).setType(Material.AIR);
                    for (int k = 0; k <= 5; k++) {
                        l5.setZ(l4.getZ() - k);
                        this.p1.getWorld().getBlockAt(l5).setType(Material.AIR);
                    }
                }
                for (int i = 0; i <= 5; i++) {
                    l5.setX(l4.getX() - i);
                    this.p1.getWorld().getBlockAt(l5).setType(Material.AIR);
                    for (int k = 0; k <= 5; k++) {
                        l5.setZ(l4.getZ() + k);
                        this.p1.getWorld().getBlockAt(l5).setType(Material.AIR);
                    }
                }
                for (int i = 0; i <= 5; i++) {
                    l5.setX(l4.getX() + i);
                    this.p1.getWorld().getBlockAt(l5).setType(Material.AIR);
                    for (int k = 0; k <= 5; k++) {
                        l5.setZ(l4.getZ() - k);
                        this.p1.getWorld().getBlockAt(l5).setType(Material.AIR);
                    }
                }
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Gladiator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */