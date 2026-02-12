package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.ability.impl.*;
import io.github.kdy05.physicalFighters.command.CommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public final class AbilityRegistry {

    // --- 타입 카탈로그 (플러그인 로드 시 고정) ---

    private static final LinkedHashMap<String, AbilityType> typesByName = new LinkedHashMap<>();
    private static final List<AbilityType> typeList = new ArrayList<>();

    // --- 활성 인스턴스 (게임 진행 중 동적 변화) ---

    private static final ConcurrentHashMap<UUID, CopyOnWriteArrayList<Ability>> abilitiesByPlayer = new ConcurrentHashMap<>();

    // --- 커맨드 핸들러 (activate/deactivate 시 등록/해제) ---

    private static final CopyOnWriteArrayList<CommandInterface> commandHandlers = new CopyOnWriteArrayList<>();

    // --- 타입 등록 ---

    private static void register(Function<UUID, Ability> factory) {
        Ability prototype = factory.apply(null);
        AbilityType type = new AbilityType(prototype, factory);
        typesByName.put(type.getName(), type);
        typeList.add(type);
    }

    // --- 타입 카탈로그 API ---

    public static List<AbilityType> getAllTypes() {
        return Collections.unmodifiableList(typeList);
    }

    public static AbilityType getType(String name) {
        return typesByName.get(name);
    }

    public static int getTypeCount() {
        return typeList.size();
    }

    // --- 인스턴스 라이프사이클 ---

    public static Ability createAndActivate(String typeName, Player player) {
        return createAndActivate(typeName, player, true);
    }

    public static Ability createAndActivate(String typeName, Player player, boolean textout) {
        AbilityType type = typesByName.get(typeName);
        if (type == null) return null;
        Ability instance = type.createInstance(player);
        addToIndex(instance, player);
        instance.activate(textout);
        return instance;
    }

    public static void deactivate(Ability ability) {
        deactivate(ability, true);
    }

    public static void deactivate(Ability ability, boolean textout) {
        ability.deactivate(textout);
        removeFromIndex(ability);
    }

    public static void deactivateAll(Player player) {
        CopyOnWriteArrayList<Ability> list = abilitiesByPlayer.remove(player.getUniqueId());
        if (list == null) return;
        for (Ability ability : list) {
            ability.deactivate(true);
        }
    }

    public static void deactivateAll() {
        for (CopyOnWriteArrayList<Ability> list : abilitiesByPlayer.values()) {
            for (Ability ability : list) {
                ability.deactivate(false);
            }
        }
        abilitiesByPlayer.clear();
        commandHandlers.clear();
    }

    /** 선택 단계에서 인스턴스를 등록 (activate 하지 않음) */
    public static void addActive(Ability ability) {
        Player player = ability.getPlayer();
        if (player != null) {
            addToIndex(ability, player);
        }
    }

    // --- 커맨드 등록 API ---

    public static void registerCommand(CommandInterface handler) {
        commandHandlers.add(handler);
    }

    public static void unregisterCommand(CommandInterface handler) {
        commandHandlers.remove(handler);
    }

    public static boolean dispatchCommand(CommandSender sender, Command command, String label, String[] args) {
        for (CommandInterface handler : commandHandlers) {
            if (handler.onCommandEvent(sender, command, label, args)) return true;
        }
        return false;
    }

    // --- 조회 API ---

    public static Ability findAbility(Player player) {
        CopyOnWriteArrayList<Ability> list = abilitiesByPlayer.get(player.getUniqueId());
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    public static List<Ability> findAbilities(Player player) {
        CopyOnWriteArrayList<Ability> list = abilitiesByPlayer.get(player.getUniqueId());
        return list != null ? Collections.unmodifiableList(list) : Collections.emptyList();
    }

    public static Ability findByType(String typeName, Player owner) {
        CopyOnWriteArrayList<Ability> list = abilitiesByPlayer.get(owner.getUniqueId());
        if (list == null) return null;
        for (Ability a : list) {
            if (a.getAbilityName().equals(typeName)) return a;
        }
        return null;
    }

    public static Ability findPrimaryAbility(Player player) {
        CopyOnWriteArrayList<Ability> list = abilitiesByPlayer.get(player.getUniqueId());
        if (list == null) return null;
        for (Ability a : list) {
            if (a.isInfoPrimary()) return a;
        }
        return null;
    }

    /** 전체 활성 능력 목록 (운영 명령어 등 비빈번 용도) */
    public static List<Ability> getActiveAbilities() {
        List<Ability> all = new ArrayList<>();
        for (CopyOnWriteArrayList<Ability> list : abilitiesByPlayer.values()) {
            all.addAll(list);
        }
        return Collections.unmodifiableList(all);
    }

    // --- 인덱스 관리 ---

    private static void addToIndex(Ability ability, Player player) {
        abilitiesByPlayer.computeIfAbsent(player.getUniqueId(), k -> new CopyOnWriteArrayList<>()).add(ability);
    }

    private static void removeFromIndex(Ability ability) {
        Player player = ability.getPlayer();
        if (player == null) return;
        CopyOnWriteArrayList<Ability> list = abilitiesByPlayer.get(player.getUniqueId());
        if (list != null) {
            list.remove(ability);
            if (list.isEmpty()) {
                abilitiesByPlayer.remove(player.getUniqueId());
            }
        }
    }

    // --- 정적 초기화 ---

    static {
        register(CP9::new);                 // CP9
        // ㄱ
        register(Gaara::new);               // 가아라
        register(Enel::new);                // 갓 에넬
        register(Fish::new);                // 강태공
        register(Berserker::new);           // 광전사
        register(Shadow::new);              // 그림자
        register(Gladiator::new);           // 글레디에이터
        register(MachineGun::new);          // 기관총
        // ㄴ
        // ㄷ
        register(Demigod::new);             // 데미갓
        register(PoisonArrow::new);         // 독화살
        // ㄹ
        register(Roclee::new);              // 록리
        register(Luffy::new);               // 루피
        // ㅁ
        register(Multishot::new);           // 멀티샷
        register(Medic::new);               // 메딕
        register(Guard::new);               // 목둔
        register(Mirroring::new);           // 미러링
        // ㅂ
        register(Cuma::new);                // 바솔로뮤 쿠마
        register(ReverseAlchemy::new);      // 반연금술
        register(Lockdown::new);            // 봉인
        register(Booster::new);             // 부스터
        register(Phoenix::new);             // 불사조
        register(Boom::new);                // 붐포인트
        // ㅅ
        register(Sasuke::new);              // 사스케
        register(SuperFan::new);            // 선풍기
        register(Teleporter::new);          // 소환술사
        register(ShockWave::new);           // 쇼크웨이브
        register(Trash::new);               // 쓰레기
        // ㅇ
        register(Aokizi::new);              // 아오키지
        register(Archer::new);              // 아쳐
        register(Akainu::new);              // 아카이누
        register(Apollon::new);             // 아폴론
        register(Ace::new);                 // 에이스
        register(Aegis::new);               // 이지스
        register(Explosion::new);           // 익스플로젼
        // ㅈ
        register(Zoro::new);                // 조로
        register(Zombie::new);              // 좀비
        register(FallArrow::new);           // 중력화살
        // ㅊ
        register(Ckyomi::new);              // 츠쿠요미
        // ㅋ
        register(Kaiji::new);               // 카이지
        register(Crocodile::new);           // 크로커다일
        register(Clocking::new);            // 클로킹
        register(Kimimaro::new);            // 키미마로
        register(Kijaru::new);              // 키자루
        // ㅌ
        register(Temari::new);              // 테마리
        register(Thor::new);                // 토르
        register(Tranceball::new);          // 트랜스볼
        // ㅍ
        register(Haki::new);                // 패기
        register(Poseidon::new);            // 포세이돈
        register(Poison::new);              // 포이즌
        register(Killtolevelup::new);       // 폭주
        register(Fly::new);                 // 플라이
        // ㅎ
        register(NuclearPunch::new);        // 핵펀치
        register(Hulk::new);                // 헐크
        register(Assimilation::new);        // 흡수
        register(Flower::new);              // 흡혈초
    }
}
