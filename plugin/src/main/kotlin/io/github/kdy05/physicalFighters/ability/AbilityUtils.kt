package io.github.kdy05.physicalFighters.ability

import io.github.kdy05.physicalFighters.PhysicalFighters
import io.github.kdy05.physicalFighters.util.AttributeUtils
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.math.cos
import kotlin.math.sin

object AbilityUtils {

    @JvmStatic
    fun findAbility(p: Player): Ability? = AbilityRegistry.findAbility(p)

    @JvmStatic
    fun getTargetLocation(p: Player, bound: Int): Location? {
        val result = p.world.rayTraceBlocks(
            p.eyeLocation, p.eyeLocation.direction,
            bound.toDouble(), FluidCollisionMode.ALWAYS
        )
        return result?.hitBlock?.location
    }

    @JvmStatic
    fun goVelocity(entity: LivingEntity, target: Location, value: Double) {
        entity.velocity = entity.velocity.add(
            target.toVector()
                .subtract(entity.location.toVector()).normalize()
                .multiply(value)
        )
    }

    @JvmStatic
    fun piercingDamage(entity: LivingEntity, damage: Double) {
        entity.health = maxOf(0.0, entity.health - damage)
    }

    @JvmStatic
    fun healEntity(entity: LivingEntity, amount: Double) {
        val maxHealthValue = AttributeUtils.getMaxHealth(entity)
        entity.health = minOf(maxHealthValue, entity.health + amount)
    }

    @JvmStatic
    @JvmOverloads
    fun createBox(center: Location, material: Material, radius: Int, height: Int, ignoreBedrock: Boolean = false) {
        val world = center.world ?: return
        for (y in 1..height) {
            for (x in -radius..radius) {
                for (z in -radius..radius) {
                    val block = world.getBlockAt(center.clone()
                        .add(x.toDouble(), y.toDouble(), z.toDouble()))
                    if (ignoreBedrock || block.type != Material.BEDROCK) {
                        block.type = material
                    }
                }
            }
        }
    }

    @JvmStatic
    fun splashDamage(caster: Player, location: Location, bound: Double, damage: Double) {
        caster.world.getNearbyEntities(location, bound, bound, bound)
            .filterIsInstance<LivingEntity>()
            .filter { it !== caster && isDifferentTeam(caster, it) }
            .forEach { it.damage(damage, caster) }
    }

    @JvmStatic
    @JvmOverloads
    fun splashTask(
        caster: Player, location: Location, bound: Double,
        filter: Predicate<LivingEntity> = Predicate { true },
        action: Consumer<LivingEntity>
    ) {
        caster.world.getNearbyEntities(location, bound, bound, bound)
            .filterIsInstance<LivingEntity>()
            .filter { it !== caster && isDifferentTeam(caster, it) }
            .filter { filter.test(it) }
            .forEach { action.accept(it) }
    }

    private const val POINTS_PER_BLOCK = 4.0
    private const val INTERVAL_TICKS = 5L

    @JvmStatic
    fun createCircleIndicator(
        player: Player,
        radius: Double,
        particle: Particle,
    ): BukkitRunnable {
        val points = maxOf(8, (2 * Math.PI * radius * POINTS_PER_BLOCK).toInt())
        val task = object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    cancel()
                    return
                }
                val center = player.location
                val world = center.world ?: return
                for (i in 0 until points) {
                    val angle = 2 * Math.PI * i / points
                    val x = center.x + radius * cos(angle)
                    val z = center.z + radius * sin(angle)
                    world.spawnParticle(particle, x, center.y + 0.5, z, 1, 0.0, 0.0, 0.0, 0.0)
                }
            }
        }
        task.runTaskTimer(PhysicalFighters.plugin, 0L, INTERVAL_TICKS)
        return task
    }

    private fun isDifferentTeam(caster: Player, target: LivingEntity): Boolean {
        if (target !is Player) return true
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard ?: return true
        val casterTeam = scoreboard.getEntryTeam(caster.name)
        val targetTeam = scoreboard.getEntryTeam(target.name)
        if (casterTeam == null || targetTeam == null) return true
        return casterTeam != targetTeam
    }
}