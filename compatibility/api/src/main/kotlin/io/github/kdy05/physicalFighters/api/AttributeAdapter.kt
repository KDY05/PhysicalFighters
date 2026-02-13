package io.github.kdy05.physicalFighters.api

import org.bukkit.attribute.Attribute

interface AttributeAdapter : VersionedAdapter {

    fun getMaxHealthAttribute(): Attribute
}
