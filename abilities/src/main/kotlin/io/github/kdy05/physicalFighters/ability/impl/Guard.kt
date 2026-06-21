package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.math.abs

@AbilityMeta(
    name = "목둔",
    rank = Rank.A,
    guide = [
        "(철괴 좌클릭) 바라보는 위치에 나무벽을 설치합니다.",
        "주위에 플레이어가 있으면 가두고, 채굴 피로를 부여합니다."
    ]
)
class Guard(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    companion object {
        private const val BOX_RADIUS = 5
        private const val BOX_HEIGHT = 9
        private const val INNER_RADIUS = 3
        private const val INNER_HEIGHT = 6
        private const val TELEPORT_HEIGHT = 2
    }

    override fun buildSkills() = listOf(object : ActiveSkill() {
        override val cooldownTicks = 600L
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
            val center = targetLocation ?: return
            targetLocation = null
            teleportPlayersInRange(center)
            AbilityUtils.createBox(center, Material.OAK_PLANKS, BOX_RADIUS, BOX_HEIGHT)
            AbilityUtils.createBox(center.clone().add(0.0, 1.0, 0.0), Material.AIR, INNER_RADIUS, INNER_HEIGHT)
        }

        private fun teleportPlayersInRange(center: Location) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player == owner) continue
                val loc = player.location
                val dx = abs(loc.x - center.x)
                val dy = loc.y - center.y
                val dz = abs(loc.z - center.z)
                if (dx <= BOX_RADIUS && dy >= 0 && dy <= BOX_HEIGHT && dz <= BOX_RADIUS) {
                    val teleportLoc = center.clone().add(0.0, TELEPORT_HEIGHT.toDouble(), 0.0)
                    teleportLoc.yaw = loc.yaw
                    teleportLoc.pitch = loc.pitch
                    player.teleport(teleportLoc)
                    player.addPotionEffect(PotionEffectFactory.createMiningFatigue(100, 1))
                }
            }
        }
    })
}
