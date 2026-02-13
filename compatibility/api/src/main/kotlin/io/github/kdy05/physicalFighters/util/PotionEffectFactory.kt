package io.github.kdy05.physicalFighters.util

import io.github.kdy05.physicalFighters.api.AdapterRegistry
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object PotionEffectFactory {

    private fun adapter() = AdapterRegistry.potionEffectType()

    // === Adapter 사용 (1.20.5에서 이름 변경됨) ===

    @JvmStatic
    fun createSlowness(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(adapter().SLOWNESS(), durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createResistance(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(adapter().RESISTANCE(), durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createHaste(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(adapter().HASTE(), durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createMiningFatigue(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(adapter().MINING_FATIGUE(), durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createStrength(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(adapter().STRENGTH(), durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createJumpBoost(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(adapter().JUMP_BOOST(), durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createNausea(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(adapter().NAUSEA(), durationTicks, amplifier, false, false, false)

    // === 직접 사용 (이름 변경 없음) ===

    @JvmStatic
    fun createBlindness(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(PotionEffectType.BLINDNESS, durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createWeakness(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(PotionEffectType.WEAKNESS, durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createRegeneration(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(PotionEffectType.REGENERATION, durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createSpeed(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(PotionEffectType.SPEED, durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createPoison(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(PotionEffectType.POISON, durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createFireResistance(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(PotionEffectType.FIRE_RESISTANCE, durationTicks, amplifier, false, false, false)

    @JvmStatic
    fun createWaterBreathing(durationTicks: Int, amplifier: Int): PotionEffect =
        PotionEffect(PotionEffectType.WATER_BREATHING, durationTicks, amplifier, false, false, false)
}
