package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;
import io.github.kdy05.physicalFighters.PhysicalFighters;

import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Kaiji extends Ability {
    public Kaiji() {
        InitAbility("카이지", Type.Passive_Manual, Rank.S,
                "다이아몬드로 상대 타격 시 30% 확률로 상대를 즉사시키고, 70% 확률로 자신이 사망합니다.");
        InitAbility(20, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        EventManager.onPlayerDropItem.add(new EventData(this, 1));
        EventManager.onPlayerRespawn.add(new EventData(this, 2));
        EventManager.onEntityDeath.add(new EventData(this, 3));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
                if (!PhysicalFighters.DamageGuard && isOwner(Event.getDamager())
                        && isValidItem(Material.DIAMOND) && Event.getEntity() instanceof Player)
                    return 0;
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                if (isOwner(Event1.getPlayer()) &&
                        Event1.getItemDrop().getItemStack().getType() == Material.DIAMOND) {
                    PlayerInventory inv = Event1.getPlayer().getInventory();
                    if (!inv.contains(Material.DIAMOND, 1)) return 1;
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
                EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
                Player player = (Player) Event.getEntity();
                if (Math.random() <= 0.3D) {
                    player.damage(5000);
                    Bukkit.broadcastMessage(String.format(ChatColor.RED +
                            "%s님이  카'의지'에 능력에 의지가 꺾였습니다.", player.getName()));
                    if (PhysicalFighters.AutoKick) {
                        if (PhysicalFighters.AutoBan)
                            player.ban("카이지에 의해 사망했습니다.", (Date) null, null, true);
                        player.kickPlayer("카이지에 의해 사망했습니다.");
                    }
                } else {
                    getPlayer().damage(5000);
                    Bukkit.broadcastMessage(String.format(ChatColor.RED +
                            "%s님이 도박하다가 손목이 날라갔습니다.", getPlayer().getName()));
                    if (PhysicalFighters.AutoKick) {
                        if (PhysicalFighters.AutoBan)
                            player.ban("카이지에 의해 사망했습니다.", (Date) null, null, true);
                        getPlayer().kickPlayer("카이지에 의해 사망했습니다.");
                    }
                }
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                Event1.getPlayer().sendMessage(ChatColor.RED + "다이아는 버릴 수 없습니다.");
                Event1.setCancelled(true);
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                PlayerInventory inv = Event2.getPlayer().getInventory();
                inv.setItem(8, new ItemStack(Material.DIAMOND, 1));
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                List<ItemStack> itemlist = Event3.getDrops();
                itemlist.removeIf(item -> item.getType() == Material.DIAMOND);
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.DIAMOND, 1));
    }

    @Override
    public void A_ResetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.DIAMOND, 1));
    }
}
