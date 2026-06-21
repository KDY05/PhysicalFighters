package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.PFContext
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

@AbilityMeta(
    name = "아카이누",
    rank = Rank.SS,
    guide = [
        "(철괴 좌클릭) 바라보는 곳의 땅을 용암으로 바꿉니다.",
        "4초 뒤에 용암이 다시 굳으며 적을 땅속에 가둡니다.",
        "(패시브) 화염 및 용암 대미지를 무시합니다."
    ]
)
class Akainu(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    companion object {
        private const val OUTER_RADIUS = 3
        private const val INNER_RADIUS = 2
        private const val DEPTH = 4
        private const val LAVA_DEPTH = 3
        private const val RESTORE_DELAY = 80L
    }

    private val activeSkill = object : ActiveSkill() {
        override val cooldownTicks = 1200L
        private var targetLocation: Location? = null

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                targetLocation = AbilityUtils.getTargetLocation(owner, 40)
                if (targetLocation == null) {
                    SoundUtils.playErrorSound(owner)
                    owner.sendMessage("${ChatColor.RED}거리가 너무 멉니다.")
                    return@onLeftClick
                }
                activate()
            }
        }

        override fun onActivate() {
            val center = targetLocation ?: run {
                owner.sendMessage("${ChatColor.RED}능력을 사용할 수 없습니다.")
                return
            }
            targetLocation = null
            val originalBlocks = mutableMapOf<Location, Material>()

            for (y in -DEPTH..0) {
                for (x in -OUTER_RADIUS..OUTER_RADIUS) {
                    for (z in -OUTER_RADIUS..OUTER_RADIUS) {
                        val loc = center.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                        originalBlocks[loc.clone()] = loc.block.type
                    }
                }
            }
            AbilityUtils.createBox(center.clone().add(0.0, -DEPTH.toDouble(), 0.0), Material.AIR, OUTER_RADIUS, DEPTH + 1)
            AbilityUtils.splashTask(owner, center, OUTER_RADIUS.toDouble()) { entity ->
                entity.velocity = entity.velocity.add(org.bukkit.util.Vector(0.0, -1.0, 0.0))
            }
            AbilityUtils.createBox(center.clone().add(0.0, -LAVA_DEPTH.toDouble(), 0.0), Material.LAVA, INNER_RADIUS, LAVA_DEPTH)

            Bukkit.getScheduler().runTaskLater(PFContext.plugin, Runnable {
                for ((loc, mat) in originalBlocks) {
                    loc.world?.getBlockAt(loc)?.type = mat
                }
            }, RESTORE_DELAY)
        }
    }

    private val passiveSkill = object : PassiveSkill() {
        override fun register() {
            on(EntityDamageEvent::class) { e ->
                if (e.entity.uniqueId != owner.uniqueId) return@on
                when (e.cause) {
                    EntityDamageEvent.DamageCause.LAVA,
                    EntityDamageEvent.DamageCause.FIRE,
                    EntityDamageEvent.DamageCause.FIRE_TICK -> e.isCancelled = true
                    else -> {}
                }
            }
        }
    }

    override fun buildSkills() = listOf(activeSkill, passiveSkill)
}
