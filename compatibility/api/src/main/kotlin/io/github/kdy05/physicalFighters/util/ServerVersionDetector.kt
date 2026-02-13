package io.github.kdy05.physicalFighters.util

import org.bukkit.Bukkit

object ServerVersionDetector {

    @JvmStatic
    fun detectVersion(): String {
        val version = Bukkit.getVersion()
        val start = version.indexOf("MC: ")
        if (start == -1) {
            return Bukkit.getBukkitVersion().split("-")[0]
        }
        val contentStart = start + 4
        val end = version.indexOf(")", contentStart)
        if (end == -1) {
            return Bukkit.getBukkitVersion().split("-")[0]
        }
        return version.substring(contentStart, end)
    }

    @JvmStatic
    fun getMajorMinorVersion(): String {
        val fullVersion = detectVersion()
        val parts = fullVersion.split(".")
        return if (parts.size >= 2) {
            "${parts[0]}.${parts[1]}"
        } else {
            fullVersion
        }
    }

    @JvmStatic
    fun compareVersions(version1: String, version2: String): Int {
        val v1 = parseVersion(version1)
        val v2 = parseVersion(version2)

        val maxLength = maxOf(v1.size, v2.size)
        for (i in 0 until maxLength) {
            val part1 = if (i < v1.size) v1[i] else 0
            val part2 = if (i < v2.size) v2[i] else 0
            if (part1 != part2) {
                return part1 - part2
            }
        }
        return 0
    }

    @JvmStatic
    fun isAtLeast(minVersion: String): Boolean {
        return compareVersions(detectVersion(), minVersion) >= 0
    }

    @JvmStatic
    fun isBelow(maxVersion: String): Boolean {
        return compareVersions(detectVersion(), maxVersion) < 0
    }

    @JvmStatic
    fun isBetween(minVersion: String, maxVersion: String): Boolean {
        val current = detectVersion()
        return compareVersions(current, minVersion) >= 0 && compareVersions(current, maxVersion) <= 0
    }

    private fun parseVersion(version: String): IntArray {
        return version.split(".").map { part ->
            part.toIntOrNull() ?: 0
        }.toIntArray()
    }
}
