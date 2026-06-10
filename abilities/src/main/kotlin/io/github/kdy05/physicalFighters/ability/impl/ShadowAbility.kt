package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.util.Vector

@AbilityMeta(
    name = "그림자",
    rank = Rank.A,
    guide = [
        "회피 - 피격 시 5% 확률로 회피하며, 체력 4를 회복합니다.",
        "기습 - 뒤에서 공격할 시 대미지를 2배로 입히고, 상대에게 일시적으로 실명을 부여합니다."
    ]
)
class ShadowAbility(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : PassiveSkill() {
        override fun register() {
            onEntityDamaged { e ->
                if (e.cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return@onEntityDamaged
                if (Math.random() >= 0.05) return@onEntityDamaged
                e.damage = 0.0
                AbilityUtils.healEntity(owner, 4.0)
                SoundUtils.playSuccessSound(owner)
                owner.sendMessage("${ChatColor.GREEN}회피하였습니다!")
            }
            onEntityDamage { e ->
                val target = e.entity as? LivingEntity ?: return@onEntityDamage
                if (!isBackstab(owner, target)) return@onEntityDamage
                e.damage = e.damage * 2.0
                target.addPotionEffect(PotionEffectFactory.createBlindness(60, 0))
                SoundUtils.playSuccessSound(owner)
                owner.sendMessage("${ChatColor.GREEN}기습 성공!")
            }
        }
    })

    private fun isBackstab(attacker: Player, target: LivingEntity): Boolean {
        val targetDir = target.location.direction.apply { y = 0.0 }.normalize()
        val attackVec = Vector(
            attacker.location.x - target.location.x,
            0.0,
            attacker.location.z - target.location.z
        ).normalize()
        return targetDir.dot(attackVec) <= -Math.cos(Math.toRadians(30.0))
    }
}
