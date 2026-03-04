package io.github.kdy05.physicalFighters.game

import io.github.kdy05.physicalFighters.ability.Ability
import io.github.kdy05.physicalFighters.util.EventData

interface EventRegistry {
    fun registerLeftClick(ability: Ability)
    fun registerRightClick(ability: Ability)
    fun registerEntityTarget(data: EventData)
    fun registerEntityDamage(data: EventData)
    fun registerEntityDamageByEntity(data: EventData)
    fun registerEntityDeath(data: EventData)
    fun registerPlayerRespawn(data: EventData)
    fun registerBlockBreak(data: EventData)
    fun registerSignChange(data: EventData)
    fun registerProjectileLaunch(data: EventData)
    fun registerPlayerDropItem(data: EventData)
    fun registerPlayerMove(data: EventData)
    fun registerProjectileHit(data: EventData)
    fun unregisterAll(ability: Ability)
}

object NoOpEventRegistry : EventRegistry {
    override fun registerLeftClick(ability: Ability) {}
    override fun registerRightClick(ability: Ability) {}
    override fun registerEntityTarget(data: EventData) {}
    override fun registerEntityDamage(data: EventData) {}
    override fun registerEntityDamageByEntity(data: EventData) {}
    override fun registerEntityDeath(data: EventData) {}
    override fun registerPlayerRespawn(data: EventData) {}
    override fun registerBlockBreak(data: EventData) {}
    override fun registerSignChange(data: EventData) {}
    override fun registerProjectileLaunch(data: EventData) {}
    override fun registerPlayerDropItem(data: EventData) {}
    override fun registerPlayerMove(data: EventData) {}
    override fun registerProjectileHit(data: EventData) {}
    override fun unregisterAll(ability: Ability) {}
}
