package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

@AbilityMeta(
    name = "트랜스볼",
    rank = Rank.SS,
    guide = [
        "웅크리고 눈덩이를 우클릭하여 모드를 전환합니다.",
        "스왑 모드 - 눈덩이를 맞은 적과 위치를 교환합니다.",
        "그랩 모드 - 눈덩이를 맞은 적을 자신의 위치로 당겨옵니다.",
        "추격 모드 - 눈덩이를 맞은 적의 위치로 즉시 이동합니다."
    ]
)
class Tranceball(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    private enum class Mode(private val displayName: String) {
        Swap("스왑"), Grab("그랩"), Chase("추격");
        override fun toString() = displayName
    }

    override fun buildSkills() = listOf(object : BaseItemSkill() {
        override val baseItems = arrayOf(ItemStack(Material.SNOWBALL, 64))
        override val itemDisplayName = "눈덩이"
        private var mode = Mode.Swap

        override fun register() {
            registerItemEvents()
            on(ProjectileHitEvent::class) { e ->
                val snowball = e.entity as? Snowball ?: return@on
                val shooter = snowball.shooter as? Player ?: return@on
                if (shooter != owner) return@on
                val target = e.hitEntity as? LivingEntity ?: return@on
                if (target == owner) return@on

                val casterLoc = owner.location
                val targetLoc = target.location
                when (mode) {
                    Mode.Swap -> {
                        target.teleport(casterLoc)
                        owner.teleport(targetLoc)
                    }
                    Mode.Grab -> target.teleport(casterLoc)
                    Mode.Chase -> owner.teleport(targetLoc)
                }
                owner.inventory.addItem(ItemStack(Material.SNOWBALL, 1))
            }
            onRightClick { e ->
                if (owner.inventory.itemInMainHand.type != Material.SNOWBALL) return@onRightClick
                if (!owner.isSneaking) return@onRightClick
                e.isCancelled = true
                mode = when (mode) {
                    Mode.Swap -> Mode.Grab
                    Mode.Grab -> Mode.Chase
                    Mode.Chase -> Mode.Swap
                }
                owner.sendMessage("${ChatColor.AQUA}${mode} 모드")
            }
        }
    })
}
