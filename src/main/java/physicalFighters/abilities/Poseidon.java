package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.utils.ACC;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Poseidon
        extends AbilityBase {
    public Poseidon() {
        if ((!PhysicalFighters.Toner) &&
                (PhysicalFighters.SRankUsed)) {
            InitAbility("포세이돈", Type.Active_Immediately, Rank.SS, new String[]{
                    "바라보는곳에 거대한 어항을 만들어 가둡니다.", "물에서 숨을 쉴 수 있습니다."});
            InitAbility(60, 0, true);
            registerLeftClickEvent();
            EventManager.onEntityDamage.add(new EventData(this, 3));
            EventManager.onPlayerMoveEvent.add(new EventData(this, 4));
        }
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            if ((!EventManager.DamageGuard) &&
                    (isOwner(Event.getPlayer())) &&
                    (isValidItem(ACC.DefaultItem)) &&
                    (Event.getPlayer().getTargetBlock(null, 0).getType() != Material.BEDROCK)) {
                return 0;
            }
        }
        if (CustomData == 3) {
            EntityDamageEvent Event2 = (EntityDamageEvent) event;
            if ((isOwner(Event2.getEntity())) &&
                    (Event2.getCause() == DamageCause.DROWNING)) {
                Event2.setCancelled(true);
            }
        }
        if (CustomData == 4) {
            PlayerMoveEvent e = (PlayerMoveEvent) event;
            if (isOwner(e.getPlayer())) {
                if (e.getPlayer().getLocation().getBlock().isLiquid()) {
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999999, 1));
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999999999, 1));
                } else {
                    e.getPlayer().removePotionEffect(PotionEffectType.SPEED);
                    e.getPlayer().removePotionEffect(PotionEffectType.RESISTANCE);
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if ((p.getLocation().distance(e.getPlayer().getLocation()) <= 10.0D) &&
                            (p.getLocation().getBlock().isLiquid()) &&
                            (p != e.getPlayer())) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                    }
                }
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Location l1 = p.getTargetBlock(null, 0).getLocation();
        Location l2 = p.getTargetBlock(null, 0).getLocation();
        for (int j = 0; j <= 8; j++) {
            l2.setY(l1.getY() + j);
            for (int i = 0; i <= 5; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.GLASS);
                for (int k = 0; k <= 5; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.GLASS);
                }
            }
            for (int i = 0; i <= 5; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.GLASS);
                for (int k = 0; k <= 5; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.GLASS);
                }
            }
            for (int i = 0; i <= 5; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.GLASS);
                for (int k = 0; k <= 5; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.GLASS);
                }
            }
            for (int i = 0; i <= 5; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.GLASS);
                for (int k = 0; k <= 5; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.GLASS);
                }
            }
        }
        for (int j = 1; j <= 6; j++) {
            l2.setY(l1.getY() + j);
            for (int i = 0; i <= 2; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.WATER);
                for (int k = 0; k <= 2; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.WATER);
                }
            }
            for (int i = 0; i <= 2; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.WATER);
                for (int k = 0; k <= 2; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.WATER);
                }
            }
            for (int i = 0; i <= 2; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.WATER);
                for (int k = 0; k <= 2; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.WATER);
                }
            }
            for (int i = 0; i <= 2; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.WATER);
                for (int k = 0; k <= 2; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.WATER);
                }
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Poseidon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */