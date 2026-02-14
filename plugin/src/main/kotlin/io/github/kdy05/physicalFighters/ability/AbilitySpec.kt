package io.github.kdy05.physicalFighters.ability

import io.github.kdy05.physicalFighters.ability.Ability.Rank
import io.github.kdy05.physicalFighters.ability.Ability.ShowText
import io.github.kdy05.physicalFighters.ability.Ability.Type

class AbilitySpec private constructor(builder: Builder) {
    val name: String = builder.name
    val type: Type = builder.type
    val rank: Rank = builder.rank
    val cooldown: Int = builder.cooldown
    val duration: Int = builder.duration
    val showText: ShowText = builder.showText
    val guide: Array<String> = builder.guide

    companion object {
        @JvmStatic
        fun builder(name: String, type: Type, rank: Rank) = Builder(name, type, rank)
    }

    class Builder(
        internal val name: String,
        internal val type: Type,
        internal val rank: Rank
    ) {
        internal var cooldown: Int = 0
        internal var duration: Int = 0
        internal var showText: ShowText = ShowText.AllText
        internal var guide: Array<String> = emptyArray()

        fun cooldown(cooldown: Int) = apply { this.cooldown = cooldown }
        fun duration(duration: Int) = apply { this.duration = duration }
        fun showText(showText: ShowText) = apply { this.showText = showText }
        fun guide(vararg guide: String) = apply { this.guide = arrayOf(*guide) }
        fun build() = AbilitySpec(this)
    }
}