package io.github.kdy05.physicalFighters.ability

import io.github.kdy05.physicalFighters.ability.impl.*
import io.github.kdy05.physicalFighters.command.CommandInterface
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object AbilityRegistry {

    private val typesByName = LinkedHashMap<String, AbilityType>()
    private val typeList = mutableListOf<AbilityType>()
    private val abilitiesByPlayer = mutableMapOf<UUID, MutableList<Ability>>()
    private val commandHandlers = mutableListOf<CommandInterface>()

    private val PROTOTYPE_UUID = UUID(0, 0)

    private fun register(factory: (UUID) -> Ability) {
        val spec = factory(PROTOTYPE_UUID).spec
        val type = AbilityType(spec, factory)
        typesByName[type.name] = type
        typeList.add(type)
    }

    // --- 타입 카탈로그 API ---

    @JvmStatic
    fun getAllTypes(): List<AbilityType> = typeList.toList()

    @JvmStatic
    fun getType(name: String): AbilityType? = typesByName[name]

    @JvmStatic
    fun getTypeCount(): Int = typeList.size

    // --- 인스턴스 라이프사이클 ---

    @JvmStatic
    @JvmOverloads
    fun createAndActivate(typeName: String, player: Player, textout: Boolean = true): Ability? {
        val type = typesByName[typeName] ?: return null
        val instance = type.createInstance(player)
        addToIndex(instance, player)
        activate(instance, textout)
        return instance
    }

    @JvmStatic
    @JvmOverloads
    fun activate(ability: Ability, textout: Boolean = true) {
        ability.activate(textout)
        if (ability is CommandInterface) commandHandlers.add(ability)
    }

    @JvmStatic
    @JvmOverloads
    fun deactivate(ability: Ability, textout: Boolean = true) {
        if (ability is CommandInterface) commandHandlers.remove(ability)
        ability.deactivate(textout)
        removeFromIndex(ability)
    }

    @JvmStatic
    fun deactivateAll(player: Player) {
        val list = abilitiesByPlayer.remove(player.uniqueId) ?: return
        for (ability in list) {
            if (ability is CommandInterface) commandHandlers.remove(ability)
            ability.deactivate(true)
        }
    }

    @JvmStatic
    fun deactivateAll() {
        for (list in abilitiesByPlayer.values) {
            for (ability in list) {
                ability.deactivate(false)
            }
        }
        abilitiesByPlayer.clear()
        commandHandlers.clear()
    }

    /** 선택 단계에서 인스턴스를 등록 (activate 하지 않음) */
    @JvmStatic
    fun addActive(ability: Ability) {
        val player = ability.player ?: return
        addToIndex(ability, player)
    }

    @JvmStatic
    fun dispatchCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        return commandHandlers.any { it.onCommandEvent(sender, command, label, args) }
    }

    // --- 조회 API ---

    @JvmStatic
    fun findAbility(player: Player): Ability? {
        return abilitiesByPlayer[player.uniqueId]?.firstOrNull()
    }

    @JvmStatic
    fun findAbilities(player: Player): List<Ability> {
        return abilitiesByPlayer[player.uniqueId]?.toList() ?: emptyList()
    }

    @JvmStatic
    fun findByType(typeName: String, owner: Player): Ability? {
        val list = abilitiesByPlayer[owner.uniqueId] ?: return null
        return list.firstOrNull { it.abilityName == typeName }
    }

    @JvmStatic
    fun findPrimaryAbility(player: Player): Ability? {
        val list = abilitiesByPlayer[player.uniqueId] ?: return null
        return list.firstOrNull { it.isInfoPrimary }
    }

    @JvmStatic
    fun getActiveAbilities(): List<Ability> {
        return abilitiesByPlayer.values.flatten()
    }

    // --- 인덱스 관리 ---

    private fun addToIndex(ability: Ability, player: Player) {
        abilitiesByPlayer.getOrPut(player.uniqueId) { mutableListOf() }.add(ability)
    }

    private fun removeFromIndex(ability: Ability) {
        val player = ability.player ?: return
        val list = abilitiesByPlayer[player.uniqueId] ?: return
        list.remove(ability)
        if (list.isEmpty()) {
            abilitiesByPlayer.remove(player.uniqueId)
        }
    }

    // --- 정적 초기화 ---

    init {
        register { CP9(it) }                 // CP9
        // ㄱ
        register { Gaara(it) }               // 가아라
        register { Enel(it) }                // 갓 에넬
        register { Fish(it) }                // 강태공
        register { Berserker(it) }           // 광전사
        register { Shadow(it) }              // 그림자
        register { Gladiator(it) }           // 글레디에이터
        register { MachineGun(it) }          // 기관총
        // ㄴ
        // ㄷ
        register { Demigod(it) }             // 데미갓
        register { PoisonArrow(it) }         // 독화살
        // ㄹ
        register { Roclee(it) }              // 록리
        register { Luffy(it) }               // 루피
        // ㅁ
        register { Multishot(it) }           // 멀티샷
        register { Medic(it) }               // 메딕
        register { Guard(it) }               // 목둔
        register { Mirroring(it) }           // 미러링
        // ㅂ
        register { Cuma(it) }                // 바솔로뮤 쿠마
        register { ReverseAlchemy(it) }      // 반연금술
        register { Lockdown(it) }            // 봉인
        register { Booster(it) }             // 부스터
        register { Phoenix(it) }             // 불사조
        register { Boom(it) }                // 붐포인트
        // ㅅ
        register { Sasuke(it) }              // 사스케
        register { SuperFan(it) }            // 선풍기
        register { Teleporter(it) }          // 소환술사
        register { ShockWave(it) }           // 쇼크웨이브
        register { Trash(it) }               // 쓰레기
        // ㅇ
        register { Aokizi(it) }              // 아오키지
//        register { Archer(it) }              // 아쳐
        register { Akainu(it) }              // 아카이누
        register { Apollon(it) }             // 아폴론
        register { Ace(it) }                 // 에이스
        register { Aegis(it) }               // 이지스
        register { Explosion(it) }           // 익스플로젼
        // ㅈ
        register { Zoro(it) }                // 조로
        register { Zombie(it) }              // 좀비
        register { FallArrow(it) }           // 중력화살
        // ㅊ
        register { Ckyomi(it) }              // 츠쿠요미
        // ㅋ
        register { Kaiji(it) }               // 카이지
//        register { Crocodile(it) }           // 크로커다일
        register { Clocking(it) }            // 클로킹
        register { Kimimaro(it) }            // 키미마로
        register { Kijaru(it) }              // 키자루
        // ㅌ
        register { Temari(it) }              // 테마리
        register { Thor(it) }                // 토르
        register { Tranceball(it) }          // 트랜스볼
        // ㅍ
        register { Haki(it) }                // 패기
        register { Poseidon(it) }            // 포세이돈
        register { Poison(it) }              // 포이즌
        register { Killtolevelup(it) }       // 폭주
        register { Fly(it) }                 // 플라이
        // ㅎ
        register { NuclearPunch(it) }        // 핵펀치
        register { Hulk(it) }                // 헐크
        register { Assimilation(it) }        // 흡수
        register { Flower(it) }              // 흡혈초
    }
}
