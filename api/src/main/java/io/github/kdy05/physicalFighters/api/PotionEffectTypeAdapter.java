package io.github.kdy05.physicalFighters.api;

import org.bukkit.potion.PotionEffectType;

public interface PotionEffectTypeAdapter {

    PotionEffectType SLOWNESS();
    PotionEffectType HASTE();
    PotionEffectType MINING_FATIGUE();
    PotionEffectType STRENGTH();
    PotionEffectType INSTANT_HEALTH();
    PotionEffectType INSTANT_DAMAGE();
    PotionEffectType JUMP_BOOST();
    PotionEffectType NAUSEA();
    PotionEffectType RESISTANCE();

    /**
     * Gets the supported version string.
     *
     * @return Version identifier (e.g., "1.20.6", "1.21")
     */
    String getSupportedVersion();

    /**
     * Checks if this adapter supports the current server version.
     *
     * @param serverVersion The server's version string
     * @return true if compatible, false otherwise
     */
    boolean isCompatible(String serverVersion);
}
