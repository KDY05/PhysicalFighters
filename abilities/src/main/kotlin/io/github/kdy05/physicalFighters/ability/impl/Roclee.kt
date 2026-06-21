package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

@AbilityMeta(
    name = "록리",
    rank = Rank.S,
    guide = [
        "(철괴 공격) 피해를 입히며 공중으로 끌어올립니다.",
        "이때 시전자는 5초간 낙하 대미지를 받지 않습니다."
    ]
)
class Roclee(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    private var fallDamageImmune = false

    override fun buildSkills(): List<io.github.kdy05.abilityAPI.skill.Skill> {
        val activeSkill = object : ActiveSkill() {
            override val cooldownTicks = 400L
            private var pendingTarget: LivingEntity? = null

            override fun register() {
                onEntityDamage { e ->
                    if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onEntityDamage
                    pendingTarget = e.entity as? LivingEntity ?: return@onEntityDamage
                    activate()
                }
            }

            override fun onActivate() {
                val target = pendingTarget ?: return
                pendingTarget = null

                target.world.createExplosion(target.location, 0.0f)
                val targetNewLoc = findSafeTeleportLocation(target.location)
                val ownerNewLoc = findSafeTeleportLocation(owner.location)
                target.teleport(targetNewLoc)
                owner.teleport(ownerNewLoc)
                fallDamageImmune = true
                context.scheduleOnce(100L) { fallDamageImmune = false }
                targetNewLoc.world?.createExplosion(targetNewLoc, 1.0f)
                target.damage(10.0)
            }
        }

        val passiveSkill = object : PassiveSkill() {
            override fun register() {
                on(EntityDamageEvent::class) { e ->
                    if (e.entity.uniqueId != owner.uniqueId) return@on
                    if (!fallDamageImmune) return@on
                    if (e.cause != EntityDamageEvent.DamageCause.FALL) return@on
                    e.isCancelled = true
                }
            }
        }

        return listOf(activeSkill, passiveSkill)
    }

    private fun findSafeTeleportLocation(originalLoc: Location): Location {
        var maxHeight = 8
        for (y in 1..8) {
            val checkLoc = originalLoc.clone().add(0.0, y.toDouble(), 0.0)
            if (checkLoc.block.type.isSolid) {
                maxHeight = y - 1
                break
            }
        }
        return originalLoc.clone().add(0.0, maxHeight.toDouble(), 0.0)
    }
}
