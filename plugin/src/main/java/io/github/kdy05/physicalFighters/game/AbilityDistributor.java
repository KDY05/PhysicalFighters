package io.github.kdy05.physicalFighters.game;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
import io.github.kdy05.physicalFighters.ability.AbilityType;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * 능력 분배 로직을 담당하는 클래스.
 * 중복을 최소화하여 균등하게 능력을 배분합니다.
 *
 * <p>예: 56명, 55종 능력 → 55명은 고유 능력, 1명만 중복</p>
 */
final class AbilityDistributor {

    private final Random random = new Random();

    /**
     * 플레이어 목록에게 중복을 최소화하여 랜덤 능력을 분배합니다.
     * 선택 단계에서 호출 — 이벤트는 등록하지 않음 (activate 하지 않음).
     *
     * @return 분배 성공 여부 (가용 능력이 없으면 false)
     */
    public boolean distributeAbilities(List<Player> players) {
        List<AbilityType> types = getAvailableTypes(players.size());
        if (types.isEmpty()) return false;

        List<AbilityType> pool = buildBalancedPool(types, players.size());
        Collections.shuffle(pool, random);

        for (int i = 0; i < players.size(); i++) {
            Ability instance = pool.get(i).createInstance(players.get(i));
            AbilityRegistry.addActive(instance);
        }
        return true;
    }

    /**
     * 단일 플레이어에게 중복을 최소화하여 랜덤 능력을 재할당합니다.
     * 현재 분배 상태에서 가장 적게 사용된 타입 중 하나를 선택합니다.
     *
     * @return 재할당 성공 여부
     */
    public boolean reassignRandomAbility(Player player, int playerCount) {
        AbilityRegistry.deactivateAll(player);

        List<AbilityType> types = getAvailableTypes(playerCount);
        if (types.isEmpty()) return false;

        // 현재 사용 빈도 계산
        Map<String, Integer> usageCount = new HashMap<>();
        for (AbilityType type : types) {
            usageCount.put(type.getName(), 0);
        }
        for (Ability active : AbilityRegistry.getActiveAbilities()) {
            usageCount.merge(active.getAbilityName(), 1, Integer::sum);
        }

        // 최소 사용 빈도의 타입 중 랜덤 선택
        int minCount = Collections.min(usageCount.values());
        List<AbilityType> candidates = new ArrayList<>();
        for (AbilityType type : types) {
            if (usageCount.getOrDefault(type.getName(), 0) == minCount) {
                candidates.add(type);
            }
        }

        AbilityType selected = candidates.get(random.nextInt(candidates.size()));
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
     * 중복을 최소화한 능력 풀을 생성합니다.
     *
     * <p>playerCount를 typeCount로 나누어 기본 세트 수와 나머지를 계산합니다.
     * 기본 세트만큼 전체 타입을 반복 추가하고, 나머지는 랜덤으로 선택합니다.</p>
     *
     * <p>예: 56명, 55종 → fullSets=1, remainder=1 → 55개 고유 + 1개 랜덤 중복</p>
     * <p>예: 110명, 55종 → fullSets=2, remainder=0 → 각 능력 정확히 2번</p>
     */
    private List<AbilityType> buildBalancedPool(List<AbilityType> types, int playerCount) {
        List<AbilityType> pool = new ArrayList<>(playerCount);
        int fullSets = playerCount / types.size();
        int remainder = playerCount % types.size();

        for (int i = 0; i < fullSets; i++) {
            pool.addAll(types);
        }

        if (remainder > 0) {
            List<AbilityType> extra = new ArrayList<>(types);
            Collections.shuffle(extra, random);
            pool.addAll(extra.subList(0, remainder));
        }

        return pool;
    }

    /**
     * 현재 플레이어 수에서 할당 가능한 능력 타입 목록을 반환합니다.
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
