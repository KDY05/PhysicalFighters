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
    name = "아폴론",
    rank = Rank.SS,
    guide = ["(철괴 좌클릭) 바라보는 방향에 불구덩이를 만듭니다."]
)
class Apollon(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : ActiveSkill() {
        override val cooldownTicks = 800L
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
            AbilityUtils.createBox(center.clone().add(0.0, -8.0, 0.0), Material.NETHERRACK, 4, 8)
            AbilityUtils.createBox(center.clone().add(0.0, -7.0, 0.0), Material.AIR, 3, 7)
            AbilityUtils.createBox(center.clone().add(0.0, -7.0, 0.0), Material.FIRE, 3, 1)
        }
    })
}
