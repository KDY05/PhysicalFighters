package io.github.kdy05.physicalFighters.game

import io.github.kdy05.abilityAPI.AbilityAPI
import io.github.kdy05.abilityAPI.ability.Ability
import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.skill.ActiveContinueSkill
import io.github.kdy05.abilityAPI.skill.CooldownSkillBase
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.PhysicalFighters
import io.github.kdy05.physicalFighters.util.toDisplayString
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Event
import kotlin.reflect.KClass

object GameUtils {

    @JvmStatic
    fun assignAbility(sender: CommandSender, abilityName: String, target: Player) {
        val type = AbilityAPI.service.registrar.getByName(abilityName)
        if (type == null) {
            sender.sendMessage("${ChatColor.RED}존재하지 않는 능력입니다.")
            return
        }

        AbilityAPI.service.clearAbilities(target)
        AbilityAPI.service.giveAbility(target, type)

        val name = type.getAnnotation(AbilityMeta::class.java)?.name ?: type.simpleName ?: "Unknown"
        sender.sendMessage(
            "${ChatColor.GREEN}${target.name}${ChatColor.WHITE}님에게 " +
                "${ChatColor.GREEN}${name}${ChatColor.WHITE} 능력 할당이 완료되었습니다."
        )
        val senderName = if (sender is Player) sender.name else "Console"
        PhysicalFighters.plugin.logger.info(
            "${senderName}님이 ${target.name}님에게 $name 능력을 할당했습니다."
        )
    }

    @JvmStatic
    fun showInfo(player: Player, pendingType: Class<out Ability>? = null) {
        val abilityClass: Class<out Ability>
        val abilityInstance: Ability?
        if (pendingType != null) {
            abilityClass = pendingType
            abilityInstance = runCatching {
                abilityClass.getDeclaredConstructor(Player::class.java, SkillContext::class.java)
                    .newInstance(player, StubSkillContext)
            }.getOrNull()
        } else {
            val ability = AbilityAPI.service.getPrimaryAbility(player)
                ?: AbilityAPI.service.getAbilities(player).firstOrNull()
            if (ability == null) {
                player.sendMessage("${ChatColor.RED}능력이 없거나 옵저버입니다.")
                return
            }
            abilityClass = ability.javaClass
            abilityInstance = ability
        }

        val meta = abilityClass.getAnnotation(AbilityMeta::class.java)
        buildList {
            add("${ChatColor.GREEN}---------------")
            add("${ChatColor.GOLD}- 능력 정보 -")
            if (meta != null) {
                add("${ChatColor.AQUA}${meta.name} ${ChatColor.WHITE}| ${meta.rank.toDisplayString()}")
                addAll(meta.guide.map { line ->
                    line.replace(Regex("\\(([^)]+)\\)"), "${ChatColor.GRAY}($1)${ChatColor.WHITE}")
                })
            } else {
                add("${ChatColor.AQUA}${abilityClass.simpleName}")
            }
            if (abilityInstance != null) {
                val skills = abilityInstance.skills()
                val cooldownSkill = skills.filterIsInstance<CooldownSkillBase>().firstOrNull()
                val continueSkill = skills.filterIsInstance<ActiveContinueSkill>().firstOrNull()
                val cooldown = if (cooldownSkill != null) "${cooldownSkill.cooldownTicks / 20}초" else "없음"
                val duration = if (continueSkill != null) "${continueSkill.durationTicks / 20}초" else "없음"
                add("${ChatColor.RED}쿨타임 : ${ChatColor.WHITE}$cooldown / ${ChatColor.RED}지속시간 : ${ChatColor.WHITE}$duration")
            }
            add("${ChatColor.GREEN}---------------")
        }.forEach { player.sendMessage(it) }
    }

    private object StubSkillContext : SkillContext {
        override fun <T : Event> subscribe(eventClass: KClass<T>, handler: (T) -> Unit): Any = Unit
        override fun unsubscribe(token: Any) {}
        override fun scheduleOnce(delayTicks: Long, task: () -> Unit): Any = Unit
        override fun scheduleRepeat(periodTicks: Long, task: () -> Unit): Any = Unit
        override fun cancelSchedule(token: Any) {}
        override fun isSilenced(player: Player): Boolean = false
        override fun <T : Event> callEvent(event: T): T = event
    }

    /**
     * OnKill 설정에 따라 사망 처리를 실행합니다.
     * (관전자 모드 전환 / 킥 / 밴)
     */
    @JvmStatic
    fun applyDeathPenalty(victim: Player) {
        val plugin = PhysicalFighters.plugin
        val onKill = plugin.configManager.onKill
        if (onKill <= 0) return

        when (onKill) {
            1 -> {
                val deathLocation = victim.location.clone()
                Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                    victim.gameMode = GameMode.SPECTATOR
                    victim.spigot().respawn()
                    victim.teleport(deathLocation)
                    victim.sendTitle(
                        "${ChatColor.RED}사망하였습니다!",
                        "${ChatColor.YELLOW}관전자 모드로 전환합니다.", 10, 100, 10
                    )
                }, 1L)
            }
            2 -> victim.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.")
            3 -> {
                if (!victim.isOp) {
                    Bukkit.getBanList(BanList.Type.NAME).addBan(
                        victim.name,
                        "당신은 죽었습니다. 다시 들어오실 수 없습니다.", null, null
                    )
                    victim.kickPlayer("당신은 죽었습니다. 다시 들어오실 수 없습니다.")
                } else {
                    victim.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.")
                }
            }
        }
    }
}
