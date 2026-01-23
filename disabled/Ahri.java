package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Ahri extends Ability {
    public Ahri() {
        InitAbility("아리", Type.Active_Immediately, Rank.SS,
                "눈덩이를 던져, 맞은 적을 자신에게 무작정 걸어오게 만듭니다.");
        InitAbility(20, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        EventManager.onPlayerDropItem.add(new EventData(this, 1));
        EventManager.onPlayerRespawn.add(new EventData(this, 2));
        EventManager.onEntityDeath.add(new EventData(this, 3));
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                if ((!ConfigManager.DamageGuard) &&
                        ((Event0.getDamager() instanceof Snowball a))) {
                    if (isOwner((Player) a.getShooter())) {
                        if (((Event0.getEntity() instanceof Player)) &&
                                (a.getShooter() == Event0.getEntity()))
                            return 9999;
                        Location l = getPlayer().getLocation();
                        Location l1 = Event0.getEntity().getLocation();
                        if (l.distance(l1) < 10.0D)
                            return 0;
                    }
                }
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                if ((isOwner(Event1.getPlayer())) &&
                        (Event1.getItemDrop().getItemStack().getType() == Material.SNOWBALL)) {
                    PlayerInventory inv = Event1.getPlayer().getInventory();
                    if (!inv.contains(Material.SNOWBALL, 16)) {
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
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                Entity pe = Event0.getEntity();
                Timer timer = new Timer();
                timer.schedule(new Timerr(getPlayer(), pe), 0L, 80L);
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                Event1.getPlayer().sendMessage(
                        ChatColor.RED + "소유한 눈덩이가 16개 이하일시 못버립니다.");
                Event1.setCancelled(true);
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                Event2.getPlayer().sendMessage(ChatColor.GREEN + "눈덩이가 지급됩니다.");
                PlayerInventory inv = Event2.getPlayer().getInventory();
                inv.setItem(8, new ItemStack(Material.SNOWBALL, 64));
                inv.setItem(7, new ItemStack(Material.SNOWBALL, 64));
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                List<ItemStack> itemlist = Event3.getDrops();
                for (int l = 0; l < itemlist.size(); l++)
                    if (itemlist.get(l).getType() == Material.SNOWBALL)
                        itemlist.remove(l);
        }
    }

    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.SNOWBALL, 64));
        p.getInventory().setItem(7, new ItemStack(Material.SNOWBALL, 64));
    }

    public void A_ResetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.SNOWBALL, 64));
        p.getInventory().setItem(7, new ItemStack(Material.SNOWBALL, 64));
    }

    public class Timerr extends TimerTask {
        private Player p;
        private Entity t;
        private int x = 0;
        private Location l;
        private Location l1;

        public Timerr(Player pp, Entity tt) {
            this.p = pp;
            this.t = tt;
            this.l = this.p.getLocation();
            this.l1 = this.t.getLocation();
        }

        public void run() {
            if (this.x > 30) {
                cancel();
            }
            this.l = this.p.getLocation();
            this.l1 = this.t.getLocation();
            this.l1.setX(((((((this.l1.getX() + this.l.getX()) / 2.0D + this.l1.getX()) / 2.0D + this.l1.getX()) / 2.0D + this.l1.getX()) / 2.0D + this.l1.getX()) / 2.0D + this.l1.getX()) / 2.0D);
            this.l1.setZ(((((((this.l1.getZ() + this.l.getZ()) / 2.0D + this.l1.getZ()) / 2.0D + this.l1.getZ()) / 2.0D + this.l1.getZ()) / 2.0D + this.l1.getZ()) / 2.0D + this.l1.getZ()) / 2.0D);
            this.t.teleport(this.l1);
            this.x += 1;
        }
    }
}
