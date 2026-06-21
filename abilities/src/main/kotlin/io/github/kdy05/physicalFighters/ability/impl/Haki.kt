package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

@AbilityMeta(
    name = "패기",
    rank = Rank.SS,
    guide = ["(철괴 좌클릭) 능력 지속 시간동안 10칸 내의 적에게 강한 대미지를 줍니다."]
)
class Haki(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    companion object {
        private const val RANGE = 10.0
        private const val DAMAGE = 8.0
    }

    override fun buildSkills() = listOf(object : ActiveContinueSkill() {
        override val cooldownTicks = 3200L
        override val durationTicks = 200L
        private var hakiTask: Any? = null
        private var indicatorTask: BukkitRunnable? = null

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                activate()
            }
        }

        override fun onActivate() {
            hakiTask = context.scheduleRepeat(40L) {
                AbilityUtils.splashTask(owner, owner.location, RANGE) { entity ->
                    entity.damage(DAMAGE, owner)
                    entity.addPotionEffect(PotionEffectFactory.createNausea(30, 0))
                    entity.sendMessage("${ChatColor.DARK_RED}패기에 압도당했습니다!")
                }
            }
            indicatorTask = AbilityUtils.createCircleIndicator(
                owner, RANGE, Particle.REDSTONE,
                Particle.DustOptions(Color.fromRGB(139, 0, 0), 1.5f)
            )
        }

        override fun onDeactivate() {
            hakiTask?.let { context.cancelSchedule(it) }
            hakiTask = null
            indicatorTask?.cancel()
            indicatorTask = null
        }
    })
}
