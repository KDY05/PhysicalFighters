package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

@AbilityMeta(
    name = "바솔로뮤 쿠마",
    rank = Rank.S,
    guide = ["피격 시 상대를 넉백시키며, 일정 확률로 받은 공격을 상대에게 되돌려줍니다."]
)
class CumaAbility(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun skills() = listOf(object : PassiveSkill() {
        override fun register() {
            onEntityDamaged { e ->
                val attacker = e.damager as? LivingEntity ?: return@onEntityDamaged
                if (Math.random() <= 0.20) {
                    attacker.damage(e.damage)
                    e.isCancelled = true
                }
                attacker.world.createExplosion(attacker.location, 0.0f)
                AbilityUtils.goVelocity(attacker, owner.location, -1.0)
            }
        }
    })
}
