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
import org.bukkit.Color
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.util.RayTraceResult

@AbilityMeta(
    name = "키자루",
    rank = Rank.SS,
    guide = [
        "[철괴 공격] 타격한 상대를 빛의 속도로 타격합니다.",
        "상대는 엄청난 속도로 멀리 날라갑니다. 당신도 상대를 따라 근접하게 날라갑니다.",
        "[철괴 우클릭] 바라보는 곳으로 순간이동합니다.",
        "[패시브] 낙하 대미지를 받지 않습니다."
    ]
)
class KijaruAbility(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    private val activeSkill = object : ActiveSkill() {
        override val cooldownTicks = 900L
        private var pendingAction: (() -> Unit)? = null

        override fun register() {
            onEntityDamage { e ->
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onEntityDamage
                val entity = e.entity as? LivingEntity ?: return@onEntityDamage
                pendingAction = {
                    e.isCancelled = true
                    spawnParticlePath(owner.location, entity.location)
                    AbilityUtils.goVelocity(entity, owner.location.clone().add(0.0, -0.25, 0.0), -4.0)
                    Bukkit.getScheduler().runTaskLater(PFContext.plugin, Runnable {
                        val loc = entity.location.clone()
                        loc.y += 2.0
                        owner.teleport(loc)
                        entity.damage(8.0, owner)
                        owner.world.createExplosion(owner.location, 0.0f)
                    }, 20L)
                }
                activate()
            }
            onRightClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onRightClick
                pendingAction = {
                    val origin = owner.eyeLocation
                    val direction = origin.direction.normalize()
                    val result: RayTraceResult? = owner.world.rayTraceBlocks(
                        origin, direction, 80.0, FluidCollisionMode.NEVER, true
                    )
                    val dest = result?.hitPosition?.subtract(direction)?.toLocation(owner.world)
                        ?: origin.clone().add(direction.clone().multiply(80.0))
                    spawnParticlePath(origin, dest)
                    dest.pitch = owner.location.pitch
                    dest.yaw = owner.location.yaw
                    owner.teleport(dest)
                    owner.world.playSound(owner.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
                }
                activate()
            }
        }

        override fun onActivate() {
            pendingAction?.invoke()
            pendingAction = null
        }

        private fun spawnParticlePath(from: org.bukkit.Location, to: org.bukkit.Location) {
            val direction = to.toVector().subtract(from.toVector()).normalize()
            val distance = from.distance(to)
            val dust = Particle.DustOptions(Color.YELLOW, 1.0f)
            var d = 0.0
            while (d <= distance) {
                from.world?.spawnParticle(Particle.REDSTONE, from.clone().add(direction.clone().multiply(d)), 1, 0.0, 0.0, 0.0, 0.0, dust)
                d += 0.5
            }
        }

    }

    private val passiveSkill = object : PassiveSkill() {
        override fun register() {
            on(EntityDamageEvent::class) { e ->
                if (e.entity.uniqueId != owner.uniqueId) return@on
                if (e.cause != EntityDamageEvent.DamageCause.FALL) return@on
                SoundUtils.playSuccessSound(owner)
                owner.sendMessage("${ChatColor.GREEN}사뿐하게 떨어져 대미지를 받지 않았습니다.")
                e.isCancelled = true
            }
        }
    }

    override fun buildSkills() = listOf(activeSkill, passiveSkill)
}
