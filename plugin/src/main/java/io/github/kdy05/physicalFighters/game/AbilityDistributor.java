package io.github.kdy05.physicalFighters.game;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
import io.github.kdy05.physicalFighters.ability.AbilityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 능력 분배 로직을 담당하는 클래스
 */
final class AbilityDistributor {

    private final Random random = new Random();

    /**
     * 플레이어에게 랜덤 능력을 할당합니다.
     * 선택 단계에서 호출 — 이벤트는 등록하지 않음 (activate 하지 않음).
     */
    public boolean assignRandomAbility(Player player, int playerCount) {
        AbilityRegistry.deactivateAll(player);

        List<AbilityType> available = getAvailableTypes(playerCount);
        if (available.isEmpty()) return false;

        AbilityType selected = available.get(random.nextInt(available.size()));
        Ability instance = selected.createInstance(player);
        AbilityRegistry.addActive(instance);
        return true;
    }

    /**
     * 모든 활성 능력을 초기화합니다.
     */
    public void resetAllAbilities() {
        AbilityRegistry.deactivateAll();
    }

    /**
     * 모든 활성 능력을 활성화합니다 (이벤트 등록 + A_SetEvent).
     */
    public void enableAllAbilities() {
        for (Ability ability : AbilityRegistry.getActiveAbilities()) {
            ability.activate(false);
        }
    }

    /**
     * 현재 할당 가능한 능력 타입 목록을 반환합니다.
     */
    private List<AbilityType> getAvailableTypes(int playerCount) {
        List<AbilityType> available = new ArrayList<>();
        for (AbilityType type : AbilityRegistry.getAllTypes()) {
            if (playerCount >= type.getMinimumPlayers()) {
                available.add(type);
            }
        }
        return available;
    }
}
