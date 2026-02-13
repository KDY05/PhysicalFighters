package io.github.kdy05.physicalFighters.util

import io.github.kdy05.physicalFighters.api.AdapterRegistry
import org.bukkit.entity.LivingEntity

object AttributeUtils {

    @JvmStatic
    fun getMaxHealth(entity: LivingEntity): Double {
        val instance = entity.getAttribute(AdapterRegistry.attribute().getMaxHealthAttribute())
            ?: return 20.0
        return instance.value
    }
}
