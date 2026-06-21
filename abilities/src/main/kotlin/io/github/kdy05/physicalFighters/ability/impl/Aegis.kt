package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

@AbilityMeta(
    name = "이지스",
    rank = Rank.A,
    guide = [
        "(철괴 좌클릭) 능력 지속 시간동안 무적이 됩니다.",
        "능력 사용 중엔 미러링 능력도 무시합니다."
    ]
)
class Aegis(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : ActiveContinueSkill() {
        override val cooldownTicks = 400L
        override val durationTicks = 120L
        private var shielding = false

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                activate()
            }
            on(EntityDamageEvent::class) { e ->
                if (e.entity.uniqueId != owner.uniqueId) return@on
                if (!shielding) return@on
                owner.fireTicks = 0
                SoundUtils.playShieldSound(owner)
                e.isCancelled = true
            }
        }

        override fun onActivate() { shielding = true }
        override fun onDeactivate() { shielding = false }
    })
}
