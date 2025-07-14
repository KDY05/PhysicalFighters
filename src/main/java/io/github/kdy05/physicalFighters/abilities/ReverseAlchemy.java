package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

// TODO: 체력 회복 로직 수정

public class ReverseAlchemy extends Ability {
    // 상수 정의
    private static final int GOLD_FOR_HEALING = 1;
    private static final int GOLD_FOR_DIAMOND = 3;
    private static final double MAX_HEALTH = 20.0;

    public ReverseAlchemy() {
        InitAbility("반 연금술", Type.Active_Immediately, Rank.A,
                "철괴 좌클릭 시 금괴 3개로 다이아몬드 하나를 얻습니다.",
                "금괴 우클릭 시 금괴를 소모하여 자신의 체력을 회복합니다.");
        InitAbility(8, 0, true, ShowText.No_Text);
        registerLeftClickEvent();
        registerRightClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player player = Event.getPlayer();
        if (!isOwner(player)) return -1;

        PlayerInventory inventory = player.getInventory();
        if (isValidItem(Material.GOLD_INGOT) && CustomData == 1)
            return handleHealing(player, inventory);

        if (isValidItem(Ability.DefaultItem) && CustomData == 0)
            return handleDiamondCreation(player, inventory);

        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player player = Event.getPlayer();
        switch (CustomData) {
            case 0: // 체력 회복
                executeHealing(player);
                break;
            case 1: // 다이아몬드 생성
                executeDiamondCreation(player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "알 수 없는 오류가 발생했습니다.");
        }
    }

    private int handleHealing(Player player, PlayerInventory inventory) {
        if (!inventory.contains(Material.GOLD_INGOT, GOLD_FOR_HEALING)) {
            player.sendMessage(ChatColor.RED + "금괴가 " + GOLD_FOR_HEALING + "개 필요합니다.");
            return -1;
        }
        return 0;
    }

    private int handleDiamondCreation(Player player, PlayerInventory inventory) {
        if (inventory.contains(Material.GOLD_INGOT, GOLD_FOR_DIAMOND))
            return 1;
        player.sendMessage(ChatColor.RED + "금괴가 " + GOLD_FOR_DIAMOND + "개 필요합니다.");
        return -1;
    }

    private void executeHealing(Player player) {
        player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, GOLD_FOR_HEALING));
        player.setHealth(MAX_HEALTH);
        player.sendMessage(ChatColor.GREEN + "체력이 완전히 회복되었습니다!");
    }

    private void executeDiamondCreation(Player player) {
        player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, GOLD_FOR_DIAMOND));
        player.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
        player.sendMessage(ChatColor.GREEN + "금괴 " + GOLD_FOR_DIAMOND + "개로 다이아몬드를 만들었습니다!");
    }
}