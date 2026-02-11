package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;

import io.github.kdy05.physicalFighters.util.AttributeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ReverseAlchemy extends Ability {
    // 상수 정의
    private static final int GOLD_FOR_HEALING = 1;
    private static final int GOLD_FOR_DIAMOND = 3;

    public ReverseAlchemy(Player player) {
        super(AbilitySpec.builder("반 연금술", Type.Active_Immediately, Rank.A)
                .cooldown(5)
                .showText(ShowText.No_CoolDownText)
                .guide(Usage.IronLeft + "금괴 3개를 다이아몬드 1개로 변환합니다.",
                        Usage.GoldRight + "금괴를 소모하여 자신의 체력을 회복합니다.",
                        "이때 체력이 최대 채력의 절반 이상이라면 체력을 전부 회복하며,",
                        "절반 이하라면 최대 체력의 절반까지 회복합니다.")
                .build(), player);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
        registerRightClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player player = event0.getPlayer();
        if (!isOwner(player)) return -1;
        PlayerInventory inventory  = player.getInventory();

        if (CustomData == 0) {
            if (!isValidItem(Ability.DefaultItem)) return -1;
            if (inventory.contains(Material.GOLD_INGOT, GOLD_FOR_DIAMOND))
                return 0;
            player.sendMessage(ChatColor.RED + "금괴가 " + GOLD_FOR_DIAMOND + "개 필요합니다.");
        } else if (CustomData == 1) {
            if (!isValidItem(Material.GOLD_INGOT)) return -1;
            if (inventory.contains(Material.GOLD_INGOT, GOLD_FOR_HEALING)) {
                return 1;
            }
            player.sendMessage(ChatColor.RED + "금괴가 " + GOLD_FOR_HEALING + "개 필요합니다.");
        }

        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player player = event0.getPlayer();
        if (CustomData == 0) {
            player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, GOLD_FOR_DIAMOND));
            player.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
            player.sendMessage(ChatColor.GREEN + "금괴 " + GOLD_FOR_DIAMOND + "개로 다이아몬드를 만들었습니다.");
        } else if (CustomData == 1) {
            player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, GOLD_FOR_HEALING));
            double maxHealthValue = AttributeUtils.getMaxHealth(player);

            if (player.getHealth() >= maxHealthValue / 2) {
                player.setHealth(maxHealthValue);
            } else {
                player.setHealth(maxHealthValue / 2);
            }

            player.sendMessage(ChatColor.GREEN + "체력을 회복하였습니다.");
        } else {
            player.sendMessage(ChatColor.RED + "알 수 없는 오류가 발생했습니다.");
        }
    }

}