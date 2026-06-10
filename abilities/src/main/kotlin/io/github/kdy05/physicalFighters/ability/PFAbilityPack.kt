package io.github.kdy05.physicalFighters.ability

import io.github.kdy05.abilityAPI.AbilityProvider
import io.github.kdy05.abilityAPI.ability.Ability
import io.github.kdy05.physicalFighters.ability.impl.AkainuAbility
import io.github.kdy05.physicalFighters.ability.impl.AokiziAbility
import io.github.kdy05.physicalFighters.ability.impl.BerserkerAbility
import io.github.kdy05.physicalFighters.ability.impl.CP9Ability
import io.github.kdy05.physicalFighters.ability.impl.CumaAbility
import io.github.kdy05.physicalFighters.ability.impl.KijaruAbility
import io.github.kdy05.physicalFighters.ability.impl.LuffyAbility
import io.github.kdy05.physicalFighters.ability.impl.ShadowAbility
import io.github.kdy05.physicalFighters.ability.impl.ShockWaveAbility
import io.github.kdy05.physicalFighters.ability.impl.ZombieAbility

class PFAbilityPack : AbilityProvider {

    override fun namespace() = "physicalfighters"

    override fun provide(): List<Class<out Ability>> = listOf(
        AkainuAbility::class.java,
        AokiziAbility::class.java,
        BerserkerAbility::class.java,
        CP9Ability::class.java,
        CumaAbility::class.java,
        KijaruAbility::class.java,
        LuffyAbility::class.java,
        ShadowAbility::class.java,
        ShockWaveAbility::class.java,
        ZombieAbility::class.java,
    )
}
