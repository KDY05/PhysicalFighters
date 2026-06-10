package io.github.kdy05.physicalFighters.config

import io.github.kdy05.physicalFighters.PhysicalFighters
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ConfigSettingsGUI(
    plugin: PhysicalFighters,
    private val configManager: ConfigManager
) : Listener {

    companion object {
        private const val TITLE = "§6§l게임 설정"
    }

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun open(player: Player) {
        val inv = Bukkit.createInventory(null, 27, TITLE)
        refreshItems(inv)
        player.openInventory(inv)
    }

    private fun refreshItems(inv: Inventory) {
        inv.setItem(0, onKillItem())
        inv.setItem(1, boolItem(Material.NAME_TAG, "처치자 공개", configManager.isKillerOutput))
        inv.setItem(2, intItem(Material.EXPERIENCE_BOTTLE, "시작 레벨", configManager.setLev))
        inv.setItem(3, intItem(Material.TOTEM_OF_UNDYING, "초반 무적 (분)", configManager.earlyInvincibleTime))
        inv.setItem(4, boolItem(Material.CHEST, "인벤토리 초기화", configManager.isClearInventory))
        inv.setItem(5, boolItem(Material.BOOK, "능력 추첨 생략", configManager.isNoAbilitySetting))
        inv.setItem(9, boolItem(Material.COOKED_BEEF, "배고픔 무한", configManager.isNoFoodMode))
        inv.setItem(10, boolItem(Material.ANVIL, "내구도 무한", configManager.isInfinityDur))
    }

    private fun onKillItem(): ItemStack {
        val label = when (configManager.onKill) {
            0 -> "없음"; 1 -> "관전자"; 2 -> "킥"; 3 -> "밴"; else -> "?"
        }
        return makeItem(Material.IRON_SWORD, "§6사망 처리  §f$label",
            "§7좌클릭: 다음  §7우클릭: 이전",
            "§8없음 → 관전자 → 킥 → 밴"
        )
    }

    private fun boolItem(material: Material, name: String, value: Boolean): ItemStack {
        val state = if (value) "§a[ON]" else "§c[OFF]"
        return makeItem(material, "§6$name  $state", "§7클릭하여 토글")
    }

    private fun intItem(material: Material, name: String, value: Int): ItemStack {
        return makeItem(material, "§6$name  §f$value",
            "§7좌클릭 +1  §7우클릭 -1",
            "§7Shift+좌클릭 +10  §7Shift+우클릭 -10"
        )
    }

    private fun makeItem(material: Material, name: String, vararg lore: String): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta ?: return item
        meta.setDisplayName(name)
        meta.lore = lore.toList()
        item.itemMeta = meta
        return item
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.view.title != TITLE) return
        if (event.clickedInventory == event.view.topInventory || event.isShiftClick) {
            event.isCancelled = true
        }
        if (event.clickedInventory != event.view.topInventory) return

        val right = event.click == ClickType.RIGHT || event.click == ClickType.SHIFT_RIGHT
        val shift = event.isShiftClick

        when (event.slot) {
            0 -> configManager.updateOnKill(if (right) (configManager.onKill - 1 + 4) % 4 else (configManager.onKill + 1) % 4)
            1 -> configManager.updateKillerOutput(!configManager.isKillerOutput)
            2 -> configManager.updateSetLev(maxOf(0, configManager.setLev + if (right) -(if (shift) 10 else 1) else if (shift) 10 else 1))
            3 -> configManager.updateEarlyInvincibleTime(maxOf(0, configManager.earlyInvincibleTime + if (right) -(if (shift) 10 else 1) else if (shift) 10 else 1))
            4 -> configManager.updateClearInventory(!configManager.isClearInventory)
            5 -> configManager.updateNoAbilitySetting(!configManager.isNoAbilitySetting)
            9 -> configManager.updateNoFoodMode(!configManager.isNoFoodMode)
            10 -> configManager.updateInfinityDur(!configManager.isInfinityDur)
            else -> return
        }

        refreshItems(event.view.topInventory)
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        if (event.view.title == TITLE) event.isCancelled = true
    }
}
