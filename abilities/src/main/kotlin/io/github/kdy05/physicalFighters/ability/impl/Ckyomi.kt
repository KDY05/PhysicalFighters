package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

@AbilityMeta(
    name = "츠쿠요미",
    rank = Rank.A,
    guide = ["상대를 공격하면 상대에게 5초간 혼란 효과와 디버프를 줍니다."]
)
class Ckyomi(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : PassiveSkill() {
        override fun register() {
            onEntityDamage { e ->
                val entity = e.entity as? LivingEntity ?: return@onEntityDamage
                entity.addPotionEffect(PotionEffectFactory.createNausea(100, 0))
                entity.addPotionEffect(PotionEffectFactory.createWeakness(100, 0))
                entity.addPotionEffect(PotionEffectFactory.createBlindness(100, 0))
            }
        }
    })
}
