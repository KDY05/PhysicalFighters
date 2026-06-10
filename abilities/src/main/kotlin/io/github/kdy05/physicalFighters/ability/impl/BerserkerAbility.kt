package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.entity.Player

@AbilityMeta(
    name = "광전사",
    rank = Rank.A,
    guide = [
        "체력이 낮아질수록 대미지가 증폭됩니다.",
        "6칸 ↓ - 1.5배, 4칸 ↓ - 2배, 2칸 ↓ - 3배, 반 칸 ↓ - 4배"
    ]
)
class BerserkerAbility(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : PassiveSkill() {
        override fun register() {
            onEntityDamage { e ->
                when {
                    owner.health <= 1.0 -> {
                        e.damage *= 4.0
                        SoundUtils.playBreakSound(e.entity)
                    }
                    owner.health <= 4.0 -> {
                        e.damage *= 3.0
                        SoundUtils.playBreakSound(e.entity)
                    }
                    owner.health <= 8.0 -> {
                        e.damage *= 2.0
                        SoundUtils.playBreakSound(e.entity)
                    }
                    owner.health <= 12.0 -> {
                        e.damage *= 1.5
                    }
                }
            }
        }
    })
}
