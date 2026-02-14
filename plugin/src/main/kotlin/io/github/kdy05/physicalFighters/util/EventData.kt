package io.github.kdy05.physicalFighters.util

import io.github.kdy05.physicalFighters.ability.Ability

data class EventData @JvmOverloads constructor(
    val ability: Ability,
    val parameter: Int = 0
)