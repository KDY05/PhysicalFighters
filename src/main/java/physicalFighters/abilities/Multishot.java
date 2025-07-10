package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import java.util.List;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Multishot extends Ability {
    public Multishot() {
        InitAbility("멀티샷", Type.Active_Immediately, Rank.A,
                "죽거나 게임 시작시 화살 한묶음이 고정적으로 주어집니다.",
                "화살 발사시에 여러발이 퍼지면서 날라갑니다. [움직이면서 사격시 화살이 나가지 않을 수 있습니다.]");
        InitAbility(3, 0, true, ShowText.All_Text);
        EventManager.onProjectileLaunchEvent.add(new EventData(this, 0));
        EventManager.onPlayerDropItem.add(new EventData(this, 1));
        EventManager.onPlayerRespawn.add(new EventData(this, 2));
        EventManager.onEntityDeath.add(new EventData(this, 3));
        EventManager.onEntityDamageByEntity.add(new EventData(this, 4));
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                ProjectileLaunchEvent Event0 = (ProjectileLaunchEvent) event;
                if (Event0.getEntity().getShooter() instanceof Player && Event0.getEntity() instanceof Arrow a
                        && ((Player) Objects.requireNonNull(Event0.getEntity().getShooter()))
                        .getInventory().getItemInMainHand().getType() == Material.BOW) {
                    if (isOwner((Player) a.getShooter())) {
                        return 0;
                    }
                }
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                if ((isOwner(Event1.getPlayer())) &&
                        (Event1.getItemDrop().getItemStack().getType() == Material.ARROW)) {
                    PlayerInventory inv = Event1.getPlayer().getInventory();
                    if (!inv.contains(Material.ARROW, 64)) {
                        return 1;
                    }
                }
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                if (isOwner(Event2.getPlayer()))
                    return 2;
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                if (isOwner(Event3.getEntity()))
                    return 3;
                break;
            case 4:
                EntityDamageByEntityEvent Event4 = (EntityDamageByEntityEvent) event;
                if ((Event4.getDamager() instanceof Arrow a)) {
                    if (isOwner((Player) a.getShooter())) {
                        if (((Event4.getEntity() instanceof Player)) &&
                                (a.getShooter() == Event4.getEntity()))
                            return -1;
                        if (Event4.getEntityType() != EntityType.PLAYER) {
                            return 1;
                        }
                        ((Player) Event4.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 2));
                        ((Player) Event4.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 2));
                        ((Player) Event4.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 2));
                    }
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                ProjectileLaunchEvent Event0 = (ProjectileLaunchEvent) event;
                Arrow a2 = (Arrow) Event0.getEntity();
                Player p = (Player) a2.getShooter();
                a2.remove();
                assert p != null;
                Location ll = p.getLocation();
                Location l1 = p.getLocation();
                Location l2 = p.getLocation();
                Location l3 = p.getLocation();
                l1.setY(l2.getY() + 1.5D);
                LookAngle(l1, l2, 0);
                LookAngle(l3, ll, 100);
                Vector v = ll.toVector().subtract(l2.toVector()).normalize();
                for (int i = 0; i <= 10; i++) {
                    Arrow a = p.getWorld().spawnArrow(l2, v, 1.5F, 10.0F);
                    a.setVelocity(a.getVelocity().multiply(2.2D));
                    a.setShooter(p);
                }
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                Event1.getPlayer().sendMessage(
                        ChatColor.RED + "소유한 화살이 64개 이하일시 화살을 버릴수 없습니다.");
                Event1.setCancelled(true);
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                Event2.getPlayer().sendMessage(
                        ChatColor.GREEN + "이전에 소유했던 화살은 모두 소멸하며 다시 지급됩니다.");
                PlayerInventory inv = Event2.getPlayer().getInventory();
                inv.remove(new ItemStack(Material.ARROW, 64));
                inv.setItem(8, new ItemStack(Material.ARROW, 64));
                inv.setItem(7, new ItemStack(Material.BOW, 1));
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                List<ItemStack> itemlist = Event3.getDrops();
                for (int l = 0; l < itemlist.size(); l++) {
                    if (itemlist.get(l).getType() == Material.ARROW)
                        itemlist.remove(l);
                }
        }
    }

    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.ARROW, 64));
        p.getInventory().setItem(7, new ItemStack(Material.BOW, 1));
    }

    public void A_ResetEvent(Player p) {
        p.getInventory().removeItem(new ItemStack(Material.ARROW, 64));
    }

    public static void LookAngle(Location l, Location l2, int value) {
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));
        l2.setX(l.getX() + 2 * value + (Math.sin(degrees) * Math.cos(ydeg)));
        l2.setY(l.getY() + 2 * value + Math.sin(ydeg));
        l2.setZ(l.getZ() + 2 * value + (Math.cos(degrees) * Math.cos(ydeg)));
    }

}
