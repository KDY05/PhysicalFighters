package io.github.kdy05.physicalFighters.ability

import org.bukkit.entity.Player
import java.util.UUID

/**
 * 능력의 메타데이터(타입 카탈로그)와 팩토리를 담는 불변 객체.
 * 플러그인 로드 시 한 번 생성되며 게임 중 변경되지 않는다.
 */
class AbilityType(
    val spec: AbilitySpec,
    private val factory: (UUID) -> Ability
) {
    val name: String get() = spec.name
    val rank: Ability.Rank get() = spec.rank
    val type: Ability.Type get() = spec.type
    val guide: List<String> get() = spec.guide
    val cooldown: Int get() = spec.cooldown
    val duration: Int get() = spec.duration
    val minimumPlayers: Int get() = spec.minimumPlayers
    val isDeathExempt: Boolean get() = spec.isDeathExempt
    val isInfoPrimary: Boolean get() = spec.isInfoPrimary

    fun createInstance(player: Player): Ability = factory(player.uniqueId)
}
