package io.github.kdy05.physicalFighters.ability

import io.github.kdy05.physicalFighters.ability.Ability.*

data class AbilitySpec(
    val name: String,
    val type: Type,
    val rank: Rank,
    val cooldown: Int = 0,
    val duration: Int = 0,
    val showText: ShowText = ShowText.AllText,
    val guide: List<String> = emptyList(),
    val minimumPlayers: Int = 0,
    val isDeathExempt: Boolean = false,
    val isInfoPrimary: Boolean = false
) {
    companion object {
        @JvmStatic
        fun builder(name: String, type: Type, rank: Rank) = AbilitySpecBuilder(name, type, rank)
    }
}
