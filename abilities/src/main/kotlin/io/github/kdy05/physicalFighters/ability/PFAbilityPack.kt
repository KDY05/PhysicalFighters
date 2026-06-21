package io.github.kdy05.physicalFighters.ability

import io.github.kdy05.abilityAPI.AbilityProvider
import io.github.kdy05.abilityAPI.ability.Ability
import io.github.kdy05.physicalFighters.ability.impl.Ace
import io.github.kdy05.physicalFighters.ability.impl.Aegis
import io.github.kdy05.physicalFighters.ability.impl.Akainu
import io.github.kdy05.physicalFighters.ability.impl.Aokizi
import io.github.kdy05.physicalFighters.ability.impl.Apollon
import io.github.kdy05.physicalFighters.ability.impl.Berserker
import io.github.kdy05.physicalFighters.ability.impl.Booster
import io.github.kdy05.physicalFighters.ability.impl.CP9
import io.github.kdy05.physicalFighters.ability.impl.Ckyomi
import io.github.kdy05.physicalFighters.ability.impl.Clocking
import io.github.kdy05.physicalFighters.ability.impl.Cuma
import io.github.kdy05.physicalFighters.ability.impl.Demigod
import io.github.kdy05.physicalFighters.ability.impl.Enel
import io.github.kdy05.physicalFighters.ability.impl.FallArrow
import io.github.kdy05.physicalFighters.ability.impl.Flower
import io.github.kdy05.physicalFighters.ability.impl.Fly
import io.github.kdy05.physicalFighters.ability.impl.Gladiator
import io.github.kdy05.physicalFighters.ability.impl.Guard
import io.github.kdy05.physicalFighters.ability.impl.Haki
import io.github.kdy05.physicalFighters.ability.impl.Hulk
import io.github.kdy05.physicalFighters.ability.impl.Killtolevelup
import io.github.kdy05.physicalFighters.ability.impl.Kimimaro
import io.github.kdy05.physicalFighters.ability.impl.Lockdown
import io.github.kdy05.physicalFighters.ability.impl.Luffy
import io.github.kdy05.physicalFighters.ability.impl.MachineGun
import io.github.kdy05.physicalFighters.ability.impl.Medic
import io.github.kdy05.physicalFighters.ability.impl.Mirroring
import io.github.kdy05.physicalFighters.ability.impl.Phoenix
import io.github.kdy05.physicalFighters.ability.impl.Poison
import io.github.kdy05.physicalFighters.ability.impl.Poseidon
import io.github.kdy05.physicalFighters.ability.impl.ReverseAlchemy
import io.github.kdy05.physicalFighters.ability.impl.Roclee
import io.github.kdy05.physicalFighters.ability.impl.Sasuke
import io.github.kdy05.physicalFighters.ability.impl.Shadow
import io.github.kdy05.physicalFighters.ability.impl.ShockWave
import io.github.kdy05.physicalFighters.ability.impl.SuperFan
import io.github.kdy05.physicalFighters.ability.impl.Temari
import io.github.kdy05.physicalFighters.ability.impl.Thor
import io.github.kdy05.physicalFighters.ability.impl.Tranceball
import io.github.kdy05.physicalFighters.ability.impl.Zombie

class PFAbilityPack : AbilityProvider {

    override fun namespace() = "physicalfighters"

    override fun provide(): List<Class<out Ability>> = listOf(
        Ace::class.java,
        Aegis::class.java,
        Akainu::class.java,
        Aokizi::class.java,
        Apollon::class.java,
        Berserker::class.java,
        Booster::class.java,
        CP9::class.java,
        Ckyomi::class.java,
        Clocking::class.java,
        Cuma::class.java,
        Demigod::class.java,
        Enel::class.java,
        FallArrow::class.java,
        Flower::class.java,
        Fly::class.java,
        Gladiator::class.java,
        Guard::class.java,
        Haki::class.java,
        Hulk::class.java,
        Killtolevelup::class.java,
        Kimimaro::class.java,
        Lockdown::class.java,
        Luffy::class.java,
        MachineGun::class.java,
        Medic::class.java,
        Mirroring::class.java,
        Phoenix::class.java,
        Poison::class.java,
        Poseidon::class.java,
        ReverseAlchemy::class.java,
        Roclee::class.java,
        Sasuke::class.java,
        Shadow::class.java,
        ShockWave::class.java,
        SuperFan::class.java,
        Temari::class.java,
        Thor::class.java,
        Tranceball::class.java,
        Zombie::class.java,
    )
}
