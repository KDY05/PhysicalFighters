package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

@AbilityMeta(
    name = "CP9",
    rank = Rank.S,
    guide = [
        "(철괴 공격) 상대에게 고정 대미지를 줍니다.",
        "(철괴 우클릭) 바라보는 방향으로 빠르게 도약합니다.",
        "(패시브) 낙하 대미지를 무시합니다."
    ]
)
class CP9(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    private val activeSkill = object : ActiveSkill() {
        override val cooldownTicks = 400L
        private var pendingAction: (() -> Unit)? = null

        override fun register() {
            onEntityDamage { e ->
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onEntityDamage
                val entity = e.entity as? LivingEntity ?: return@onEntityDamage
                pendingAction = {
                    AbilityUtils.piercingDamage(entity, 6.0)
                    SoundUtils.playBreakSound(entity)
                    owner.sendMessage("${ChatColor.RED}${entity.name}에게 지건을 사용했습니다.")
                }
                activate()
            }
            onRightClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onRightClick
                pendingAction = {
                    val direction = owner.location.direction.normalize()
                    owner.world.createExplosion(owner.location.clone().subtract(direction.clone().multiply(1.5)), 0.0f)
                    AbilityUtils.goVelocity(owner, owner.location.clone().add(direction.clone().multiply(5.0)), 5.0)
                }
                activate()
            }
        }

        override fun onActivate() {
            pendingAction?.invoke()
            pendingAction = null
        }
    }

    private val passiveSkill = object : PassiveSkill() {
        override fun register() {
            on(EntityDamageEvent::class) { e ->
                if (e.entity.uniqueId != owner.uniqueId) return@on
                if (e.cause != EntityDamageEvent.DamageCause.FALL) return@on
                e.isCancelled = true
                SoundUtils.playSuccessSound(owner)
                owner.sendMessage("${ChatColor.GREEN}사뿐하게 떨어져 대미지를 받지 않았습니다.")
            }
        }
    }

    override fun buildSkills() = listOf(activeSkill, passiveSkill)
}
