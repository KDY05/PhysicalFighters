package io.github.kdy05.physicalFighters.util;

import org.bukkit.Bukkit;

/**
 * Utility class for detecting the current server version.
 */
public class ServerVersionDetector {

    /**
     * Detects the current server version.
     *
     * @return Version string (e.g., "1.20.6", "1.21.4")
     */
    public static String detectVersion() {
        String version = Bukkit.getVersion();
        // Parse version from string like "git-Paper-123 (MC: 1.20.6)"
        int start = version.indexOf("MC: ");
        if (start == -1) {
            // Fallback: try to extract from Bukkit version
            return Bukkit.getBukkitVersion().split("-")[0];
        }
        start += 4;
        int end = version.indexOf(")", start);
        if (end == -1) {
            return Bukkit.getBukkitVersion().split("-")[0];
        }
        return version.substring(start, end);
    }

    /**
     * Gets the <major>.<minor> version (e.g., "1.20", "1.21").
     *
     * @return Major.minor version string
     */
    public static String getMajorMinorVersion() {
        String fullVersion = detectVersion();
        String[] parts = fullVersion.split("\\.");
        if (parts.length >= 2) {
            return parts[0] + "." + parts[1];
        }
        return fullVersion;
    }

    /**
     * Compares two version strings.
     *
     * @param version1 First version (e.g., "1.20.6")
     * @param version2 Second version (e.g., "1.21.0")
     * @return negative if version1 < version2, 0 if equal, positive if version1 > version2
     */
    public static int compareVersions(String version1, String version2) {
        int[] v1 = parseVersion(version1);
        int[] v2 = parseVersion(version2);

        int maxLength = Math.max(v1.length, v2.length);
        for (int i = 0; i < maxLength; i++) {
            int part1 = i < v1.length ? v1[i] : 0;
            int part2 = i < v2.length ? v2[i] : 0;
            if (part1 != part2) {
                return part1 - part2;
            }
        }
        return 0;
    }

    /**
     * Checks if the current server version is at least the specified version.
     *
     * @param minVersion Minimum version required (e.g., "1.21")
     * @return true if current version >= minVersion
     */
    public static boolean isAtLeast(String minVersion) {
        return compareVersions(detectVersion(), minVersion) >= 0;
    }

    /**
     * Checks if the current server version is below the specified version.
     *
     * @param maxVersion Version to compare against (e.g., "1.21")
     * @return true if current version < maxVersion
     */
    public static boolean isBelow(String maxVersion) {
        return compareVersions(detectVersion(), maxVersion) < 0;
    }

    /**
     * Checks if the current server version is within a range (inclusive).
     *
     * @param minVersion Minimum version (inclusive)
     * @param maxVersion Maximum version (inclusive)
     * @return true if minVersion <= current version <= maxVersion
     */
    public static boolean isBetween(String minVersion, String maxVersion) {
        String current = detectVersion();
        return compareVersions(current, minVersion) >= 0 && compareVersions(current, maxVersion) <= 0;
    }

    /**
     * Parses a version string into an array of integers.
     *
     * @param version Version string (e.g., "1.20.6")
     * @return Array of version parts as integers
     */
    private static int[] parseVersion(String version) {
        String[] parts = version.split("\\.");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                result[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }
        return result;
    }
}
