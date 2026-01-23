package io.github.kdy05.physicalFighters.v1_16_5;

import io.github.kdy05.physicalFighters.api.PotionEffectTypeAdapter;
import io.github.kdy05.physicalFighters.util.ServerVersionDetector;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectTypeAdapter_1_16_5 implements PotionEffectTypeAdapter {

    @Override
    public PotionEffectType SLOWNESS() {
        return PotionEffectType.SLOW;
    }

    @Override
    public PotionEffectType HASTE() {
        return PotionEffectType.FAST_DIGGING;
    }

    @Override
    public PotionEffectType MINING_FATIGUE() {
        return PotionEffectType.SLOW_DIGGING;
    }

    @Override
    public PotionEffectType STRENGTH() {
        return PotionEffectType.INCREASE_DAMAGE;
    }

    @Override
    public PotionEffectType INSTANT_HEALTH() {
        return PotionEffectType.HEAL;
    }

    @Override
    public PotionEffectType INSTANT_DAMAGE() {
        return PotionEffectType.HARM;
    }

    @Override
    public PotionEffectType JUMP_BOOST() {
        return PotionEffectType.JUMP;
    }

    @Override
    public PotionEffectType NAUSEA() {
        return PotionEffectType.CONFUSION;
    }

    @Override
    public PotionEffectType RESISTANCE() {
        return PotionEffectType.DAMAGE_RESISTANCE;
    }

    @Override
    public String getSupportedVersion() {
        return "1.16.5-1.20.4";
    }

    @Override
    public boolean isCompatible(String serverVersion) {
        return ServerVersionDetector.isBetween("1.16.5", "1.20.4");
    }
}
