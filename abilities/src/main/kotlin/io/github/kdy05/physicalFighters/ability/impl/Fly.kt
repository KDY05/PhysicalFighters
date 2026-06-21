package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

@AbilityMeta(
    name = "플라이",
    rank = Rank.GOD,
    guide = [
        "(철괴 좌클릭) 10초간 하늘을 날라다닐 수 있습니다.",
        "(패시브) 낙하 대미지를 받지 않습니다."
    ]
)
class Fly(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    private val continueSkill = object : ActiveContinueSkill() {
        override val cooldownTicks = 1200L
        override val durationTicks = 200L

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                activate()
            }
        }

        override fun onActivate() {
            owner.allowFlight = true
            owner.isFlying = true
        }

        override fun onDeactivate() {
            owner.allowFlight = false
            owner.isFlying = false
        }
    }

    private val passiveSkill = object : PassiveSkill() {
        override fun register() {
            on(EntityDamageEvent::class) { e ->
                if (e.entity.uniqueId != owner.uniqueId) return@on
                if (e.cause != EntityDamageEvent.DamageCause.FALL) return@on
                SoundUtils.playSuccessSound(owner)
                owner.sendMessage("${ChatColor.GREEN}사뿐하게 떨어져 대미지를 받지 않았습니다.")
                e.isCancelled = true
            }
        }
    }

    override fun buildSkills() = listOf(continueSkill, passiveSkill)
}
