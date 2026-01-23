package io.github.kdy05.physicalFighters.utils;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.api.PotionEffectTypeAdapter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectFactory {

    private static PotionEffectTypeAdapter adapter() {
        return PhysicalFighters.getPotionEffectTypeAdapter();
    }

    // === Adapter 사용 (1.20.5에서 이름 변경됨) ===

    public static PotionEffect createSlowness(int durationTicks, int amplifier) {
        return new PotionEffect(adapter().SLOWNESS(), durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createResistance(int durationTicks, int amplifier) {
        return new PotionEffect(adapter().RESISTANCE(), durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createHaste(int durationTicks, int amplifier) {
        return new PotionEffect(adapter().HASTE(), durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createMiningFatigue(int durationTicks, int amplifier) {
        return new PotionEffect(adapter().MINING_FATIGUE(), durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createStrength(int durationTicks, int amplifier) {
        return new PotionEffect(adapter().STRENGTH(), durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createJumpBoost(int durationTicks, int amplifier) {
        return new PotionEffect(adapter().JUMP_BOOST(), durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createNausea(int durationTicks, int amplifier) {
        return new PotionEffect(adapter().NAUSEA(), durationTicks, amplifier, false, false, false);
    }

    // === 직접 사용 (이름 변경 없음) ===

    public static PotionEffect createBlindness(int durationTicks, int amplifier) {
        return new PotionEffect(PotionEffectType.BLINDNESS, durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createWeakness(int durationTicks, int amplifier) {
        return new PotionEffect(PotionEffectType.WEAKNESS, durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createRegeneration(int durationTicks, int amplifier) {
        return new PotionEffect(PotionEffectType.REGENERATION, durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createSpeed(int durationTicks, int amplifier) {
        return new PotionEffect(PotionEffectType.SPEED, durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createPoison(int durationTicks, int amplifier) {
        return new PotionEffect(PotionEffectType.POISON, durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createFireResistance(int durationTicks, int amplifier) {
        return new PotionEffect(PotionEffectType.FIRE_RESISTANCE, durationTicks, amplifier, false, false, false);
    }

    public static PotionEffect createWaterBreathing(int durationTicks, int amplifier) {
        return new PotionEffect(PotionEffectType.WATER_BREATHING, durationTicks, amplifier, false, false, false);
    }

}
