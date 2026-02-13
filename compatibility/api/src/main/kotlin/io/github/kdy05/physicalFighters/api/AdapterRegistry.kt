package io.github.kdy05.physicalFighters.api

object AdapterRegistry {

    private var attributeAdapter: AttributeAdapter? = null
    private var potionEffectTypeAdapter: PotionEffectTypeAdapter? = null

    @JvmStatic
    fun register(attribute: AttributeAdapter, potionEffectType: PotionEffectTypeAdapter) {
        attributeAdapter = attribute
        potionEffectTypeAdapter = potionEffectType
    }

    @JvmStatic
    fun attribute(): AttributeAdapter = attributeAdapter!!

    @JvmStatic
    fun potionEffectType(): PotionEffectTypeAdapter = potionEffectTypeAdapter!!
}
