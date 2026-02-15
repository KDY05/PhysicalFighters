package io.github.kdy05.physicalFighters.ability

import io.github.kdy05.physicalFighters.PhysicalFighters
import io.github.kdy05.physicalFighters.game.EventManager
import io.github.kdy05.physicalFighters.util.BaseItem
import io.github.kdy05.physicalFighters.util.EventData
import io.github.kdy05.physicalFighters.util.TimerBase
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.util.UUID

abstract class Ability protected constructor(val spec: AbilitySpec, private val playerUuid: UUID) {

    val abilityName: String = spec.name
    val abilityType: Type = spec.type
    val rank: Rank = spec.rank
    val coolDown: Int = spec.cooldown
    val duration: Int = spec.duration
    private val showText: ShowText = spec.showText
    val guide: List<String> = spec.guide
    val minimumPlayers: Int get() = spec.minimumPlayers
    val isDeathExempt: Boolean get() = spec.isDeathExempt
    val isInfoPrimary: Boolean get() = spec.isInfoPrimary

    private val cTimer = CoolDownTimer(this)
    private val dTimer = DurationTimer(this, cTimer)

    val player: Player? get() = Bukkit.getPlayer(playerUuid)

    enum class Type(private val text: Array<String>) {
        PassiveAutoMatic(arrayOf("패시브", "자동")),
        PassiveManual(arrayOf("패시브", "수동")),
        ActiveImmediately(arrayOf("액티브", "즉발")),
        ActiveContinue(arrayOf("액티브", "지속"));

        val isActive: Boolean
            get() = this == ActiveContinue || this == ActiveImmediately

        override fun toString(): String =
            "${ChatColor.GREEN}${text[0]}${ChatColor.WHITE} / ${ChatColor.GOLD}${text[1]}${ChatColor.WHITE}"
    }

    enum class Rank(private val s: String) {
        SSS("${ChatColor.DARK_PURPLE}Special Rank"),
        SS("${ChatColor.GOLD}SS Rank"),
        S("${ChatColor.RED}S Rank"),
        A("${ChatColor.GREEN}A Rank"),
        B("${ChatColor.BLUE}B Rank"),
        C("${ChatColor.YELLOW}C Rank"),
        F("${ChatColor.BLACK}F Rank"),
        GOD("${ChatColor.WHITE}신");

        override fun toString(): String = "$s${ChatColor.WHITE}"
    }

    enum class ShowText {
        AllText, NoCoolDownText, NoDurationText, CustomText
    }

    enum class Usage(private val s: String) {
        IronLeft("철괴 좌클릭"), IronRight("철괴 우클릭"),
        IronAttack("철괴 타격"), GoldRight("금괴 우클릭"),
        GoldLeft("금괴 좌클릭"), Passive("패시브");

        override fun toString(): String =
            "${ChatColor.GRAY}($s) ${ChatColor.WHITE}"
    }

    companion object {
        @JvmField
        val plugin: PhysicalFighters = PhysicalFighters.plugin

        @JvmField
        val DefaultItem: Material = Material.IRON_INGOT
    }

    // --- Overridable hooks ---

    abstract fun registerEvents()

    abstract fun checkCondition(event: Event?, customData: Int): Int

    abstract fun applyEffect(event: Event?, customData: Int)

    open fun onActivate(p: Player) {}

    open fun onDeactivate(p: Player) {}

    open fun onCooldownStart() {}

    open fun onCooldownEnd() {}

    open fun onDurationStart() {}

    open fun onDurationEnd() {}

    open fun onDurationFinalize() {}

    // --- Final methods ---

    fun registerLeftClickEvent() {
        EventManager.registerLeftClick(this)
    }

    fun registerRightClickEvent() {
        EventManager.registerRightClick(this)
    }

    fun isValidItem(material: Material): Boolean {
        val player = player ?: return false
        return player.inventory.itemInMainHand.type == material
    }

    fun isOwner(e: Entity): Boolean = e is Player && isOwner(e)

    fun isOwner(p: Player): Boolean = p.uniqueId == playerUuid

    fun sendMessage(message: String) {
        player?.sendMessage(message)
    }

    fun execute(event: Event?, customData: Int) {
        val player = player ?: return

        if (this is BaseItem && event != null) {
            when (customData) {
                BaseItem.ITEM_DROP_EVENT -> { handleItemDrop(event); return }
                BaseItem.ITEM_RESPAWN_EVENT -> { handleItemRespawn(event); return }
                BaseItem.ITEM_DEATH_EVENT -> { handleItemDeath(event); return }
            }
        }

        val data = checkCondition(event, customData)
        if (data < 0) return

        if (abilityType.isActive) {
            // 지속 시간 알림 후 종료
            if (dTimer.isRunning) {
                player.sendMessage("${ChatColor.WHITE}${dTimer.count}초${ChatColor.GREEN} 후 지속시간이 끝납니다.")
                return
            }
            // 쿨타임 알림 후 종료
            if (cTimer.isRunning) {
                if (showText != ShowText.NoCoolDownText) {
                    player.sendMessage("${ChatColor.WHITE}${cTimer.count}초${ChatColor.RED} 후 능력을 다시 사용하실 수 있습니다.")
                }
                return
            }
            // 능력 사용 알림
            if (showText != ShowText.CustomText) {
                player.sendMessage("${ChatColor.LIGHT_PURPLE}능력을 사용했습니다.")
            }
        }

        when (abilityType) {
            Type.ActiveContinue -> dTimer.startTimer(duration, true)
            Type.ActiveImmediately -> {
                applyEffect(event, data)
                if (coolDown != 0) cTimer.startTimer(coolDown, true)
            }
            else -> applyEffect(event, data)
        }
    }

    // Life cycle

    fun activate(textout: Boolean) {
        registerEvents()
        if (this is BaseItem) {
            EventManager.registerPlayerDropItem(EventData(this, BaseItem.ITEM_DROP_EVENT))
            EventManager.registerPlayerRespawn(EventData(this, BaseItem.ITEM_RESPAWN_EVENT))
            EventManager.registerEntityDeath(EventData(this, BaseItem.ITEM_DEATH_EVENT))
        }
        val player = player
        if (player != null) {
            if (textout) {
                player.sendMessage("${ChatColor.GREEN}$abilityName${ChatColor.WHITE} 능력이 설정되었습니다.")
            }
            onActivate(player)
            if (this is BaseItem) {
                giveBaseItem(player)
            }
        }
    }

    fun deactivate(textout: Boolean) {
        cancelDTimer()
        cancelCTimer()
        val player = player
        if (player != null) {
            if (textout) {
                player.sendMessage("${ChatColor.RED}$abilityName${ChatColor.WHITE} 능력이 해제되었습니다.")
            }
            if (this is BaseItem) {
                removeBaseItem(player)
            }
            onDeactivate(player)
        }
        unregisterEvents()
    }

    fun unregisterEvents() {
        EventManager.unregisterAll(this)
    }

    // Timer Managing

    fun cancelDTimer() { dTimer.stopTimer() }

    fun cancelCTimer() { cTimer.stopTimer() }

    val isDurationRunning: Boolean get() = dTimer.isRunning


    // --- Timer Classes ---

    private class CoolDownTimer(private val ability: Ability) : TimerBase() {

        override fun onTimerStart() {
            ability.onCooldownStart()
        }

        override fun onTimerRunning(count: Int) {
            val showText = ability.showText
            if (count in 1..3 && showText != ShowText.NoCoolDownText && showText != ShowText.CustomText) {
                ability.player?.sendMessage(
                    "${ChatColor.RED}${count}초 뒤${ChatColor.WHITE} 능력사용이 가능합니다."
                )
            }
        }

        override fun onTimerEnd() {
            ability.onCooldownEnd()
            if (ability.showText != ShowText.CustomText) {
                ability.player?.sendMessage("${ChatColor.AQUA}다시 능력을 사용할 수 있습니다.")
            }
        }
    }

    private class DurationTimer(
        private val ability: Ability,
        private val ctimer: CoolDownTimer
    ) : TimerBase() {

        override fun onTimerStart() {
            ability.onDurationStart()
        }

        override fun onTimerRunning(count: Int) {
            ability.player ?: return
            val showText = ability.showText
            if (count in 1..3 && showText != ShowText.NoDurationText && showText != ShowText.CustomText) {
                ability.player?.sendMessage(
                    "${ChatColor.GREEN}지속 시간${ChatColor.WHITE} ${count}초 전"
                )
            }
        }

        override fun onTimerEnd() {
            ability.onDurationEnd()
            if (ability.showText != ShowText.CustomText) {
                ability.player?.sendMessage("${ChatColor.GREEN}능력 지속시간이 끝났습니다.")
            }
            ctimer.startTimer(ability.coolDown, true)
        }

        override fun onTimerFinalize() {
            ability.onDurationFinalize()
        }
    }
}
