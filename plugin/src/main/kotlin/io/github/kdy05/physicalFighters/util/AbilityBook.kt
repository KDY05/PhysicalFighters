package io.github.kdy05.physicalFighters.util

import io.github.kdy05.physicalFighters.ability.AbilityRegistry
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object AbilityBook {

    private val PREFIX = "${ChatColor.GOLD}[능력서]${ChatColor.WHITE}"

    fun create(abilityName: String): ItemStack? {
        val type = AbilityRegistry.getType(abilityName) ?: return null

        val stack = ItemStack(Material.ENCHANTED_BOOK)
        val meta = stack.itemMeta ?: return null
        meta.setDisplayName("$PREFIX$abilityName")
        meta.lore = type.guide.toList()
        stack.itemMeta = meta
        return stack
    }

    fun isAbilityBook(item: ItemStack?): Boolean {
        if (item == null || item.type != Material.ENCHANTED_BOOK) return false
        val meta = item.itemMeta ?: return false
        return meta.displayName.startsWith(PREFIX)
    }

    fun parseAbilityName(item: ItemStack?): String? {
        if (!isAbilityBook(item)) return null

        val displayName = item!!.itemMeta!!.displayName
        var afterPrefix = displayName.substring(PREFIX.length)

        // 레거시 형식 호환: "42. 불사조" → "불사조"
        val dotIndex = afterPrefix.indexOf(". ")
        if (dotIndex >= 0) {
            val beforeDot = afterPrefix.substring(0, dotIndex)
            if (beforeDot.toIntOrNull() != null) {
                afterPrefix = afterPrefix.substring(dotIndex + 2)
            }
        }

        return if (AbilityRegistry.getType(afterPrefix) != null) afterPrefix else null
    }
}
