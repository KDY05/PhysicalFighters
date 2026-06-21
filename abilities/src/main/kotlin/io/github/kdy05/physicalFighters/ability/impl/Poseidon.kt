package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.abs

@AbilityMeta(
    name = "포세이돈",
    rank = Rank.SS,
    guide = [
        "바라보는 곳에 거대한 어항을 만들어 가둡니다.",
        "물 속에서 자신에게는 버프, 상대에게는 디버프를 겁니다."
    ]
)
class Poseidon(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    companion object {
        private const val GLASS_RANGE = 4
        private const val WATER_RANGE = 2
        private const val SLOW_RANGE = 10.0
        private const val TELEPORT_HEIGHT = 3
    }

    override fun buildSkills() = listOf(object : ActiveSkill() {
        override val cooldownTicks = 1200L
        private var targetLocation: Location? = null

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                targetLocation = AbilityUtils.getTargetLocation(owner, 40)
                if (targetLocation == null) {
                    owner.sendMessage("${ChatColor.RED}거리가 너무 멉니다.")
                    return@onLeftClick
                }
                activate()
            }
            on(PlayerMoveEvent::class) { e ->
                if (e.player != owner) return@on
                if (owner.location.block.type != Material.WATER) return@on
                owner.addPotionEffect(PotionEffectFactory.createWaterBreathing(60, 0))
                owner.addPotionEffect(PotionEffectFactory.createSpeed(60, 0))
                owner.addPotionEffect(PotionEffectFactory.createResistance(60, 0))
                AbilityUtils.splashTask(
                    owner, owner.location, SLOW_RANGE,
                    { it.location.block.type == Material.WATER },
                    { it.addPotionEffect(PotionEffectFactory.createSlowness(60, 0)) }
                )
            }
        }

        override fun onActivate() {
            val center = targetLocation ?: return
            targetLocation = null
            teleportPlayersInRange(center)
            AbilityUtils.createBox(center, Material.GLASS, GLASS_RANGE, 2 * GLASS_RANGE + 1)
            AbilityUtils.createBox(
                center.clone().add(0.0, (GLASS_RANGE - WATER_RANGE).toDouble(), 0.0),
                Material.WATER, WATER_RANGE, 2 * WATER_RANGE + 1
            )
        }

        private fun teleportPlayersInRange(center: Location) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player == owner) continue
                val loc = player.location
                val dx = abs(loc.x - center.x)
                val dy = loc.y - center.y
                val dz = abs(loc.z - center.z)
                if (dx <= GLASS_RANGE && dy >= 0 && dy <= 2 * GLASS_RANGE + 1 && dz <= GLASS_RANGE) {
                    val teleportLoc = center.clone().add(0.0, TELEPORT_HEIGHT.toDouble(), 0.0)
                    teleportLoc.yaw = loc.yaw
                    teleportLoc.pitch = loc.pitch
                    player.teleport(teleportLoc)
                }
            }
        }
    })
}
