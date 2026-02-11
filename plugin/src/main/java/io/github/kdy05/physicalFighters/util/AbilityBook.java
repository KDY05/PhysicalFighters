package io.github.kdy05.physicalFighters.util;

import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
import io.github.kdy05.physicalFighters.ability.AbilityType;
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
     * @param abilityName 능력 이름
     * @return 생성된 능력서 ItemStack, 실패 시 null
     */
    public static ItemStack create(String abilityName) {
        AbilityType type = AbilityRegistry.getType(abilityName);
        if (type == null) return null;

        ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(PREFIX + abilityName);
        meta.setLore(new LinkedList<>(Arrays.asList(type.getGuide())));
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
     * 능력서에서 능력 이름을 파싱합니다.
     *
     * @param item 능력서 아이템
     * @return 능력 이름, 파싱 실패 시 null
     */
    public static String parseAbilityName(ItemStack item) {
        if (!isAbilityBook(item)) return null;

        String displayName = item.getItemMeta().getDisplayName();
        String afterPrefix = displayName.substring(PREFIX.length());

        // 레거시 형식 호환: "42. 불사조" → "불사조"
        int dotIndex = afterPrefix.indexOf(". ");
        if (dotIndex >= 0) {
            String beforeDot = afterPrefix.substring(0, dotIndex);
            try {
                Integer.parseInt(beforeDot);
                afterPrefix = afterPrefix.substring(dotIndex + 2);
            } catch (NumberFormatException ignored) {
                // 숫자가 아니면 레거시 형식이 아님
            }
        }

        if (AbilityRegistry.getType(afterPrefix) != null) {
            return afterPrefix;
        }
        return null;
    }
}
