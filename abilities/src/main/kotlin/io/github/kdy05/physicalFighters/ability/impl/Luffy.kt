package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.PFContext
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

@AbilityMeta(
    name = "루피",
    rank = Rank.S,
    guide = [
        "(철괴 좌클릭) 사거리가 긴 주먹질을 합니다.",
        "(금괴 좌클릭) 체력을 5 소모하여 30초간 여러 버프를 얻습니다."
    ]
)
class Luffy(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : PassiveSkill() {
        override fun register() {
            onLeftClick {
                when (owner.inventory.itemInMainHand.type) {
                    Material.IRON_INGOT -> doGumGumPunch()
                    Material.GOLD_INGOT -> doGearSecond()
                    else -> {}
                }
            }
        }

        private fun doGumGumPunch() {
            val origin = owner.location
            val direction = origin.direction
            for (i in 2..5) {
                val blockLoc = origin.clone().add(direction.clone().multiply(i))
                val block = blockLoc.block
                if (block.type != Material.SANDSTONE) {
                    val original = block.type
                    block.type = Material.SANDSTONE
                    Bukkit.getScheduler().runTaskLater(PFContext.plugin, Runnable {
                        block.type = original
                    }, 5L)
                }
                AbilityUtils.splashDamage(owner, blockLoc, 2.5, 2.0, true)
            }
        }

        private fun doGearSecond() {
            if (owner.health < 6.0) return
            AbilityUtils.piercingDamage(owner, 5.0)
            owner.addPotionEffect(PotionEffectFactory.createJumpBoost(600, 0))
            owner.addPotionEffect(PotionEffectFactory.createSpeed(600, 0))
            owner.addPotionEffect(PotionEffectFactory.createNausea(200, 0))
            owner.addPotionEffect(PotionEffectFactory.createStrength(600, 0))
            owner.addPotionEffect(PotionEffectFactory.createResistance(600, 0))
            owner.sendMessage("${ChatColor.RED}기어 세컨드를 사용하였습니다.")
        }
    })
}
