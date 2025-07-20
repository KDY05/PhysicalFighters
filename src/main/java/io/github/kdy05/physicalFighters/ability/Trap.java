package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Trap extends Ability {
    public static Block[] trap = new Block[6];
    public static int traps = 0;

    public Trap() {
        InitAbility("부비트랩", Type.Passive_Manual, Rank.S,
                "처음 시작시에 소울샌드가 주어집니다. 소울샌드는 버릴 수 없습니다.",
                "소울샌드는 최대 5개까지 설치가 가능하며, 철괴 왼쪽클릭으로 수동으로 폭발시킬 수 있습니다.");
        InitAbility(0, 0, true);
        registerLeftClickEvent();
        EventManager.onBlockPlaceEvent.add(new EventData(this, 1));
        EventManager.onPlayerDropItem.add(new EventData(this, 3));
        EventManager.onPlayerRespawn.add(new EventData(this, 4));
        EventManager.onEntityDeath.add(new EventData(this, 5));
        EventManager.onBlockBreakEvent.add(new EventData(this, 6));
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem)) && !ConfigManager.DamageGuard) {
                    return 0;
                }
                break;
            case 1:
                BlockPlaceEvent Event1 = (BlockPlaceEvent) event;
                if ((isOwner(Event1.getPlayer())) && (Event1.getBlock().getType() == Material.SOUL_SAND)) {
                    return 1;
                }
                break;
            case 3:
                PlayerDropItemEvent Event3 = (PlayerDropItemEvent) event;
                if ((isOwner(Event3.getPlayer())) &&
                        (Event3.getItemDrop().getItemStack().getType() == Material.SOUL_SAND)) {
                    PlayerInventory inv = Event3.getPlayer().getInventory();
                    if (!inv.contains(Material.SOUL_SAND, 2)) {
                        return 3;
                    }
                }
                break;
            case 4:
                PlayerRespawnEvent Event4 = (PlayerRespawnEvent) event;
                if (isOwner(Event4.getPlayer()))
                    return 4;
                break;
            case 5:
                EntityDeathEvent Event5 = (EntityDeathEvent) event;
                if (isOwner(Event5.getEntity()))
                    return 5;
                break;
            case 6:
                BlockBreakEvent Event6 = (BlockBreakEvent) event;
                if ((isOwner(Event6.getPlayer())) && (Event6.getBlock().getType() == Material.SOUL_SAND)) {
                    return 6;
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if (traps != 0) {
                    if (trap[0] != null)
                        trap[0].getWorld().createExplosion(trap[0].getLocation(), 3.0F);
                    if (trap[1] != null)
                        trap[1].getWorld().createExplosion(trap[1].getLocation(), 3.0F);
                    if (trap[2] != null)
                        trap[2].getWorld().createExplosion(trap[2].getLocation(), 3.0F);
                    if (trap[3] != null)
                        trap[3].getWorld().createExplosion(trap[3].getLocation(), 3.0F);
                    if (trap[4] != null) {
                        trap[4].getWorld().createExplosion(trap[4].getLocation(), 3.0F);
                    }
                    Event.getPlayer().sendMessage(String.format(ChatColor.AQUA + "모든 폭발물을 폭발시켰습니다. 터진 폭발물 : " +
                            ChatColor.WHITE + "%d개", traps));
                    traps = 0;
                    for (int i = 0; i < 5; i++) {
                        if (trap[i] != null) {
                            trap[i] = null;
                        }
                    }
                } else {
                    Event.getPlayer().sendMessage("폭발물이 없습니다. 설치해주세요.");
                }
                break;
            case 1:
                BlockPlaceEvent Event1 = (BlockPlaceEvent) event;
                if (traps < 5) {
                    Event1.getPlayer().sendMessage(String.format(ChatColor.AQUA + "폭발물을 설치했습니다. 폭발물 : " +
                            ChatColor.WHITE + "(%d/5)", traps + 1));
                    traps += 1;
                    trap[traps] = Event1.getBlock();
                    PlayerInventory inv = Event1.getPlayer().getInventory();
                    inv.setItem(8, new ItemStack(Material.SOUL_SAND, 1));
                } else {
                    Event1.getPlayer().sendMessage("더 이상 폭발물을 설치할 수 없습니다.");
                    Event1.setCancelled(true);
                    PlayerInventory inv = Event1.getPlayer().getInventory();
                    inv.setItem(8, new ItemStack(Material.SOUL_SAND, 1));
                }
                break;
            case 6:
                BlockBreakEvent Event6 = (BlockBreakEvent) event;
                if (traps >= 0) {
                    Event6.getPlayer().sendMessage(String.format(ChatColor.AQUA + "폭발물이 제거되었습니다. 폭발물 : " +
                            ChatColor.WHITE + "(%d/5)", traps - 1));
                    traps -= 1;
                    trap[traps] = Event6.getBlock();
                    PlayerInventory inv = Event6.getPlayer().getInventory();
                    inv.setItem(8, new ItemStack(Material.SOUL_SAND, 1));
                } else {
                    Event6.getPlayer().sendMessage("ERROR");
                    Event6.setCancelled(true);
                    PlayerInventory inv = Event6.getPlayer().getInventory();
                    inv.setItem(8, new ItemStack(Material.SOUL_SAND, 1));
                }
                break;
            case 3:
                PlayerDropItemEvent Event3 = (PlayerDropItemEvent) event;
                Event3.getPlayer().sendMessage(
                        ChatColor.RED + "소울샌드는 버릴 수 없습니다.");
                Event3.setCancelled(true);
                break;
            case 4:
                PlayerRespawnEvent Event4 = (PlayerRespawnEvent) event;
                Event4.getPlayer().sendMessage(
                        ChatColor.GREEN + "소울샌드가 지급됩니다.");
                PlayerInventory inv = Event4.getPlayer().getInventory();
                inv.setItem(8, new ItemStack(Material.SOUL_SAND, 1));
                break;
            case 5:
                EntityDeathEvent Event5 = (EntityDeathEvent) event;
                List<ItemStack> itemlist = Event5.getDrops();
                itemlist.removeIf(item -> item.getType() == Material.SOUL_SAND);
        }
    }

    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.SOUL_SAND, 1));
    }
}
