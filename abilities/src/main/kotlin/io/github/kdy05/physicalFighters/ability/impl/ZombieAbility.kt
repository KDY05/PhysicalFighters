package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

@AbilityMeta(
    name = "좀비",
    rank = Rank.B,
    guide = ["모든 대미지의 반을 흡수합니다. 단, 화염 대미지를 8배로 받습니다."]
)
class ZombieAbility(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : PassiveSkill() {
        override fun register() {
            on(EntityDamageEvent::class) { e ->
                if (e.entity.uniqueId != owner.uniqueId) return@on
                when (e.cause) {
                    EntityDamageEvent.DamageCause.LAVA,
                    EntityDamageEvent.DamageCause.FIRE,
                    EntityDamageEvent.DamageCause.FIRE_TICK -> e.damage *= 8.0
                    else -> {
                        SoundUtils.playShieldSound(owner)
                        e.damage *= 0.5
                    }
                }
            }
        }
    })
}
