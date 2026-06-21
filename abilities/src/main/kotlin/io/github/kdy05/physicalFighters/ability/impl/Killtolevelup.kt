package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

@AbilityMeta(
    name = "폭주",
    rank = Rank.SS,
    guide = [
        "깃털의 처음 대미지는 5입니다.",
        "깃털로 적을 처치할 때마다 대미지가 2만큼 늘어납니다."
    ]
)
class Killtolevelup(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : BaseItemSkill() {
        override val baseItems = arrayOf(ItemStack(Material.FEATHER, 1))
        override val itemDisplayName = "깃털"
        private var damage = 5

        override fun register() {
            registerItemEvents()
            onEntityDamage { e ->
                if (owner.inventory.itemInMainHand.type != Material.FEATHER) return@onEntityDamage
                e.damage *= damage.toDouble()
            }
            on(EntityDeathEvent::class) { e ->
                val killed = e.entity as? Player ?: return@on
                val killer = killed.killer ?: return@on
                if (killer != owner) return@on
                if (owner.inventory.itemInMainHand.type != Material.FEATHER) return@on
                damage += 2
                SoundUtils.broadcastWarningSound()
                Bukkit.broadcastMessage("${ChatColor.RED}${killed.name}님을 죽이고 ${killer.name}님이 폭주했습니다.")
            }
        }
    })
}
