package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

@AbilityMeta(
    name = "토르",
    rank = Rank.S,
    guide = [
        "(철괴 좌클릭) 바라보는 지점에 번개를 떨어뜨립니다.",
        "번개가 떨어진 지점에 강한 폭발이 일어납니다."
    ]
)
class Thor(owner: Player, context: SkillContext) : PFAbility(owner, context) {
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
            val loc = targetLocation ?: return
            targetLocation = null
            val world = loc.world ?: return
            world.createExplosion(loc, 4.0f, true)
            world.strikeLightning(loc)
            world.strikeLightning(loc)
        }
    })
}
