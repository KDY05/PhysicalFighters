package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

@AbilityMeta(
    name = "중력화살",
    rank = Rank.S,
    guide = ["화살에 맞은 플레이어는 공중으로 뜹니다. (추가타 가능)"]
)
class FallArrow(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : BaseItemSkill() {
        override val baseItems = arrayOf(
            ItemStack(Material.BOW, 1),
            ItemStack(Material.ARROW, 64)
        )
        override val itemDisplayName = "활과 화살"

        override fun register() {
            registerItemEvents()
            on(EntityDamageByEntityEvent::class) { e ->
                val arrow = e.damager as? Arrow ?: return@on
                val shooter = arrow.shooter as? Player ?: return@on
                if (shooter != owner) return@on
                val entity = e.entity as? LivingEntity ?: return@on
                if (entity == owner) return@on
                val liftLoc = entity.location.clone().add(0.0, 4.0, 0.0)
                AbilityUtils.goVelocity(entity, liftLoc, 1.0)
                entity.world.createExplosion(entity.location, 0.0f)
                entity.teleport(liftLoc)
            }
        }
    })
}
