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

class EventManager(private val plugin: PhysicalFighters) : Listener, EventRegistry {

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

    private val allEventDataLists = listOf(
        onEntityTarget, onEntityDamage, onEntityDamageByEntity, onEntityDeath,
        onPlayerRespawn, onBlockBreakEvent, onSignChangeEvent,
        onProjectileLaunchEvent, onPlayerDropItem, onPlayerMoveEvent, onProjectileHitEvent
    )

    // --- EventRegistry 구현 ---

    override fun registerLeftClick(ability: Ability) { leftClickHandlers.add(ability) }
    override fun registerRightClick(ability: Ability) { rightClickHandlers.add(ability) }
    override fun registerEntityTarget(data: EventData) { onEntityTarget.add(data) }
    override fun registerEntityDamage(data: EventData) { onEntityDamage.add(data) }
    override fun registerEntityDamageByEntity(data: EventData) { onEntityDamageByEntity.add(data) }
    override fun registerEntityDeath(data: EventData) { onEntityDeath.add(data) }
    override fun registerPlayerRespawn(data: EventData) { onPlayerRespawn.add(data) }
    override fun registerBlockBreak(data: EventData) { onBlockBreakEvent.add(data) }
    override fun registerSignChange(data: EventData) { onSignChangeEvent.add(data) }
    override fun registerProjectileLaunch(data: EventData) { onProjectileLaunchEvent.add(data) }
    override fun registerPlayerDropItem(data: EventData) { onPlayerDropItem.add(data) }
    override fun registerPlayerMove(data: EventData) { onPlayerMoveEvent.add(data) }
    override fun registerProjectileHit(data: EventData) { onProjectileHitEvent.add(data) }

    override fun unregisterAll(ability: Ability) {
        leftClickHandlers.remove(ability)
        rightClickHandlers.remove(ability)
        allEventDataLists.forEach { list -> list.removeAll { it.ability === ability } }
    }

    // --- @EventHandler ---

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
        if (event is PlayerDeathEvent) {
            val victim = event.entity
            AbilityRegistry.findAbilities(victim).forEach { it.cancelDTimer() }

            // executeAbility를 handleVictim보다 먼저 실행해야 함:
            // handleVictim → applyDeathPenalty → kickPlayer()가 동기 실행되면
            // Bukkit.getPlayer(uuid)가 null을 반환하여 능력(미러링 등)이 발동 안 됨.
            executeAbility(onEntityDeath, event)

            if (plugin.gameManager.scenario == GameManager.ScriptStatus.GameStart) {
                val killer = victim.killer
                handleVictim(victim)
                printDeathMessage(event, killer, victim)
            }
        } else {
            executeAbility(onEntityDeath, event)
        }
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
        val (handlers, parameter) = when (event.action) {
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> leftClickHandlers to 0
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> rightClickHandlers to 1
            else -> return
        }
        for (ability in handlers) {
            ability.execute(event, parameter)
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
