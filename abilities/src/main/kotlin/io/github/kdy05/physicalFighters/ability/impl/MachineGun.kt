package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

@AbilityMeta(
    name = "기관총",
    rank = Rank.S,
    guide = [
        "(금괴 우클릭) 화살을 연사합니다. 철괴를 탄창으로 사용하며 한 탄창은 30발입니다.",
        "크리티컬 - 20% 확률로 화살이 고정 대미지를 입힙니다."
    ]
)
class MachineGun(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    companion object {
        private const val MAGAZINE_SIZE = 30
        private const val RELOAD_TIME_TICKS = 60L
        private const val CRITICAL_CHANCE = 0.2
        private const val CRITICAL_DAMAGE = 2.0
        private const val BULLET_DAMAGE = 2.0
    }

    override fun buildSkills() = listOf(object : PassiveSkill() {
        private var currentBullets = 0
        private var isReloading = false

        override fun register() {
            onRightClick {
                if (owner.inventory.itemInMainHand.type != Material.GOLD_INGOT) return@onRightClick
                when {
                    currentBullets > 0 -> shoot()
                    isReloading -> {
                        SoundUtils.playErrorSound(owner)
                        owner.sendMessage("${ChatColor.RED}장전중입니다.")
                    }
                    owner.inventory.contains(Material.IRON_INGOT) -> reload()
                    else -> {
                        SoundUtils.playErrorSound(owner)
                        owner.sendMessage("${ChatColor.RED}탄창이 없습니다.")
                    }
                }
            }
            on(EntityDamageByEntityEvent::class) { e ->
                val arrow = e.damager as? Arrow ?: return@on
                val shooter = arrow.shooter as? Player ?: return@on
                if (shooter != owner) return@on
                if (e.entity == owner) return@on
                e.damage = BULLET_DAMAGE
                val target = e.entity as? LivingEntity ?: return@on
                if (Math.random() <= CRITICAL_CHANCE) {
                    target.world.createExplosion(target.location, 0.0f)
                    AbilityUtils.piercingDamage(target, CRITICAL_DAMAGE)
                    SoundUtils.playSuccessSound(owner)
                }
            }
            on(ProjectileHitEvent::class) { e ->
                val arrow = e.entity as? Arrow ?: return@on
                val shooter = arrow.shooter as? Player ?: return@on
                if (shooter == owner) arrow.remove()
            }
        }

        private fun shoot() {
            if (currentBullets % 5 == 0) {
                owner.sendMessage("${ChatColor.AQUA}남은 탄환 : ${ChatColor.WHITE}${currentBullets}개")
            }
            currentBullets--
            owner.launchProjectile(Arrow::class.java)
        }

        private fun reload() {
            if (isReloading) return
            isReloading = true
            owner.sendMessage("${ChatColor.AQUA}장전 중... (3초 소요)")
            context.scheduleOnce(RELOAD_TIME_TICKS) {
                if (owner.inventory.contains(Material.IRON_INGOT, 1)) {
                    owner.inventory.removeItem(ItemStack(Material.IRON_INGOT, 1))
                    currentBullets = MAGAZINE_SIZE
                    owner.sendMessage("${ChatColor.GREEN}재장전 완료")
                } else {
                    owner.sendMessage("${ChatColor.RED}재장전 실패 - 탄창이 없습니다.")
                }
                isReloading = false
            }
        }
    })
}
