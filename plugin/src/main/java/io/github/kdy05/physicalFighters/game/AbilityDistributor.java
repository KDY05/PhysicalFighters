package io.github.kdy05.physicalFighters.game;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
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
     *
     * @param player 대상 플레이어
     * @param playerCount 현재 플레이어 수 (mirroring 능력 제한에 사용)
     * @return 능력 할당 성공 여부
     */
    public boolean assignRandomAbility(Player player, int playerCount) {
        // Remove current ability
        for (Ability ability : AbilityRegistry.AbilityList) {
            if (ability.isOwner(player)) {
                ability.setPlayer(null, false);
                break;
            }
        }

        List<Ability> availableAbilities = getAvailableAbilities(playerCount);
        if (availableAbilities.isEmpty()) return false;

        Ability selectedAbility = availableAbilities.get(random.nextInt(availableAbilities.size()));
        selectedAbility.setPlayer(player, false);
        return true;
    }

    /**
     * 모든 능력을 초기화합니다.
     */
    public void resetAllAbilities() {
        for (Ability ability : AbilityRegistry.AbilityList) {
            ability.setRunAbility(false);
            ability.setPlayer(null, false);
        }
    }

    /**
     * 모든 능력을 활성화합니다.
     */
    public void enableAllAbilities() {
        for (Ability ability : AbilityRegistry.AbilityList) {
            ability.setRunAbility(true);
            ability.setPlayer(ability.getPlayer(), false);
        }
    }

    /**
     * 현재 할당 가능한 능력 목록을 반환합니다.
     *
     * @param playerCount 현재 플레이어 수
     * @return 가용 능력 목록
     */
    private List<Ability> getAvailableAbilities(int playerCount) {
        List<Ability> available = new ArrayList<>();
        for (Ability ability : AbilityRegistry.AbilityList) {
            if (ability.getPlayer() == null &&
                (playerCount > 6 || ability != AbilityRegistry.mirroring)) {
                available.add(ability);
            }
        }
        return available;
    }
}
