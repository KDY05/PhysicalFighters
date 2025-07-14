package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Devil extends Ability {
    public Devil() {
        InitAbility("악마", Type.Active_Immediately, Rank.GOD, new String[]{
                "상대를 철괴로 타격할시에 공중의 투기장으로 이동하여 20초간 1:1 대결을 펼칩니다.",
                "이때 10초간 상대는 다양한 디버프를 받고, 당신은 버프를 받습니다.",
                "당신은 불데미지, 낙하데미지를 받지 않습니다."});
        InitAbility(80, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
        EventManager.onEntityDamage.add(new EventData(this, 3));
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
            if ((!EventManager.DamageGuard) &&
                    (isOwner(Event.getDamager())) && ((Event.getEntity() instanceof Player)) && (isValidItem(Ability.DefaultItem))) {
                return 0;
            }
        } else if (CustomData == 3) {
            EntityDamageEvent Event2 = (EntityDamageEvent) event;
            if (isOwner(Event2.getEntity())) {
                if ((Event2.getCause() == DamageCause.LAVA) ||
                        (Event2.getCause() == DamageCause.FIRE) ||
                        (Event2.getCause() == DamageCause.FIRE_TICK)) {
                    Event2.setCancelled(true);
                } else if (Event2.getCause() == DamageCause.FALL) {
                    getPlayer().sendMessage(
                            ChatColor.GREEN + "사뿐하게 떨어져 데미지를 받지 않았습니다.");
                    Event2.setCancelled(true);
                }
            }
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
        l3.setY(l1.getY() + 83.0D);
        ll.setY(l.getY() + 83.0D);
        p.teleport(l3);
        getPlayer().teleport(ll);
        p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0), true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0), true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 0), true);
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 0), true);
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 0), true);
        Timer timer = new Timer();
        timer.schedule(new Pauck(p, getPlayer(), l1, l), 20000L);
        for (int j = 0; j <= 8; j++) {
            l2.setY(l1.getY() + j + 80.0D);
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
            l2.setY(l1.getY() + j + 80.0D);
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                }
            }
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                }
            }
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                }
            }
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                }
            }
        }
        for (int j = 2; j <= 6; j++) {
            l2.setY(l1.getY() + j + 80.0D);
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
        }
        l2.setY(l1.getY() + 6.0D + 80.0D);
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
                l5.setY(this.l1.getY() + j + 80.0D);
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


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Devil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */