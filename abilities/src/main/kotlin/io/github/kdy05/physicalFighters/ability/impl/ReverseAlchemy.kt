package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.util.AttributeUtils
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AbilityMeta(
    name = "반 연금술",
    rank = Rank.A,
    guide = [
        "(철괴 좌클릭) 금괴 3개를 다이아몬드 1개로 변환합니다.",
        "(금괴 우클릭) 금괴를 소모하여 자신의 체력을 회복합니다.",
        "이때 체력이 최대 채력의 절반 이상이라면 체력을 전부 회복하며,",
        "절반 이하라면 최대 체력의 절반까지 회복합니다."
    ]
)
class ReverseAlchemy(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    companion object {
        private const val GOLD_FOR_HEALING = 1
        private const val GOLD_FOR_DIAMOND = 3
    }

    override fun buildSkills() = listOf(object : ActiveSkill() {
        override val cooldownTicks = 100L
        private var pendingAction: (() -> Unit)? = null

        override fun register() {
            onLeftClick {
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onLeftClick
                if (!owner.inventory.contains(Material.GOLD_INGOT, GOLD_FOR_DIAMOND)) {
                    owner.sendMessage("${ChatColor.RED}금괴가 ${GOLD_FOR_DIAMOND}개 필요합니다.")
                    return@onLeftClick
                }
                pendingAction = {
                    owner.inventory.removeItem(ItemStack(Material.GOLD_INGOT, GOLD_FOR_DIAMOND))
                    owner.inventory.addItem(ItemStack(Material.DIAMOND, 1))
                    owner.sendMessage("${ChatColor.GREEN}금괴 ${GOLD_FOR_DIAMOND}개로 다이아몬드를 만들었습니다.")
                }
                activate()
            }
            onRightClick {
                if (owner.inventory.itemInMainHand.type != Material.GOLD_INGOT) return@onRightClick
                if (!owner.inventory.contains(Material.GOLD_INGOT, GOLD_FOR_HEALING)) {
                    owner.sendMessage("${ChatColor.RED}금괴가 ${GOLD_FOR_HEALING}개 필요합니다.")
                    return@onRightClick
                }
                pendingAction = {
                    owner.inventory.removeItem(ItemStack(Material.GOLD_INGOT, GOLD_FOR_HEALING))
                    val maxHealth = AttributeUtils.getMaxHealth(owner)
                    owner.health = if (owner.health >= maxHealth / 2) maxHealth else maxHealth / 2
                    SoundUtils.playSuccessSound(owner)
                    owner.sendMessage("${ChatColor.GREEN}체력을 회복하였습니다.")
                }
                activate()
            }
        }

        override fun onActivate() {
            pendingAction?.invoke()
            pendingAction = null
        }
    })
}
