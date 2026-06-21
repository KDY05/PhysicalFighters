package io.github.kdy05.physicalFighters.ability.impl

import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.SkillContext
import io.github.kdy05.physicalFighters.ability.PFAbility
import io.github.kdy05.physicalFighters.ability.AbilityUtils
import io.github.kdy05.physicalFighters.util.PotionEffectFactory
import io.github.kdy05.physicalFighters.util.SoundUtils
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Directional
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

@AbilityMeta(
    name = "글레디에이터",
    rank = Rank.SSS,
    guide = [
        "(철괴 공격) 천공의 투기장으로 이동하여 15초간 1:1 대결을 펼칩니다.",
        "이때 상대는 디버프, 당신은 버프를 받습니다."
    ]
)
class Gladiator(owner: Player, context: SkillContext) : PFAbility(owner, context) {
    companion object {
        private const val ARENA_SIZE = 5
        private const val ARENA_HEIGHT = 8
        private const val ARENA_Y_OFFSET = 50
    }

    override fun buildSkills() = listOf(object : ActiveContinueSkill() {
        override val cooldownTicks = 1200L
        override val durationTicks = 300L
        private var savedTarget: LivingEntity? = null
        private var savedTargetLoc: Location? = null
        private var savedAttackerLoc: Location? = null
        private var savedArenaBase: Location? = null

        override fun register() {
            onEntityDamage { e ->
                if (owner.inventory.itemInMainHand.type != Material.IRON_INGOT) return@onEntityDamage
                savedTarget = e.entity as? LivingEntity ?: return@onEntityDamage
                activate()
            }
        }

        override fun onActivate() {
            val target = savedTarget ?: return
            savedTargetLoc = target.location.clone()
            savedAttackerLoc = owner.location.clone()
            val arenaBase = target.location.clone().also { it.y += ARENA_Y_OFFSET }
            savedArenaBase = arenaBase

            createArena(arenaBase)

            val targetArenaLoc = arenaBase.clone().add(2.0, 2.0, 2.0)
            val attackerArenaLoc = arenaBase.clone().add(-2.0, 2.0, -2.0)
            targetArenaLoc.direction = attackerArenaLoc.toVector().subtract(targetArenaLoc.toVector()).normalize()
            attackerArenaLoc.direction = targetArenaLoc.toVector().subtract(attackerArenaLoc.toVector()).normalize()
            target.teleport(targetArenaLoc)
            owner.teleport(attackerArenaLoc)

            target.addPotionEffect(PotionEffectFactory.createNausea(300, 0))
            target.addPotionEffect(PotionEffectFactory.createBlindness(300, 0))
            owner.addPotionEffect(PotionEffectFactory.createResistance(300, 0))
            owner.addPotionEffect(PotionEffectFactory.createStrength(300, 0))

            SoundUtils.broadcastWarningSound()
        }

        override fun onDeactivate() {
            savedTarget?.let { savedTargetLoc?.let { loc -> it.teleport(loc) } }
            savedAttackerLoc?.let { owner.teleport(it) }
            savedArenaBase?.let { AbilityUtils.createBox(it, Material.AIR, ARENA_SIZE, ARENA_HEIGHT, true) }
            savedTarget = null
            savedTargetLoc = null
            savedAttackerLoc = null
            savedArenaBase = null
        }

        private fun createArena(base: Location) {
            AbilityUtils.createBox(base, Material.BEDROCK, ARENA_SIZE, ARENA_HEIGHT)
            AbilityUtils.createBox(base.clone().add(0.0, 1.0, 0.0), Material.AIR, ARENA_SIZE - 1, ARENA_HEIGHT - 2, true)
            val torchY = (ARENA_HEIGHT - 2).toDouble()
            placeWallTorch(base.clone().add((ARENA_SIZE - 1).toDouble(), torchY, 0.0), BlockFace.WEST)
            placeWallTorch(base.clone().add(-(ARENA_SIZE - 1).toDouble(), torchY, 0.0), BlockFace.EAST)
            placeWallTorch(base.clone().add(0.0, torchY, (ARENA_SIZE - 1).toDouble()), BlockFace.NORTH)
            placeWallTorch(base.clone().add(0.0, torchY, -(ARENA_SIZE - 1).toDouble()), BlockFace.SOUTH)
        }

        private fun placeWallTorch(loc: Location, facing: BlockFace) {
            val block = loc.block
            block.type = Material.WALL_TORCH
            val data = block.blockData as? Directional ?: return
            data.facing = facing
            block.setBlockData(data)
        }
    })
}
