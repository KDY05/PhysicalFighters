package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.AUC;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class FallArrow extends Ability {
    public FallArrow() {
        InitAbility("중력화살", Type.Passive_Manual, Rank.S,
                "화살에 맞은 플레이어는 공중으로 뜹니다. [추가타 가능]");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        EventManager.onPlayerDropItem.add(new EventData(this, 1));
        EventManager.onPlayerRespawn.add(new EventData(this, 2));
        EventManager.onEntityDeath.add(new EventData(this, 3));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                if (Event0.getDamager() instanceof Arrow a && a.getShooter() instanceof Player player
                        && isOwner(player) && Event0.getEntity() instanceof LivingEntity entity
                        && entity != player && !PhysicalFighters.DamageGuard) {
                    return 0;
                }
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                if (isOwner(Event1.getPlayer()) &&
                        Event1.getItemDrop().getItemStack().getType() == Material.ARROW) {
                    PlayerInventory inv = Event1.getPlayer().getInventory();
                    if (!inv.contains(Material.ARROW, 64)) return 1;
                }
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                if (isOwner(Event2.getPlayer())) return 2;
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                if (isOwner(Event3.getEntity())) return 3;
                break;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                LivingEntity entity = (LivingEntity) Event0.getEntity();
                Location l1 = Event0.getEntity().getLocation();
                Location l2 = Event0.getEntity().getLocation();
                l2.setY(l1.getY() + 4.0D);
                AUC.goVelocity(entity, l2, 1);
                entity.getWorld().createExplosion(entity.getLocation(), 0.0F);
                entity.teleport(l2);
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
                itemlist.removeIf(item -> item.getType() == Material.ARROW);
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.ARROW, 64));
        p.getInventory().setItem(7, new ItemStack(Material.BOW, 1));
    }

    @Override
    public void A_ResetEvent(Player p) {
        p.getInventory().removeItem(new ItemStack(Material.ARROW, 64));
    }
}
