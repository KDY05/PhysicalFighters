package io.github.kdy05.physicalFighters.util

import io.github.kdy05.physicalFighters.PhysicalFighters
import io.github.kdy05.physicalFighters.ability.Ability
import io.github.kdy05.physicalFighters.ability.AbilitySpec
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

abstract class BaseItemAbility @JvmOverloads protected constructor(
    spec: AbilitySpec,
    playerUuid: UUID,
    plugin: PhysicalFighters = PhysicalFighters.plugin
) : Ability(spec, playerUuid, plugin) {

    abstract fun getBaseItem(): Array<ItemStack>
    abstract fun getItemName(): String

    // Template method: registers item events automatically, then delegates to subclass
    final override fun registerEvents() {
        registerAbilityEvents()
        eventRegistry.registerPlayerDropItem(EventData(this, ITEM_DROP_EVENT))
        eventRegistry.registerPlayerRespawn(EventData(this, ITEM_RESPAWN_EVENT))
        eventRegistry.registerEntityDeath(EventData(this, ITEM_DEATH_EVENT))
    }

    // Subclasses implement this instead of registerEvents()
    abstract fun registerAbilityEvents()

    override fun onActivate(p: Player) {
        super.onActivate(p)
        giveBaseItem(p)
    }

    override fun onDeactivate(p: Player) {
        super.onDeactivate(p)
        removeBaseItem(p)
    }

    // Intercepts item events before delegating to normal execute() flow
    override fun execute(event: Event?, customData: Int) {
        if (event != null) {
            when (customData) {
                ITEM_DROP_EVENT -> { handleItemDrop(event); return }
                ITEM_RESPAWN_EVENT -> { handleItemRespawn(event); return }
                ITEM_DEATH_EVENT -> { handleItemDeath(event); return }
            }
        }
        super.execute(event, customData)
    }

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
