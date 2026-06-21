package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.scheduler.BukkitRunnable

@AbilityMeta(
    name = "에이스",
    rank = Rank.S,
    guide = [
        "(철괴 좌클릭) 능력 지속시간 동안 자신의 주변에 있는 적들을 불태웁니다.",
        "(패시브) 화염 대미지를 무시합니다."
    ]
)
class Ace(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    private val continueSkill = object : ActiveContinueSkill() {
        override val cooldownTicks = 800L
        override val durationTicks = 400L
        private var splashTask: Any? = null
        private var indicatorTask: BukkitRunnable? = null

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                activate()
            }
        }

        override fun onActivate() {
            splashTask = context.scheduleRepeat(30L) {
                AbilityUtils.splashTask(owner, owner.location, 10.0) { entity ->
                    entity.fireTicks = 80
                }
            }
            indicatorTask = AbilityUtils.createCircleIndicator(owner, 10.0, Particle.FLAME)
        }

        override fun onDeactivate() {
            splashTask?.let { context.cancelSchedule(it) }
            splashTask = null
            indicatorTask?.cancel()
            indicatorTask = null
        }
    }

    private val passiveSkill = object : PassiveSkill() {
        override fun register() {
            on(EntityDamageEvent::class) { e ->
                if (e.entity.uniqueId != owner.uniqueId) return@on
                when (e.cause) {
                    EntityDamageEvent.DamageCause.FIRE,
                    EntityDamageEvent.DamageCause.FIRE_TICK -> e.isCancelled = true
                    else -> {}
                }
            }
        }
    }

    override fun buildSkills() = listOf(continueSkill, passiveSkill)
}
