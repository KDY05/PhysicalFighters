package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Teleporter extends Ability {
    // 표지판 정보 저장 필드
    private static String signName = null;
    private static Location signLoc = null;

    public Teleporter() {
        InitAbility("소환술사", Type.Active_Immediately, Rank.A,
                "표지판을 설치하고 첫 줄에 플레이어의 이름(자신도 가능)을 적으면,",
                "철괴 좌클릭 시 이름이 적힌 플레이어가 표지판으로 이동합니다.");
        InitAbility(100, 0, true);
        registerLeftClickEvent();
        EventManager.onSignChangeEvent.add(new EventData(this, 1));
        EventManager.onPlayerRespawn.add(new EventData(this, 2));
        EventManager.onEntityDeath.add(new EventData(this, 3));
        EventManager.onBlockBreakEvent.add(new EventData(this, 4));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if (isOwner(Event.getPlayer()) && !EventManager.DamageGuard && isValidItem(Ability.DefaultItem)) {
                    if (signName != null && signLoc != null) return 0;
                    Event.getPlayer().sendMessage(ChatColor.RED + "표지판을 설치하셔야 합니다.");
                }
                break;
            case 1:
                SignChangeEvent event2 = (SignChangeEvent) event;
                if (isOwner(event2.getPlayer()) && !EventManager.DamageGuard) {
                    if (event2.getBlock().getType().name().endsWith("_SIGN")) {
                        String line = event2.getLine(0);
                        if (line == null || line.isEmpty()) {
                            getPlayer().sendMessage(ChatColor.RED + "표지판의 첫 줄에 플레이어의 이름을 입력해주세요.");
                            break;
                        }
                        Player target = Bukkit.getPlayer(line);
                        if (target == null || !target.isOnline()) {
                            getPlayer().sendMessage(ChatColor.RED + "유효한 플레이어가 아닙니다.");
                            break;
                        }
                        signName = target.getName();
                        signLoc = event2.getBlock().getLocation();
                        event2.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "철괴를 휘두르면 " + ChatColor.WHITE + signName
                                + ChatColor.LIGHT_PURPLE + "님은 이곳으로 텔레포트됩니다.");
                    }
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
            case 4:
                BlockBreakEvent Event4 = (BlockBreakEvent) event;
                if (signLoc != null && Event4.getBlock().getLocation().equals(signLoc)) {
                    signName = null;
                    signLoc = null;
                    getPlayer().sendMessage(ChatColor.RED + "표지판이 제거되었습니다.");
                }
                break;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                Player caster = Event.getPlayer();
                Player target = Bukkit.getPlayer(signName);
                if (target == null) break;
                target.teleport(signLoc);
                target.sendMessage(ChatColor.RED + "소환술사에 의해 당신은 지정된 위치로 이동합니다.");
                caster.sendMessage(ChatColor.GREEN + "성공적으로 소환을 마쳤습니다.");
                signLoc.getBlock().breakNaturally();
                signName = null;
                signLoc = null;
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                Event2.getPlayer().sendMessage(
                        ChatColor.GREEN + "이전에 소유했던 표지판은 모두 소멸하며 다시 지급됩니다.");
                PlayerInventory inv = Event2.getPlayer().getInventory();
                inv.remove(new ItemStack(Material.OAK_SIGN));
                inv.setItem(8, new ItemStack(Material.OAK_SIGN, 3));
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                List<ItemStack> itemlist = Event3.getDrops();
                itemlist.removeIf(item -> item.getType().name().endsWith("_SIGN"));
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.OAK_SIGN, 3));
    }

    @Override
    public void A_ResetEvent(Player p) {
        p.getInventory().removeItem(new ItemStack(Material.OAK_SIGN));
    }
}
