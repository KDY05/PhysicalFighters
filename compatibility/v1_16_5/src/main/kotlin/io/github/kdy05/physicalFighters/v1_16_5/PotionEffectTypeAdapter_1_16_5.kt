package io.github.kdy05.physicalFighters.v1_16_5

import io.github.kdy05.physicalFighters.api.PotionEffectTypeAdapter
import io.github.kdy05.physicalFighters.util.ServerVersionDetector
import org.bukkit.potion.PotionEffectType

class PotionEffectTypeAdapter_1_16_5 : PotionEffectTypeAdapter {

    override fun SLOWNESS(): PotionEffectType = PotionEffectType.SLOW
    override fun HASTE(): PotionEffectType = PotionEffectType.FAST_DIGGING
    override fun MINING_FATIGUE(): PotionEffectType = PotionEffectType.SLOW_DIGGING
    override fun STRENGTH(): PotionEffectType = PotionEffectType.INCREASE_DAMAGE
    override fun INSTANT_HEALTH(): PotionEffectType = PotionEffectType.HEAL
    override fun INSTANT_DAMAGE(): PotionEffectType = PotionEffectType.HARM
    override fun JUMP_BOOST(): PotionEffectType = PotionEffectType.JUMP
    override fun NAUSEA(): PotionEffectType = PotionEffectType.CONFUSION
    override fun RESISTANCE(): PotionEffectType = PotionEffectType.DAMAGE_RESISTANCE

    override fun getSupportedVersion(): String = "1.16.5-1.20.4"

    override fun isCompatible(serverVersion: String): Boolean =
        ServerVersionDetector.isBetween("1.16.5", "1.20.4")
}
