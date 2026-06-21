package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

@AbilityMeta(
    name = "데미갓",
    rank = Rank.S,
    guide = [
        "반은 인간, 반은 신인 능력자입니다.",
        "대미지를 받으면 일정 확률로 10초간 랜덤 버프가 발동됩니다."
    ]
)
class Demigod(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : PassiveSkill() {
        override fun register() {
            on(EntityDamageEvent::class) { e ->
                if (e.entity.uniqueId != owner.uniqueId) return@on
                if (Math.random() <= 0.05) AbilityUtils.healEntity(owner, 2.0)
                if (Math.random() <= 0.05) owner.addPotionEffect(PotionEffectFactory.createResistance(200, 0))
                if (Math.random() <= 0.05) owner.addPotionEffect(PotionEffectFactory.createRegeneration(200, 0))
                if (Math.random() <= 0.1) owner.addPotionEffect(PotionEffectFactory.createJumpBoost(200, 0))
                if (Math.random() <= 0.1) owner.addPotionEffect(PotionEffectFactory.createHaste(200, 0))
                if (Math.random() <= 0.1) owner.addPotionEffect(PotionEffectFactory.createSpeed(200, 0))
            }
        }
    })
}
