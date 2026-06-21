package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

@AbilityMeta(
    name = "헐크",
    rank = Rank.SSS,
    guide = ["(철괴 우클릭) 지속 시간동안 각종 버프를 받으며 주는 대미지가 1.5배, 받는 대미지가 절반이 됩니다."]
)
class Hulk(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : ActiveContinueSkill() {
        override val cooldownTicks = 3600L
        override val durationTicks = 600L
        private var hulking = false
        private var originalHealth = 20.0

        override fun register() {
            onRightClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onRightClick
                originalHealth = owner.health
                activate()
            }
            on(EntityDamageByEntityEvent::class) { e ->
                if (!hulking) return@on
                if (e.damager == owner) e.damage *= 1.5
                if (e.entity == owner) e.damage /= 2.0
            }
        }

        override fun onActivate() {
            hulking = true
            SoundUtils.broadcastWarningSound()
            owner.health = 20.0
            owner.world.createExplosion(owner.location, 0.0f)
            owner.addPotionEffect(PotionEffectFactory.createResistance(600, 0))
            owner.addPotionEffect(PotionEffectFactory.createStrength(600, 0))
            owner.addPotionEffect(PotionEffectFactory.createRegeneration(600, 0))
            owner.addPotionEffect(PotionEffectFactory.createSpeed(600, 0))
            owner.addPotionEffect(PotionEffectFactory.createNausea(600, 0))
            owner.addPotionEffect(PotionEffectFactory.createJumpBoost(600, 0))
        }

        override fun onDeactivate() {
            hulking = false
            owner.health = originalHealth
            owner.sendMessage("${ChatColor.RED}원래대로 돌아왔습니다.")
        }
    })
}
