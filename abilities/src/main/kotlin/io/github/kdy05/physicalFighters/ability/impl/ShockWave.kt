package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.cos
import kotlin.math.sin

@AbilityMeta(
    name = "쇼크웨이브",
    rank = Rank.A,
    guide = [
        "(철괴 좌클릭) 철괴를 소모하여 보고있는 방향으로 막강한 직선 충격포를 쏩니다.",
        "충격포는 물과 벽 건너편까지 통과할 수 있습니다."
    ]
)
class ShockWave(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : ActiveSkill() {
        override val cooldownTicks = 900L

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                if (!owner.inventory.contains(Material.IRON_INGOT, 1)) {
                    owner.sendMessage("${ChatColor.RED}철괴가 부족합니다.")
                    return@onLeftClick
                }
                activate()
            }
        }

        override fun onActivate() {
            owner.inventory.removeItem(ItemStack(Material.IRON_INGOT, 1))
            val l = owner.location
            val l2 = owner.location.clone()
            val degrees = Math.toRadians(-(l.yaw % 360.0f).toDouble())
            val ydeg = Math.toRadians(-(l.pitch % 360.0f).toDouble())
            for (i in 1..6) {
                l2.x = l.x + (3 * i + 3) * (sin(degrees) * cos(ydeg))
                l2.y = l.y + (3 * i + 3) * sin(ydeg)
                l2.z = l.z + (3 * i + 3) * (cos(degrees) * cos(ydeg))
                owner.world.createExplosion(l2, 5.0f)
            }
        }
    })
}
