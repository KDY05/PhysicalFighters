package io.github.kdy05.physicalFighters.game

import io.github.kdy05.abilityAPI.AbilityAPI
import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.physicalFighters.PhysicalFighters
import io.github.kdy05.physicalFighters.util.AbilityBook
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.entity.Player

class GameEventListener(private val plugin: PhysicalFighters) : Listener {

    @EventHandler
    fun onPlayerItemDamage(event: PlayerItemDamageEvent) {
        if (plugin.configManager.isInfinityDur) event.isCancelled = true
    }

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (plugin.configManager.isNoFoodMode) event.foodLevel = 20
    }

    // damageGuard 활성 시 Bukkit 이벤트 자체도 취소 (AbilityAPI의 damageGuard는 스킬 핸들러만 차단)
    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player && AbilityAPI.service.damageGuard) {
            event.isCancelled = true
            event.entity.fireTicks = 0
        }
    }

    @EventHandler
    fun onEntityTarget(event: EntityTargetEvent) {
        if (event.target is Player && AbilityAPI.service.damageGuard) {
            event.target = null
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val victim = event.entity
        if (plugin.gameManager.scenario != GameManager.ScriptStatus.GameStart) return

        val killer = victim.killer
        handleVictim(victim)
        printDeathMessage(event, killer, victim)
    }

    private fun handleVictim(victim: Player) {
        val hasDeathExempt = AbilityAPI.service.getAbilities(victim)
            .any { it.javaClass.getAnnotation(AbilityMeta::class.java)?.isDeathExempt == true }
        if (!hasDeathExempt) {
            GameUtils.applyDeathPenalty(victim)
        }
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
        val player = event.player
        val handItem = player.inventory.itemInMainHand
        val bookAbility = AbilityBook.parseAbilityName(handItem)
        if (bookAbility != null) {
            player.inventory.setItemInMainHand(ItemStack(Material.AIR))
            GameUtils.assignAbility(player, bookAbility, player)
        }
    }
}
