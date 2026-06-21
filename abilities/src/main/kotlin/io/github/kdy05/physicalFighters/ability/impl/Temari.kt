package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

@AbilityMeta(
    name = "테마리",
    rank = Rank.S,
    guide = [
        "(철괴 좌클릭) 능력 지속 시간동안 자신의 주변에 있는 적들을 공중으로 날려버립니다.",
        "이때 날아간 플레이어는 일정 확률로 손에 쥐고 있는 아이템을 떨어뜨립니다."
    ]
)
class Temari(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    companion object {
        private const val WIND_RANGE = 10.0
    }

    override fun buildSkills() = listOf(object : ActiveContinueSkill() {
        override val cooldownTicks = 1200L
        override val durationTicks = 400L
        private var windTask: Any? = null
        private var indicatorTask: BukkitRunnable? = null

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                activate()
            }
        }

        override fun onActivate() {
            windTask = context.scheduleRepeat(30L) {
                AbilityUtils.splashTask(owner, owner.location, WIND_RANGE) { entity ->
                    val liftLoc = entity.location.clone().add(0.0, 4.0, 0.0)
                    AbilityUtils.goVelocity(entity, liftLoc, 1.0)
                    if (entity is Player && Math.random() <= 0.02) {
                        val item = entity.inventory.itemInMainHand
                        owner.world.dropItem(entity.location, item)
                        entity.inventory.removeItem(item)
                        entity.sendMessage("${ChatColor.RED}테마리의 강풍에 의해 손에 쥐고 있는 아이템을 떨어뜨렸습니다.")
                    }
                }
            }
            indicatorTask = AbilityUtils.createCircleIndicator(owner, WIND_RANGE, Particle.CLOUD)
        }

        override fun onDeactivate() {
            windTask?.let { context.cancelSchedule(it) }
            windTask = null
            indicatorTask?.cancel()
            indicatorTask = null
        }
    })
}
