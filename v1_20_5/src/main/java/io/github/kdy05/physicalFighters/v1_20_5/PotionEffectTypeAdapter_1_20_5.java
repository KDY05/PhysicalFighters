package io.github.kdy05.physicalFighters.v1_20_5;

import io.github.kdy05.physicalFighters.api.PotionEffectTypeAdapter;

import io.github.kdy05.physicalFighters.util.ServerVersionDetector;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectTypeAdapter_1_20_5 implements PotionEffectTypeAdapter {

    @Override
    public PotionEffectType SLOWNESS() {
        return PotionEffectType.SLOWNESS;
    }

    @Override
    public PotionEffectType HASTE() {
        return PotionEffectType.HASTE;
    }

    @Override
    public PotionEffectType MINING_FATIGUE() {
        return PotionEffectType.MINING_FATIGUE;
    }

    @Override
    public PotionEffectType STRENGTH() {
        return PotionEffectType.STRENGTH;
    }

    @Override
    public PotionEffectType INSTANT_HEALTH() {
        return PotionEffectType.INSTANT_HEALTH;
    }

    @Override
    public PotionEffectType INSTANT_DAMAGE() {
        return PotionEffectType.INSTANT_DAMAGE;
    }

    @Override
    public PotionEffectType JUMP_BOOST() {
        return PotionEffectType.JUMP_BOOST;
    }

    @Override
    public PotionEffectType NAUSEA() {
        return PotionEffectType.NAUSEA;
    }

    @Override
    public PotionEffectType RESISTANCE() {
        return PotionEffectType.RESISTANCE;
    }

    @Override
    public String getSupportedVersion() {
        return "1.20.5+";
    }

    @Override
    public boolean isCompatible(String serverVersion) {
        return ServerVersionDetector.isAtLeast("1.20.5");
    }
}