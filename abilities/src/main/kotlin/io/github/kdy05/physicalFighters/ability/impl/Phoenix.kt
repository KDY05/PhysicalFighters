package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.PFContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

@AbilityMeta(
    name = "불사조",
    rank = Rank.A,
    guide = [
        "자연사할 시 무제한으로 인벤토리를 잃지 않고 부활합니다.",
        "타인에게 사망할 경우 1회에 한하여 자연사 판정으로 부활합니다.",
        "부활시 자신의 능력이 모두에게 알려지게 됩니다."
    ],
    isDeathExempt = true
)
class Phoenix(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    private var reviveCounter = 0
    private var abilityUse = false
    private val invsave = mutableMapOf<UUID, Array<ItemStack?>>()

    override fun buildSkills() = listOf(object : PassiveSkill() {
        override fun register() {
            on(PlayerDeathEvent::class) { e ->
                val killed = e.entity as? Player ?: return@on
                if (killed.uniqueId != owner.uniqueId) return@on

                if (abilityUse) {
                    Bukkit.broadcastMessage("${ChatColor.RED}불사조가 죽었습니다. 더 이상 부활할수 없습니다.")
                } else {
                    invsave[killed.uniqueId] = killed.inventory.contents
                    e.drops.clear()
                    Bukkit.broadcastMessage("${ChatColor.RED}불사조가 죽었습니다. 다시 부활할 수 있습니다.")
                    if (killed.killer != null) {
                        abilityUse = true
                    }
                }
                reviveCounter++
            }
            on(PlayerRespawnEvent::class) { e ->
                if (e.player.uniqueId != owner.uniqueId) return@on
                val player = e.player
                val uuid = player.uniqueId
                val inv = invsave[uuid] ?: return@on

                if (!abilityUse) {
                    Bukkit.broadcastMessage(
                        "${ChatColor.GREEN}불사조가 부활하였습니다. 부활 횟수 : ${reviveCounter}회")
                }

                Bukkit.getScheduler().runTaskLater(PFContext.plugin, Runnable {
                    if (player.isOnline) {
                        player.inventory.setContents(inv)
                    }
                    invsave.remove(uuid)
                }, 1L)

                player.addPotionEffect(PotionEffectFactory.createHaste(600, 0))
                player.addPotionEffect(PotionEffectFactory.createFireResistance(600, 0))
                player.addPotionEffect(PotionEffectFactory.createJumpBoost(600, 0))
                player.addPotionEffect(PotionEffectFactory.createSpeed(600, 0))
                player.addPotionEffect(PotionEffectFactory.createWaterBreathing(600, 0))
                player.addPotionEffect(PotionEffectFactory.createRegeneration(600, 0))
                player.addPotionEffect(PotionEffectFactory.createResistance(600, 0))
            }
        }
    })
}
