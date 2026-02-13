package io.github.kdy05.physicalFighters.v1_20_5

import io.github.kdy05.physicalFighters.api.PotionEffectTypeAdapter
import io.github.kdy05.physicalFighters.util.ServerVersionDetector
import org.bukkit.potion.PotionEffectType

class PotionEffectTypeAdapter_1_20_5 : PotionEffectTypeAdapter {

    override fun SLOWNESS(): PotionEffectType = PotionEffectType.SLOWNESS
    override fun HASTE(): PotionEffectType = PotionEffectType.HASTE
    override fun MINING_FATIGUE(): PotionEffectType = PotionEffectType.MINING_FATIGUE
    override fun STRENGTH(): PotionEffectType = PotionEffectType.STRENGTH
    override fun INSTANT_HEALTH(): PotionEffectType = PotionEffectType.INSTANT_HEALTH
    override fun INSTANT_DAMAGE(): PotionEffectType = PotionEffectType.INSTANT_DAMAGE
    override fun JUMP_BOOST(): PotionEffectType = PotionEffectType.JUMP_BOOST
    override fun NAUSEA(): PotionEffectType = PotionEffectType.NAUSEA
    override fun RESISTANCE(): PotionEffectType = PotionEffectType.RESISTANCE

    override fun getSupportedVersion(): String = "1.20.5+"

    override fun isCompatible(serverVersion: String): Boolean =
        ServerVersionDetector.isAtLeast("1.20.5")
}
