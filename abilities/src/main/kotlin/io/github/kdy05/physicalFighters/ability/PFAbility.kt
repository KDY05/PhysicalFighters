package io.github.kdy05.physicalFighters.ability

import io.github.kdy05.abilityAPI.ability.Ability
import io.github.kdy05.abilityAPI.skill.ActiveContinueSkill as APIActiveContinueSkill
import io.github.kdy05.abilityAPI.skill.ActiveSkill as APIActiveSkill
import io.github.kdy05.abilityAPI.skill.PassiveSkill as APIPassiveSkill
import io.github.kdy05.abilityAPI.skill.SkillContext
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack

abstract class PFAbility(owner: Player, context: SkillContext) : Ability(owner, context) {

    abstract inner class PassiveSkill : APIPassiveSkill(owner, context)

    abstract inner class ActiveSkill : APIActiveSkill(owner, context) {
        override fun onCooldownAttempt(remainingSeconds: Int) {
            owner.sendMessage(
                "${ChatColor.WHITE}${remainingSeconds}초${ChatColor.RED} 후 능력을 다시 사용할 수 있습니다.")
        }

        override fun onCooldownRunning(remainingSeconds: Int) {
            if (remainingSeconds in 1..3) {
                owner.sendMessage(
                    "${ChatColor.RED}${remainingSeconds}초${ChatColor.WHITE} 후 능력 사용이 가능합니다.")
            }
        }

        override fun onCooldownEnd() {
            owner.sendMessage("${ChatColor.AQUA}다시 능력을 사용할 수 있습니다.")
        }

        override fun onPostActivate() {
            owner.sendMessage("${ChatColor.LIGHT_PURPLE}능력을 사용했습니다.")
        }
    }

    abstract inner class ActiveContinueSkill : APIActiveContinueSkill(owner, context) {
        override fun onCooldownAttempt(remainingSeconds: Int) {
            owner.sendMessage(
                "${ChatColor.WHITE}${remainingSeconds}초${ChatColor.RED} 후 능력을 다시 사용할 수 있습니다.")
        }

        override fun onCooldownRunning(remainingSeconds: Int) {
            if (remainingSeconds in 1..3) {
                owner.sendMessage(
                    "${ChatColor.RED}${remainingSeconds}초${ChatColor.WHITE} 후 능력 사용이 가능합니다.")
            }
        }

        override fun onCooldownEnd() {
            owner.sendMessage("${ChatColor.AQUA}다시 능력을 사용할 수 있습니다.")
        }

        override fun onPostActivate() {
            owner.sendMessage("${ChatColor.LIGHT_PURPLE}능력을 사용했습니다.")
        }

        override fun onActiveAttempt(remainingSeconds: Int) {
            owner.sendMessage(
                "${ChatColor.WHITE}${remainingSeconds}초${ChatColor.GREEN} 후 지속 시간이 끝납니다.")
        }

        override fun onActiveRunning(remainingSeconds: Int) {
            if (remainingSeconds in 1..3)
                owner.sendMessage("${ChatColor.GREEN}지속 시간${ChatColor.WHITE} ${remainingSeconds}초 전")
        }

        override fun onActiveEnd() {
            owner.sendMessage("${ChatColor.GREEN}능력 지속 시간이 끝났습니다.")
        }
    }

    abstract inner class BaseItemSkill : APIPassiveSkill(owner, context) {
        abstract val baseItems: Array<ItemStack>
        abstract val itemDisplayName: String

        override fun onStart() {
            giveBaseItem()
            register()
        }

        override fun onStop() {
            removeBaseItem()
            unsubscribeAll()
        }

        protected fun registerItemEvents() {
            on(PlayerDropItemEvent::class) { e ->
                if (e.player.uniqueId != owner.uniqueId) return@on
                val matched = baseItems.firstOrNull { it.type == e.itemDrop.itemStack.type } ?: return@on
                if (owner.inventory.contains(matched.type, matched.amount)) return@on
                e.player.sendMessage("§c${itemDisplayName}(은/는) 버릴 수 없습니다.")
                e.isCancelled = true
            }
            on(PlayerRespawnEvent::class) { e ->
                if (e.player.uniqueId != owner.uniqueId) return@on
                e.player.sendMessage("§a${itemDisplayName}(이/가) 지급됩니다.")
                context.scheduleOnce(1L) { giveBaseItem() }
            }
            on(EntityDeathEvent::class) { e ->
                if (e.entity.uniqueId != owner.uniqueId) return@on
                val dropTypes = baseItems.map { it.type }.toSet()
                e.drops.removeIf { it.type in dropTypes }
            }
        }

        fun giveBaseItem() {
            val inv = owner.inventory
            for (item in baseItems) {
                val slot = (0 until 36).firstOrNull { inv.getItem(it) == null } ?: break
                inv.setItem(slot, item.clone())
            }
        }

        fun removeBaseItem() {
            for (item in baseItems) {
                owner.inventory.removeItem(item)
            }
        }
    }
}
