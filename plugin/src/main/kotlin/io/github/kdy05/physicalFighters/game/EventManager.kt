package io.github.kdy05.physicalFighters.game

import io.github.kdy05.physicalFighters.PhysicalFighters
import io.github.kdy05.physicalFighters.ability.Ability
import io.github.kdy05.physicalFighters.ability.AbilityRegistry
import io.github.kdy05.physicalFighters.util.AbilityBook
import io.github.kdy05.physicalFighters.util.EventData
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

class EventManager(private val plugin: PhysicalFighters) : Listener {

    companion object {
        private val leftClickHandlers = mutableListOf<Ability>()
        private val rightClickHandlers = mutableListOf<Ability>()

        private val onEntityTarget = mutableListOf<EventData>()
        private val onEntityDamage = mutableListOf<EventData>()
        private val onEntityDamageByEntity = mutableListOf<EventData>()
        private val onEntityDeath = mutableListOf<EventData>()
        private val onPlayerRespawn = mutableListOf<EventData>()
        private val onBlockBreakEvent = mutableListOf<EventData>()
        private val onSignChangeEvent = mutableListOf<EventData>()
        private val onProjectileLaunchEvent = mutableListOf<EventData>()
        private val onPlayerDropItem = mutableListOf<EventData>()
        private val onPlayerMoveEvent = mutableListOf<EventData>()
        private val onProjectileHitEvent = mutableListOf<EventData>()

        // --- 이벤트 등록 API ---

        @JvmStatic fun registerLeftClick(ability: Ability) { leftClickHandlers.add(ability) }
        @JvmStatic fun registerRightClick(ability: Ability) { rightClickHandlers.add(ability) }

        @JvmStatic fun registerEntityTarget(data: EventData) { onEntityTarget.add(data) }
        @JvmStatic fun registerEntityDamage(data: EventData) { onEntityDamage.add(data) }
        @JvmStatic fun registerEntityDamageByEntity(data: EventData) { onEntityDamageByEntity.add(data) }
        @JvmStatic fun registerEntityDeath(data: EventData) { onEntityDeath.add(data) }
        @JvmStatic fun registerPlayerRespawn(data: EventData) { onPlayerRespawn.add(data) }
        @JvmStatic fun registerBlockBreak(data: EventData) { onBlockBreakEvent.add(data) }
        @JvmStatic fun registerSignChange(data: EventData) { onSignChangeEvent.add(data) }
        @JvmStatic fun registerProjectileLaunch(data: EventData) { onProjectileLaunchEvent.add(data) }
        @JvmStatic fun registerPlayerDropItem(data: EventData) { onPlayerDropItem.add(data) }
        @JvmStatic fun registerPlayerMove(data: EventData) { onPlayerMoveEvent.add(data) }
        @JvmStatic fun registerProjectileHit(data: EventData) { onProjectileHitEvent.add(data) }

        @JvmStatic
        fun unregisterAll(ability: Ability) {
            leftClickHandlers.remove(ability)
            rightClickHandlers.remove(ability)
            onEntityTarget.removeAll { it.ability === ability }
            onEntityDamage.removeAll { it.ability === ability }
            onEntityDamageByEntity.removeAll { it.ability === ability }
            onEntityDeath.removeAll { it.ability === ability }
            onPlayerRespawn.removeAll { it.ability === ability }
            onBlockBreakEvent.removeAll { it.ability === ability }
            onSignChangeEvent.removeAll { it.ability === ability }
            onProjectileLaunchEvent.removeAll { it.ability === ability }
            onPlayerDropItem.removeAll { it.ability === ability }
            onPlayerMoveEvent.removeAll { it.ability === ability }
            onProjectileHitEvent.removeAll { it.ability === ability }
        }
    }

    @EventHandler
    fun onPlayerItemDamage(event: PlayerItemDamageEvent) {
        if (plugin.configManager.isInfinityDur) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (plugin.configManager.isNoFoodMode) {
            event.foodLevel = 20
        }
    }

    @EventHandler
    fun onEntityTarget(event: EntityTargetEvent) {
        if (event.target is Player) {
            if (InvincibilityManager.isDamageGuard) {
                event.target = null
                event.isCancelled = true
            }
        }
        executeAbility(onEntityTarget, event)
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            if (InvincibilityManager.isDamageGuard) {
                event.isCancelled = true
                event.entity.fireTicks = 0
            }
        }
        // 주의: EntityDamageByEntityEvent는 EntityDamageEvent의 하위 클래스이므로
        // 양쪽 리스트에 같은 능력을 등록하면 이중 실행됩니다.
        if (event is EntityDamageByEntityEvent) {
            executeAbility(onEntityDamageByEntity, event)
        }
        executeAbility(onEntityDamage, event)
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (plugin.gameManager.scenario == GameManager.ScriptStatus.GameStart
            && event is PlayerDeathEvent
        ) {
            val victim = event.entity
            val killer = victim.killer

            handleVictim(victim)
            printDeathMessage(event, killer, victim)
        }
        executeAbility(onEntityDeath, event)
    }

    private fun handleVictim(victim: Player) {
        for (ability in AbilityRegistry.findAbilities(victim)) {
            if (ability.isDeathExempt) return
        }
        GameUtils.applyDeathPenalty(victim)
    }

    private fun printDeathMessage(pde: PlayerDeathEvent, killer: Player?, victim: Player) {
        plugin.logger.info(pde.deathMessage)
        pde.deathMessage = if (killer != null) {
            if (plugin.configManager.isKillerOutput) {
                "${ChatColor.GREEN}${killer.name}${ChatColor.WHITE}님이 " +
                    "${ChatColor.RED}${victim.name}${ChatColor.WHITE}님의 살겠다는 의지를 꺾었습니다."
            } else {
                "${ChatColor.RED}${victim.name}${ChatColor.WHITE}님이 누군가에게 살해당했습니다."
            }
        } else {
            "${ChatColor.RED}${victim.name}${ChatColor.WHITE}님이 대자연에 의해 의지가 꺾였습니다."
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        filterAbilityExecution(event)
        // 능력서 사용
        val player = event.player
        val handItem = player.inventory.itemInMainHand
        val bookAbility = AbilityBook.parseAbilityName(handItem)
        if (bookAbility != null) {
            player.inventory.setItemInMainHand(ItemStack(Material.AIR))
            GameUtils.assignAbility(player, bookAbility, player, plugin.configManager.isAbilityOverLap)
        }
    }

    private fun executeAbility(dataList: List<EventData>, event: Event) {
        for (data in dataList) {
            val ability = data.ability
            if (ability.abilityType == Ability.Type.ActiveContinue
                && ability.player != null && ability.isDurationRunning
            ) {
                ability.applyEffect(event, data.parameter)
            }
            ability.execute(event, data.parameter)
        }
    }

    private fun filterAbilityExecution(event: PlayerInteractEvent) {
        when (event.action) {
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                for (ability in leftClickHandlers) {
                    ability.execute(event, 0)
                }
            }
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                for (ability in rightClickHandlers) {
                    ability.execute(event, 1)
                }
            }
            else -> {}
        }
    }

    // 이하는 이벤트 구독 용도만 수행

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) { executeAbility(onPlayerRespawn, event) }

    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) { executeAbility(onBlockBreakEvent, event) }

    @EventHandler
    fun onSignChangeEvent(event: SignChangeEvent) { executeAbility(onSignChangeEvent, event) }

    @EventHandler
    fun onProjectileLaunchEvent(event: ProjectileLaunchEvent) { executeAbility(onProjectileLaunchEvent, event) }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) { executeAbility(onPlayerDropItem, event) }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) { executeAbility(onPlayerMoveEvent, event) }

    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) { executeAbility(onProjectileHitEvent, event) }
}
