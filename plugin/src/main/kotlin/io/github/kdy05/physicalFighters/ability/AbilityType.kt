package io.github.kdy05.physicalFighters.ability

import org.bukkit.entity.Player
import java.util.UUID

/**
 * 능력의 메타데이터(타입 카탈로그)와 팩토리를 담는 불변 객체.
 * 플러그인 로드 시 한 번 생성되며 게임 중 변경되지 않는다.
 */
class AbilityType internal constructor(
    prototype: Ability,
    private val factory: (UUID?) -> Ability
) {
    val name: String = prototype.abilityName
    val rank: Ability.Rank = prototype.rank
    val type: Ability.Type = prototype.abilityType
    private val _guide: Array<String> = prototype.guide.clone()
    val cooldown: Int = prototype.coolDown
    val duration: Int = prototype.duration
    val minimumPlayers: Int = prototype.minimumPlayers
    val isDeathExempt: Boolean = prototype.isDeathExempt
    val isInfoPrimary: Boolean = prototype.isInfoPrimary

    fun createInstance(player: Player): Ability = factory(player.uniqueId)

    fun getGuide(): Array<String> = _guide.clone()
}