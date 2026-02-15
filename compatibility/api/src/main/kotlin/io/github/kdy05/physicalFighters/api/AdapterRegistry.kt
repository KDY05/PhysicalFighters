package io.github.kdy05.physicalFighters.api

object AdapterRegistry {

    private lateinit var attributeAdapter: AttributeAdapter
    private lateinit var potionEffectTypeAdapter: PotionEffectTypeAdapter

    @JvmStatic
    fun register(attribute: AttributeAdapter, potionEffectType: PotionEffectTypeAdapter) {
        attributeAdapter = attribute
        potionEffectTypeAdapter = potionEffectType
    }

    @JvmStatic
    fun attribute(): AttributeAdapter = attributeAdapter

    @JvmStatic
    fun potionEffectType(): PotionEffectTypeAdapter = potionEffectTypeAdapter
}
