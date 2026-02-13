package io.github.kdy05.physicalFighters.v1_16_5

import io.github.kdy05.physicalFighters.api.AttributeAdapter
import io.github.kdy05.physicalFighters.util.ServerVersionDetector
import org.bukkit.attribute.Attribute

class AttributeAdapter_1_16_5 : AttributeAdapter {

    override fun getMaxHealthAttribute(): Attribute = Attribute.GENERIC_MAX_HEALTH

    override fun getSupportedVersion(): String = "1.16.5-1.21.2"

    override fun isCompatible(serverVersion: String): Boolean =
        ServerVersionDetector.isBetween("1.16.5", "1.21.2")
}
