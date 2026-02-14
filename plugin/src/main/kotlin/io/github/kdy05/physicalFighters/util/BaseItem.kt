package io.github.kdy05.physicalFighters.util

import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack

interface BaseItem {
    val player: Player?
    fun getBaseItem(): Array<ItemStack>
    fun getItemName(): String
    fun isOwner(e: Entity): Boolean

    fun handleItemDrop(event: Event) {
        val dropEvent = event as PlayerDropItemEvent
        if (!isOwner(dropEvent.player)) return

        val droppedType = dropEvent.itemDrop.itemStack.type
        val matched = getBaseItem().firstOrNull { it.type == droppedType } ?: return
        val inv = dropEvent.player.inventory
        if (inv.contains(droppedType, matched.amount)) return

        dropEvent.player.sendMessage("${ChatColor.RED}${getItemName()}(은/는) 버릴 수 없습니다.")
        dropEvent.isCancelled = true
    }

    fun handleItemRespawn(event: Event) {
        val respawnEvent = event as PlayerRespawnEvent
        if (!isOwner(respawnEvent.player)) return

        respawnEvent.player.sendMessage("${ChatColor.GREEN}${getItemName()}(이/가) 지급됩니다.")
        giveBaseItem(respawnEvent.player)
    }

    fun handleItemDeath(event: Event) {
        val deathEvent = event as EntityDeathEvent
        if (!isOwner(deathEvent.entity)) return

        val dropTypes = getBaseItem().map { it.type }.toSet()
        deathEvent.drops.removeIf { it.type in dropTypes }
    }

    fun giveBaseItem(player: Player) {
        val inventory = player.inventory
        for (item in getBaseItem()) {
            val emptySlot = (0 until 36).firstOrNull { inventory.getItem(it) == null } ?: break
            inventory.setItem(emptySlot, item.clone())
        }
    }

    fun removeBaseItem(player: Player) {
        for (item in getBaseItem()) {
            player.inventory.removeItem(item)
        }
    }

    companion object {
        const val ITEM_DROP_EVENT = 1001
        const val ITEM_RESPAWN_EVENT = 1002
        const val ITEM_DEATH_EVENT = 1003
    }
}