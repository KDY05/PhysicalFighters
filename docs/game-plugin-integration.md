# Game Plugin Integration

게임 플러그인에서 AbilityAPI를 통해 능력을 등록·배분·제어하는 방법을 설명합니다.

## Contents

- [Project Setup](#project-setup)
- [AbilityAPI.service 접근](#abilityapiservice-접근)
- [능력 등록](#능력-등록)
- [능력 목록 조회](#능력-목록-조회)
- [능력 배분 (게임 시작)](#능력-배분-게임-시작)
- [능력 개별 제어](#능력-개별-제어)
- [플레이어 조회](#플레이어-조회)
- [쿨타임 제어](#쿨타임-제어)
- [damageGuard](#damageguard)
- [silencePlayer](#silenceplayer)
- [이벤트 수신](#이벤트-수신)
- [전체 흐름 예시](#전체-흐름-예시)

---

## Project Setup

### build.gradle.kts

```kotlin
dependencies {
    compileOnly("io.github.kdy05:ability-api:<version>")
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
```

### plugin.yml

AbilityAPI를 `depend`에 추가해야 로드 순서가 보장됩니다. AbilityAPI는 `POSTWORLD`로 로드되므로 게임 플러그인도 `POSTWORLD` 또는 기본값(`STARTUP`)이면 됩니다.

```yaml
name: MyGame
version: '1.0.0'
main: com.example.mygame.MyGame
api-version: '1.16'
depend: [AbilityAPI]   # 반드시 포함 — AbilityAPI.service 초기화 순서 보장
```

---

## AbilityAPI.service 접근

`AbilityAPI.service`는 AbilityAPI 플러그인이 `onEnable()`을 완료한 뒤에만 유효합니다. `depend: [AbilityAPI]`가 선언된 플러그인의 `onEnable()` 시점에서는 항상 사용 가능합니다.

```kotlin
import io.github.kdy05.abilityAPI.AbilityAPI

class MyGame : JavaPlugin() {
    override fun onEnable() {
        val service = AbilityAPI.service  // depend 보장으로 항상 안전
    }
}
```

`depend` 없이 호출하면 `IllegalStateException("AbilityAPI is not Initialized yet.")` 발생.

---

## 능력 등록

`onEnable()`에서 게임 플러그인이 직접 제공하는 능력 클래스를 등록합니다. 능력 팩(외부 JAR)의 능력은 AbilityAPI가 자동으로 로드하므로 별도 등록 불필요.

```kotlin
import io.github.kdy05.abilityAPI.AbilityAPI
import com.example.mygame.abilities.FireAbility
import com.example.mygame.abilities.IceAbility

override fun onEnable() {
    AbilityAPI.service.registrar.register(
        FireAbility::class.java,
        IceAbility::class.java,
    )
}
```

**특성:**
- `@AbilityMeta` 없는 클래스 등록 시 즉시 예외 발생.
- 동일 이름 중복 등록은 경고 로그 후 무시.
- 게임 플러그인이 등록한 능력은 `/abilityapi reload`로 제거되지 않음 (팩 능력만 재로드).

---

## 능력 목록 조회

`registrar`를 통해 등록된 능력 타입 전체를 조회할 수 있습니다.

```kotlin
val registrar = AbilityAPI.service.registrar

// 등록된 모든 능력 타입
val all: List<Class<out Ability>> = registrar.getAll()

// @AbilityMeta.name 기준 조회 (대소문자 무시)
val type: Class<out Ability>? = registrar.getByName("화염")

// 등록된 능력 수
val count: Int = registrar.getCount()
```

**활용 예시 — 능력 목록 출력:**

```kotlin
registrar.getAll()
    .mapNotNull { it.getAnnotation(AbilityMeta::class.java) }
    .forEach { meta -> player.sendMessage("[${meta.rank}] ${meta.name}") }
```

**활용 예시 — 이름으로 능력 부여:**

```kotlin
fun giveByName(player: Player, name: String) {
    val type = AbilityAPI.service.registrar.getByName(name)
        ?: return player.sendMessage("존재하지 않는 능력입니다.")
    AbilityAPI.service.giveAbility(player, type)
}
```

---

## 능력 배분 (게임 시작)

플레이어 목록을 받아 능력을 균등 배분하는 3단계 흐름입니다.

```
previewAbilities(players)   능력 배정 계획 수립 (인스턴스 생성 안 함)
       ↓ (선택)
reassignAbility(player)     특정 플레이어의 배정 능력 교체
       ↓
distributeAbilities()       계획 확정 + 전원 인스턴스 생성 및 시작
```

### previewAbilities

```kotlin
// 반환값: 각 플레이어에게 배정 예정인 능력 타입 Map (UI 표시 등에 활용)
val preview: Map<Player, Class<out Ability>> =
    AbilityAPI.service.previewAbilities(players)
```

**동작:**
- `list.yml`에서 활성화된 능력 중 `minimumPlayers <= players.size`인 능력만 후보.
- 후보를 플레이어 수만큼 순환 배치 후 셔플 → 균등 분포 보장.
- 예비 풀(능력 종류별 1개)도 함께 생성 → `reassignAbility` 재배정 시 사용.
- 후보 능력이 없으면 빈 Map 반환 (배분 없음).

### reassignAbility

```kotlin
// 반환값: 새로 배정된 능력 타입 (UI 업데이트에 활용)
val newType: Class<out Ability> =
    AbilityAPI.service.reassignAbility(player)
```

**동작:**
- 해당 플레이어의 배정 능력을 예비 풀에서 다른 것으로 교체.
- 1순위: 현재 배정과 다르고 다른 플레이어도 보유하지 않은 것.
- 2순위: 현재 배정과만 다른 것 (모든 능력이 이미 배정된 경우).
- 교체 불가능하면 현재 능력 그대로 반환.
- `previewAbilities()` 없이 호출하면 `IllegalStateException`.

### distributeAbilities

```kotlin
AbilityAPI.service.distributeAbilities()
// 이후 pendingMap, reservePool 초기화됨
```

**동작:**
- `previewAbilities()`가 없었거나 후보가 없었으면 아무 작업 안 함.
- 각 플레이어에게 `giveAbility()` 호출 → 능력 인스턴스 생성 및 `start()`.

---

## 능력 개별 제어

배분 흐름 외에 개별 플레이어에게 직접 능력을 제어합니다.

```kotlin
val service = AbilityAPI.service

// 능력 부여 — 인스턴스 생성 후 즉시 start() (쿨다운 없이 READY 상태로 시작)
service.giveAbility(player, FireAbility::class.java)

// 특정 타입의 능력 제거 — 해당 타입 인스턴스 전부 stop() 후 제거
service.removeAbility(player, FireAbility::class.java)

// 모든 능력 제거 + 재입장 시 복원 데이터도 삭제 (게임 완전 종료 시)
service.clearAbilities(player)
```

**`removeAbility` vs `clearAbilities`:**

| | `removeAbility` | `clearAbilities` |
|---|---|---|
| 대상 | 특정 타입만 | 전체 |
| 재입장 복원 데이터 | 유지 | 삭제 |
| 용도 | 게임 중 능력 교체 | 게임 완전 종료 |

---

## 플레이어 조회

```kotlin
val service = AbilityAPI.service

// 플레이어가 보유한 모든 능력 인스턴스
val abilities: List<Ability> = service.getAbilities(player)

// @AbilityMeta(isInfoPrimary = true)인 첫 번째 능력 (없으면 null)
val primary: Ability? = service.getPrimaryAbility(player)

// 특정 타입 보유 여부
val has: Boolean = service.hasAbility(player, FireAbility::class.java)

// 특정 능력을 보유한 온라인 플레이어 목록
val players: List<Player> = service.getAbilityPlayers(FireAbility::class.java)
```

---

## 쿨타임 제어

### 서비스 레벨 — 전체 초기화

`resetAllCooldowns(player)`는 해당 플레이어의 모든 `CooldownSkillBase` 계열 스킬(`ActiveSkill`, `ActiveContinueSkill`, `StackableActiveSkill`)의 쿨타임을 즉시 초기화합니다.

```kotlin
// 플레이어의 모든 스킬 쿨타임 초기화 (예: 관리자 명령어 /tc <player>)
AbilityAPI.service.resetAllCooldowns(player)
```

각 스킬의 `onCooldownEnd()`가 호출되어 READY 상태로 전환됩니다.

### 스킬 레벨 — 개별 초기화

특정 스킬만 초기화하거나 남은 쿨타임을 조회할 때는 스킬 인스턴스에 직접 접근합니다.

```kotlin
import io.github.kdy05.abilityAPI.skill.CooldownSkillBase

AbilityAPI.service.getAbilities(player)
    .flatMap { it.skills() }
    .filterIsInstance<CooldownSkillBase>()
    .forEach { skill ->
        val remaining = skill.getRemainingTicks()  // 남은 틱 (1초 단위 근사값)
        skill.resetCooldown()                      // 즉시 초기화
    }
```

---

## damageGuard

`damageGuard = true`로 설정하면 `EventBus`가 모든 `EntityDamageEvent` 계열 이벤트 디스패치를 차단합니다. 스킬의 `onEntityDamage` / `onEntityDamaged` 핸들러가 호출되지 않습니다.

```kotlin
// PvP 비활성화 구간 (로비, 게임 종료 후 등)
AbilityAPI.service.damageGuard = true

// PvP 활성화 (게임 시작)
AbilityAPI.service.damageGuard = false
```

**주의:** `damageGuard`는 Bukkit 이벤트 자체를 취소하지 않습니다. 스킬의 핸들러에만 전달을 막는 것이므로, 다른 플러그인의 데미지 이벤트 처리에는 영향을 주지 않습니다.

---

## silencePlayer

침묵 상태의 플레이어는 `ActiveSkill`, `ActiveContinueSkill`, `StackableActiveSkill`의 `activate()` 호출 시 `onSilenceAttempt()`만 호출되고 실제 발동되지 않습니다.

```kotlin
val service = AbilityAPI.service

// durationTicks 동안 침묵 (20 ticks = 1초)
// 이미 침묵 중이면 타이머를 새 값으로 교체 (연장 또는 단축)
service.silencePlayer(player, 100L)  // 5초

// 즉시 침묵 해제
service.unsilencePlayer(player)

// 침묵 여부 확인
val silenced: Boolean = service.isSilenced(player)
```

**용도 예시:** 부활 직후 무적·침묵 구간, 게임 시작 카운트다운, 스턴 효과 구현.

---

## 이벤트 수신

AbilityAPI가 발행하는 이벤트를 `@EventHandler`로 수신할 수 있습니다. 자세한 내용은 [Writing Ability Packs — Custom Events](writing-ability-packs.md#custom-events)를 참고하세요.

```kotlin
class MyGameListener : Listener {

    @EventHandler
    fun onAbilityGive(e: AbilityGiveEvent) {
        // 능력 부여 시 — UI 업데이트, 로그 등
        val meta = e.ability.javaClass.getAnnotation(AbilityMeta::class.java)
        e.player.sendTitle(meta.name, "", 10, 40, 10)
    }

    @EventHandler
    fun onAbilityRemove(e: AbilityRemoveEvent) {
        // 능력 제거 시
    }

    @EventHandler
    fun onSkillActivate(e: SkillActivateEvent) {
        // 스킬 발동 직전 — 취소 가능
        if (isLobbyPhase) e.isCancelled = true
    }
}
```

---

## 전체 흐름 예시

미니게임의 전형적인 게임 사이클 구현 예시입니다.

```kotlin
import io.github.kdy05.abilityAPI.AbilityAPI
import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.event.AbilityGiveEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class MyGame : JavaPlugin(), Listener {

    private val service get() = AbilityAPI.service

    override fun onEnable() {
        service.registrar.register(FireAbility::class.java, IceAbility::class.java)
        server.pluginManager.registerEvents(this, this)
    }

    // 1단계: 능력 미리보기 + 플레이어에게 공지
    fun startVotePhase(players: List<Player>) {
        val preview = service.previewAbilities(players)
        preview.forEach { (player, type) ->
            val meta = type.getAnnotation(AbilityMeta::class.java)
            player.sendMessage("배정 예정 능력: ${meta.name} [${meta.rank}등급]")
        }
    }

    // 2단계: 능력 재배정 요청 처리
    fun onReassignRequest(player: Player) {
        val newType = service.reassignAbility(player)
        val meta = newType.getAnnotation(AbilityMeta::class.java)
        player.sendMessage("새 능력: ${meta.name}")
    }

    // 3단계: 게임 시작 — 능력 확정 및 부여
    fun startGame(players: List<Player>) {
        service.distributeAbilities()
        service.damageGuard = false
    }

    // 4단계: 게임 종료 — 모든 능력 제거
    fun endGame(players: List<Player>) {
        service.damageGuard = true
        players.forEach { service.clearAbilities(it) }
    }

    // 능력 부여 시 타이틀 표시
    @EventHandler
    fun onAbilityGive(e: AbilityGiveEvent) {
        val meta = e.ability.javaClass.getAnnotation(AbilityMeta::class.java)
        e.player.sendTitle("§6${meta.name}", "§7${meta.rank}등급", 10, 60, 10)
    }
}
```
