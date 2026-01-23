package io.github.kdy05.physicalFighters.api;

import org.bukkit.attribute.Attribute;

/**
 * Version-specific attribute adapter interface.
 * Implementations provide access to the correct attribute
 * for their respective Minecraft version.
 */
public interface AttributeAdapter {

    /**
     * Gets the max health attribute for this version.
     *
     * @return The Attribute enum constant for max health
     *         - 1.16.5+: GENERIC_MAX_HEALTH
     *         - 1.21.2+: MAX_HEALTH
     */
    Attribute getMaxHealthAttribute();

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
