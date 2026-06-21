package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

@AbilityMeta(
    name = "포이즌",
    rank = Rank.A,
    guide = ["자신에게 공격받은 사람은 3초간 독에 감염됩니다."]
)
class Poison(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : PassiveSkill() {
        override fun register() {
            onEntityDamage { e ->
                val entity = e.entity as? LivingEntity ?: return@onEntityDamage
                entity.addPotionEffect(PotionEffectFactory.createPoison(60, 0))
            }
        }
    })
}
