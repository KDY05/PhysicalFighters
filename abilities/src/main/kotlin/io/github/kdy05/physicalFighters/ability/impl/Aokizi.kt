package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.abilityAPI.AbilityAPI
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.sin

@AbilityMeta(
    name = "아오키지",
    rank = Rank.S,
    guide = [
        "(철괴 좌클릭) 자신이 보고있는 방향으로 얼음을 날립니다.",
        "(패시브) 자신이 공격한 적을 30% 확률로 3초간 느리게 만듭니다."
    ]
)
class Aokizi(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    private val activeSkill = object : ActiveSkill() {
        override val cooldownTicks = 20L

        override fun onCooldownRunning(remainingSeconds: Int) {}
        override fun onPostActivate() {}
        override fun onCooldownEnd() {}

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                activate()
            }
        }

        override fun onActivate() {
            val l = owner.location
            val l2 = owner.location.clone()
            val degrees = Math.toRadians(-(l.yaw % 360.0f).toDouble())
            val ydeg = Math.toRadians(-(l.pitch % 360.0f).toDouble())
            for (i in 1..9) {
                l2.x = l.x + (i + 1) * (sin(degrees) * cos(ydeg))
                l2.y = l.y + (i + 1) * sin(ydeg)
                l2.z = l.z + (i + 1) * (cos(degrees) * cos(ydeg))
                val block = owner.world.getBlockAt(l2)
                if (block.type != Material.ICE) {
                    val original = block.type
                    val loc = block.location.clone()
                    Bukkit.getScheduler().runTaskLater(AbilityAPI.plugin, Runnable {
                        loc.world?.getBlockAt(loc)?.type = original
                    }, 15L)
                }
                block.type = Material.ICE
                AbilityUtils.splashDamage(owner, block.location, 2.5, 8.0, true)
            }
        }
    }

    private val passiveSkill = object : PassiveSkill() {
        override fun register() {
            onEntityDamage { e ->
                if (Math.random() >= 0.30) return@onEntityDamage
                val entity = e.entity as? LivingEntity ?: return@onEntityDamage
                entity.addPotionEffect(PotionEffectFactory.createSlowness(60, 0))
            }
        }
    }

    override fun buildSkills() = listOf(activeSkill, passiveSkill)
}
