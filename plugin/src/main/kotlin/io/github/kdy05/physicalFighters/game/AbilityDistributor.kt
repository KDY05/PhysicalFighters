package io.github.kdy05.physicalFighters.game

import io.github.kdy05.physicalFighters.ability.AbilityRegistry
import io.github.kdy05.physicalFighters.ability.AbilityType
import org.bukkit.entity.Player

class AbilityDistributor {

    /**
     * 플레이어 목록에게 중복을 최소화하여 랜덤 능력을 분배합니다.
     * 선택 단계에서 호출 — 이벤트는 등록하지 않음 (activate 하지 않음).
     *
     * @return 분배 성공 여부 (가용 능력이 없으면 false)
     */
    fun distributeAbilities(players: List<Player>): Boolean {
        val types = getAvailableTypes(players.size)
        if (types.isEmpty()) return false

        val pool = buildBalancedPool(types, players.size)
        pool.shuffle()

        for (i in players.indices) {
            val instance = pool[i].createInstance(players[i])
            AbilityRegistry.addActive(instance)
        }
        return true
    }

    /**
     * 단일 플레이어에게 중복을 최소화하여 랜덤 능력을 재할당합니다.
     * 현재 분배 상태에서 가장 적게 사용된 타입 중 하나를 선택합니다.
     *
     * @return 재할당 성공 여부
     */
    fun reassignRandomAbility(player: Player, playerCount: Int): Boolean {
        AbilityRegistry.deactivateAll(player)

        val types = getAvailableTypes(playerCount)
        if (types.isEmpty()) return false

        // 현재 사용 빈도 계산
        val usageCount = mutableMapOf<String, Int>()
        for (type in types) {
            usageCount[type.name] = 0
        }
        for (active in AbilityRegistry.getActiveAbilities()) {
            usageCount.merge(active.abilityName, 1, Int::plus)
        }

        // 최소 사용 빈도의 타입 중 랜덤 선택
        val minCount = usageCount.values.min()
        val candidates = types.filter { (usageCount[it.name] ?: 0) == minCount }

        val selected = candidates.random()
        val instance = selected.createInstance(player)
        AbilityRegistry.addActive(instance)
        return true
    }

    fun resetAllAbilities() {
        AbilityRegistry.deactivateAll()
    }

    fun enableAllAbilities() {
        for (ability in AbilityRegistry.getActiveAbilities()) {
            AbilityRegistry.activate(ability, false)
        }
    }

    /**
     * 중복을 최소화한 능력 풀을 생성합니다.
     */
    private fun buildBalancedPool(types: List<AbilityType>, playerCount: Int): MutableList<AbilityType> {
        val pool = mutableListOf<AbilityType>()
        val fullSets = playerCount / types.size
        val remainder = playerCount % types.size

        repeat(fullSets) { pool.addAll(types) }

        if (remainder > 0) {
            val extra = types.toMutableList()
            extra.shuffle()
            pool.addAll(extra.subList(0, remainder))
        }

        return pool
    }

    private fun getAvailableTypes(playerCount: Int): List<AbilityType> =
        AbilityRegistry.getAllTypes().filter { playerCount >= it.minimumPlayers }
}
