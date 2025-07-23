package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.BaseItem;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Teleporter extends Ability implements BaseItem {
    // 표지판 정보 저장 필드
    private static String signName = null;
    private static Location signLoc = null;

    public Teleporter() {
        InitAbility("소환술사", Type.Active_Immediately, Rank.A,
                Usage.IronLeft + "표지판을 설치하고 첫 줄에 플레이어의 이름(자신도 가능)을 적으면,",
                "능력 사용 시 이름이 적힌 플레이어가 표지판으로 이동합니다.");
        InitAbility(100, 0, true);
        registerLeftClickEvent();
        EventManager.onSignChangeEvent.add(new EventData(this, 1));
        EventManager.onBlockBreakEvent.add(new EventData(this, 2));
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                PlayerInteractEvent event0 = (PlayerInteractEvent) event;
                if (isOwner(event0.getPlayer()) && !ConfigManager.DamageGuard && isValidItem(Ability.DefaultItem)) {
                    if (signName != null && signLoc != null) return 0;
                    event0.getPlayer().sendMessage(ChatColor.RED + "표지판을 설치하셔야 합니다.");
                }
            }
            case 1 -> {
                SignChangeEvent event1 = (SignChangeEvent) event;
                if (isOwner(event1.getPlayer()) && !ConfigManager.DamageGuard) {
                    if (event1.getBlock().getType().name().endsWith("_SIGN")) {
                        String line = event1.getLine(0);
                        if (line == null || line.isEmpty()) {
                            sendMessage(ChatColor.RED + "표지판의 첫 줄에 플레이어의 이름을 입력해주세요.");
                            break;
                        }
                        Player target = Bukkit.getPlayer(line);
                        if (target == null || !target.isOnline()) {
                            sendMessage(ChatColor.RED + "유효한 플레이어가 아닙니다.");
                            break;
                        }
                        signName = target.getName();
                        signLoc = event1.getBlock().getLocation();
                        event1.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "철괴를 휘두르면 " + ChatColor.WHITE + signName
                                + ChatColor.LIGHT_PURPLE + "님은 이곳으로 텔레포트됩니다.");
                    }
                }
            }
            case 2 -> {
                BlockBreakEvent event2 = (BlockBreakEvent) event;
                if (signLoc != null && event2.getBlock().getLocation().equals(signLoc)) {
                    signName = null;
                    signLoc = null;
                    sendMessage(ChatColor.RED + "표지판이 제거되었습니다.");
                }
            }
            case ITEM_DROP_EVENT -> {
                return handleItemDropCondition(event);
            }
            case ITEM_RESPAWN_EVENT -> {
                return handleItemRespawnCondition(event);
            }
            case ITEM_DEATH_EVENT -> {
                return handleItemDeathCondition(event);
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 0) {
            Player target = Bukkit.getPlayer(signName);
            if (target == null) return;
            target.teleport(signLoc);
            target.sendMessage(ChatColor.RED + "소환술사에 의해 당신은 지정된 위치로 이동합니다.");
            signLoc.getBlock().breakNaturally();
            signName = null;
            signLoc = null;
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        giveBaseItem(p);
    }

    @Override
    public void A_ResetEvent(Player p) {
        removeBaseItem(p);
    }

    @Override
    public ItemStack[] getBaseItem() {
        return new ItemStack[] {
                new ItemStack(Material.OAK_SIGN, 3)
        };
    }

    @Override
    public String getItemName() {
        return "눈덩이";
    }

}
