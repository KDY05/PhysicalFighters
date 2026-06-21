package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

@AbilityMeta(
    name = "선풍기",
    rank = Rank.C,
    guide = [
        "(철괴 좌클릭) 바라보는 방향의 플레이어들을 날려버립니다.",
        "플레이어들은 무더위에 시원함을 느껴 체력이 회복됩니다.",
        "하지만 강한 바람에 의해 눈을 뜨기가 힘들고 허약해집니다."
    ]
)
class SuperFan(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : ActiveSkill() {
        override val cooldownTicks = 400L

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                activate()
            }
        }

        override fun onActivate() {
            val origin = owner.location
            val direction = origin.direction
            val knockbackOrigin = origin.clone().subtract(0.0, 1.0, 0.0)

            val affected = mutableSetOf<LivingEntity>()
            for (step in 1..9) {
                val windLoc = origin.clone().add(direction.clone().multiply(step + 2))
                owner.world.getNearbyEntities(windLoc, 3.0, 3.0, 3.0)
                    .filterIsInstance<LivingEntity>()
                    .filter { it != owner }
                    .forEach { affected.add(it) }
            }

            for (target in affected) {
                AbilityUtils.goVelocity(target, knockbackOrigin, -2.5)
                target.addPotionEffect(PotionEffectFactory.createRegeneration(100, 0))
                target.addPotionEffect(PotionEffectFactory.createBlindness(200, 2))
                target.addPotionEffect(PotionEffectFactory.createNausea(200, 2))
                target.addPotionEffect(PotionEffectFactory.createWeakness(200, 2))
                target.sendMessage("${ChatColor.LIGHT_PURPLE}앗! 바람이 강하지만 시원해~♥")
            }
        }
    })
}
