package io.github.kdy05.physicalFighters.api

import org.bukkit.potion.PotionEffectType

interface PotionEffectTypeAdapter : VersionedAdapter {

    fun SLOWNESS(): PotionEffectType
    fun HASTE(): PotionEffectType
    fun MINING_FATIGUE(): PotionEffectType
    fun STRENGTH(): PotionEffectType
    fun INSTANT_HEALTH(): PotionEffectType
    fun INSTANT_DAMAGE(): PotionEffectType
    fun JUMP_BOOST(): PotionEffectType
    fun NAUSEA(): PotionEffectType
    fun RESISTANCE(): PotionEffectType
}
