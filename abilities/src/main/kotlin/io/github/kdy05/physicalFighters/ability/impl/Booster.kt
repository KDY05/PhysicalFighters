package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.abilityAPI.AbilityAPI
import io.github.kdy05.physicalFighters.ability.PFAbility
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

@AbilityMeta(
    name = "부스터",
    rank = Rank.SS,
    guide = [
        "매우 낮은 딜레이로 상대를 공격합니다. 단 대미지는 2-5으로 랜덤입니다.",
        "피격 시 60% 확률로 넉백을 무시합니다."
    ]
)
class Booster(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : PassiveSkill() {
        override fun register() {
            onEntityDamage { e ->
                val entity = e.entity as? LivingEntity ?: return@onEntityDamage
                e.damage = 2.0 + Math.random() * 3.0
                Bukkit.getScheduler().runTaskLater(AbilityAPI.plugin, Runnable {
                    entity.noDamageTicks = 6
                }, 1L)
            }
            onEntityDamaged { e ->
                if (e.cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
                    e.cause != EntityDamageEvent.DamageCause.PROJECTILE) return@onEntityDamaged
                if (Math.random() > 0.60) return@onEntityDamaged
                val velocity = owner.velocity.clone()
                Bukkit.getScheduler().runTaskLater(AbilityAPI.plugin, Runnable {
                    owner.velocity = velocity
                }, 1L)
            }
        }
    })
}
