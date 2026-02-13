package io.github.kdy05.physicalFighters.v1_21_3

import io.github.kdy05.physicalFighters.api.AttributeAdapter
import io.github.kdy05.physicalFighters.util.ServerVersionDetector
import org.bukkit.attribute.Attribute

class AttributeAdapter_1_21_3 : AttributeAdapter {

    override fun getMaxHealthAttribute(): Attribute = Attribute.MAX_HEALTH

    override fun getSupportedVersion(): String = "1.21.3+"

    override fun isCompatible(serverVersion: String): Boolean =
        ServerVersionDetector.isAtLeast("1.21.3")
}
