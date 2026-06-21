package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.AbilityAPI
import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent

@AbilityMeta(
    name = "미러링",
    rank = Rank.SSS,
    guide = ["당신을 죽인 사람을 함께 저승으로 끌고갑니다."],
    minimumPlayers = 7
)
class Mirroring(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    override fun buildSkills() = listOf(object : PassiveSkill() {
        override fun register() {
            on(EntityDeathEvent::class) { e ->
                if (e.entity.uniqueId != owner.uniqueId) return@on
                val killed = e.entity as? Player ?: return@on
                val killer = killed.killer ?: return@on

                Bukkit.broadcastMessage("${ChatColor.RED}${killed.name}님의 미러링 능력이 발동되었습니다.")
                SoundUtils.broadcastWarningSound()

                val hasAegis = AbilityAPI.service.getAbilities(killer).any { it is Aegis }
                if (hasAegis) {
                    Bukkit.broadcastMessage("${ChatColor.GREEN}이지스 능력에 의해 미러링 능력이 무력화 되었습니다.")
                    return@on
                }

                killer.damage(5000.0)
                Bukkit.broadcastMessage(
                    "${ChatColor.RED}${killed.name}님의 미러링에 의해 ${killer.name}님이 죽었습니다.")
            }
        }
    })
}
