package io.github.kdy05.physicalFighters.util;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * 능력서 아이템의 생성과 파싱을 담당하는 유틸리티 클래스.
 */
public final class AbilityBook {

    private static final String PREFIX = ChatColor.GOLD + "[능력서]" + ChatColor.WHITE;

    private AbilityBook() {
        throw new AssertionError();
    }

    /**
     * 능력서 아이템을 생성합니다.
     *
     * @param abilityCode 능력 코드 (AbilityRegistry.AbilityList 인덱스)
     * @return 생성된 능력서 ItemStack, 실패 시 null
     */
    public static ItemStack create(int abilityCode) {
        if (abilityCode < 0 || abilityCode >= AbilityRegistry.AbilityList.size()) {
            return null;
        }

        Ability ability = AbilityRegistry.AbilityList.get(abilityCode);
        ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(PREFIX + abilityCode + ". " + ability.getAbilityName());
        meta.setLore(new LinkedList<>(Arrays.asList(ability.getGuide())));
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * 주어진 아이템이 능력서인지 확인합니다.
     */
    public static boolean isAbilityBook(ItemStack item) {
        if (item == null || item.getType() != Material.ENCHANTED_BOOK) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getDisplayName().startsWith(PREFIX);
    }

    /**
     * 능력서에서 능력 코드를 파싱합니다.
     *
     * @param item 능력서 아이템
     * @return 능력 코드, 파싱 실패 시 -1
     */
    public static int parseAbilityCode(ItemStack item) {
        if (!isAbilityBook(item)) return -1;

        String displayName = item.getItemMeta().getDisplayName();
        String afterPrefix = displayName.substring(PREFIX.length());
        int dotIndex = afterPrefix.indexOf('.');
        if (dotIndex <= 0) return -1;

        try {
            int code = Integer.parseInt(afterPrefix.substring(0, dotIndex));
            if (code < 0 || code >= AbilityRegistry.AbilityList.size()) return -1;
            return code;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
