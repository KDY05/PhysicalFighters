package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import org.bukkit.Material
import org.bukkit.entity.Player

@AbilityMeta(
    name = "갓 에넬",
    rank = Rank.S,
    guide = ["(철괴 좌클릭) 바라보는 방향으로 번개를 발사하여 강한 범위 대미지를 줍니다."]
)
class Enel(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : ActiveSkill() {
        override val cooldownTicks = 600L

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                activate()
            }
        }

        override fun onActivate() {
            val direction = owner.location.direction
            for (i in 3..10) {
                val loc = owner.location.clone().add(direction.clone().multiply(i))
                loc.world?.strikeLightning(loc)
                AbilityUtils.splashDamage(owner, loc, 2.0, 15.0)
            }
        }
    })
}
