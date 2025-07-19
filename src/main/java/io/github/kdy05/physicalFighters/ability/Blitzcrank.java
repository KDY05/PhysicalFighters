package io.github.kdy05.physicalFighters.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileHitEvent;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Blitzcrank extends Ability {
    public Blitzcrank() {
        InitAbility("블리츠크랭크", Type.Active_Immediately, Rank.S,
                "눈덩이를 던져, 맞은 적을 자신에게 끌어당깁니다.");
        InitAbility(1, 0, true);
        EventManager.onProjectileHitEvent.add(new EventData(this, 0));
        EventManager.onPlayerDropItem.add(new EventData(this, 1));
        EventManager.onPlayerRespawn.add(new EventData(this, 2));
        EventManager.onEntityDeath.add(new EventData(this, 3));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                ProjectileHitEvent Event0 = (ProjectileHitEvent) event;
                if (Event0.getEntity() instanceof Snowball s && s.getShooter() instanceof Player p
                        && isOwner(p) && s.getShooter() != Event0.getHitEntity()
                        && Event0.getHitEntity() instanceof LivingEntity) {
                    return 0;
                }
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                if (isOwner(Event1.getPlayer()) &&
                        Event1.getItemDrop().getItemStack().getType() == Material.SNOWBALL) {
                    PlayerInventory inv = Event1.getPlayer().getInventory();
                    if (!inv.contains(Material.SNOWBALL, 16)) return 1;
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
                ProjectileHitEvent Event0 = (ProjectileHitEvent) event;
                LivingEntity target = (LivingEntity) Event0.getHitEntity();
                if (target != null) target.teleport(getPlayer().getLocation());
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                Event1.getPlayer().sendMessage(
                        ChatColor.RED + "소유한 눈덩이가 16개 이하일시 버릴 수 없습니다.");
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
                itemlist.removeIf(item -> item.getType() == Material.SNOWBALL);
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.SNOWBALL, 64));
        p.getInventory().setItem(7, new ItemStack(Material.SNOWBALL, 64));
    }

    @Override
    public void A_ResetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.SNOWBALL, 64));
        p.getInventory().setItem(7, new ItemStack(Material.SNOWBALL, 64));
    }
}
