package io.github.kdy05.physicalFighters.ability

import io.github.kdy05.physicalFighters.ability.Ability.*

class AbilitySpecBuilder(
    private val name: String,
    private val type: Type,
    private val rank: Rank
) {
    private var cooldown: Int = 0
    private var duration: Int = 0
    private var showText: ShowText = ShowText.AllText
    private var guide: List<String> = emptyList()
    private var minimumPlayers: Int = 0
    private var isDeathExempt: Boolean = false
    private var isInfoPrimary: Boolean = false

    fun cooldown(cooldown: Int) = apply { this.cooldown = cooldown }
    fun duration(duration: Int) = apply { this.duration = duration }
    fun showText(showText: ShowText) = apply { this.showText = showText }
    fun guide(vararg guide: String) = apply { this.guide = listOf(*guide) }
    fun minimumPlayers(minimumPlayers: Int) = apply { this.minimumPlayers = minimumPlayers }
    fun deathExempt(deathExempt: Boolean) = apply { this.isDeathExempt = deathExempt }
    fun infoPrimary(infoPrimary: Boolean) = apply { this.isInfoPrimary = infoPrimary }

    fun build() = AbilitySpec(
        name = name,
        type = type,
        rank = rank,
        cooldown = cooldown,
        duration = duration,
        showText = showText,
        guide = guide,
        minimumPlayers = minimumPlayers,
        isDeathExempt = isDeathExempt,
        isInfoPrimary = isInfoPrimary
    )
}
