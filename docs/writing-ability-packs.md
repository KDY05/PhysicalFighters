# Writing Ability Packs

An ability pack is a JAR file that the AbilityAPI plugin loads at runtime from its `abilities/` directory. Each pack declares one or more `Ability` classes and registers them via the `AbilityProvider` SPI.

## Contents

- [Project Setup](#project-setup)
- [Ability](#ability)
- [Skill Types](#skill-types)
  - [PassiveSkill](#passiveskill)
  - [ActiveSkill](#activeskill)
  - [ActiveContinueSkill](#activecontinueskill)
  - [StackableActiveSkill](#stackableactiveskill)
- [SkillBase Helpers](#skillbase-helpers)
- [SkillContext](#skillcontext)
- [Registering the Pack](#registering-the-pack)
- [Custom Events](#custom-events)

---

## Project Setup

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.3.21"
}

dependencies {
    compileOnly("io.github.kdy05:ability-api:<version>")         // AbilityAPI public types
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT") // Bukkit API
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")       // Kotlin stdlib (provided by plugin)
}

kotlin {
    jvmToolchain(8)
}
```

The pack JAR must **not** bundle kotlin-stdlib or ability-api — the implementation plugin provides both at runtime.

---

## Ability

An `Ability` is a per-player container that holds one or more `Skill` instances. Each time the implementation assigns an ability to a player, it constructs a fresh instance via reflection.

### Required structure

```kotlin
import io.github.kdy05.abilityAPI.ability.Ability
import io.github.kdy05.abilityAPI.ability.AbilityMeta
import io.github.kdy05.abilityAPI.rank.Rank
import io.github.kdy05.abilityAPI.skill.Skill
import io.github.kdy05.abilityAPI.skill.SkillContext
import org.bukkit.entity.Player

@AbilityMeta(
    name = "내 능력",          // Registry key uses this name (spaces → underscores)
    rank = Rank.B,
    guide = ["우클릭으로 발동합니다.", "쿨다운: 5초"],
    minimumPlayers = 0,        // 이 능력이 배분될 최소 인원 수 (0 = 제한 없음)
    isDeathExempt = false,     // true면 게임 플러그인이 사망 처리에서 제외할 수 있음
    isInfoPrimary = true,      // true면 getPrimaryAbility()가 이 능력을 반환
)
class MyAbility(owner: Player, context: SkillContext) : Ability(owner, context) {

    // 스킬은 익명 객체 또는 inner class로 선언
    private val mySkill = object : PassiveSkill(owner, context) {
        override fun register() { /* 이벤트 구독 */ }
    }

    // 반드시 구현 — 이 능력이 소유하는 모든 스킬을 반환
    override fun skills(): List<Skill> = listOf(mySkill)
}
```

**Constraints:**
- `@AbilityMeta` 어노테이션이 없으면 등록 시 예외 발생.
- 생성자 시그니처는 정확히 `(Player, SkillContext)` 이어야 함 — 파라미터 수/순서 불일치 시 예외 발생.
- `Rank` 값: `F`, `C`, `B`, `A`, `S`, `SS`, `SSS`, `GOD`

---

## Skill Types

모든 스킬 타입은 `SkillBase`를 상속합니다. 공통 필드:

| 필드 / 메서드 | 설명 |
|---|---|
| `owner: Player` | 이 스킬을 소유한 플레이어 |
| `context: SkillContext` | 이벤트·타이머 접근 인터페이스 |
| `register()` | 스킬 시작 시 한 번 호출 — 이벤트 구독·초기화 수행 |
| `onStop()` | 스킬 정지 시 호출 — `super.onStop()` 호출 필수 |
| `unsubscribeAll()` | `register()`에서 `on()`으로 등록한 모든 구독 해제 |

### PassiveSkill

항상 활성 상태인 이벤트 전용 스킬. 쿨다운·타이머 없음.

```kotlin
import io.github.kdy05.abilityAPI.skill.PassiveSkill
import org.bukkit.event.entity.EntityDamageByEntityEvent

private val myPassive = object : PassiveSkill(owner, context) {

    override fun register() {
        onEntityDamage { e ->
            // owner가 다른 엔티티를 공격할 때
            e.damage *= 1.5
        }
    }

    override fun onStop() {
        // 필요한 정리 작업 (예: 포션 효과 제거)
        super.onStop() // 반드시 호출
    }
}
```

### ActiveSkill

한 번 발동 후 쿨다운에 진입하는 스킬.

**상태 머신:** `READY → (activate()) → COOLDOWN → (쿨다운 완료) → READY`

```kotlin
import io.github.kdy05.abilityAPI.skill.ActiveSkill
import org.bukkit.Material

private val myActive = object : ActiveSkill(owner, context) {

    override val cooldownTicks: Long = 100L  // 5초 (20 ticks = 1초)

    override fun register() {
        onRightClick { e ->
            if (e.player.inventory.itemInMainHand.type == Material.COMPASS) {
                activate()  // READY 상태일 때만 발동
            }
        }
    }

    // READY 상태에서 activate() 호출 시 — 실제 효과 구현
    override fun onActivate() {
        owner.sendMessage("스킬 발동!")
    }

    // COOLDOWN 상태에서 activate() 시도 시
    override fun onCooldownAttempt(remainingSeconds: Int) {
        owner.sendMessage("쿨다운 중: ${remainingSeconds}초 남음")
    }

    // 쿨다운 중 매 초 호출
    override fun onCooldownRunning(remainingSeconds: Int) {}

    // 쿨다운 완료 시
    override fun onCooldownEnd() {}

    // context.isSilenced(owner) == true 상태에서 activate() 시도 시
    override fun onSilenceAttempt() {}
}
```

`ActiveSkill`은 `CooldownSkillBase`를 상속하므로 쿨타임 제어 메서드를 사용할 수 있습니다.

```kotlin
// 쿨타임을 즉시 0으로 초기화 — onCooldownEnd()가 호출되고 READY 상태로 전환
mySkill.resetCooldown()

// 남은 쿨타임(틱 단위) 조회 — 1초 단위 근사값 (최대 20틱 오차)
val ticks: Long = mySkill.getRemainingTicks()
```

### ActiveContinueSkill

발동 → 지속 → 쿨다운 순서로 진행하는 스킬.

**상태 머신:** `READY → (activate()) → ACTIVE → (durationTicks 경과) → COOLDOWN → READY`

```kotlin
import io.github.kdy05.abilityAPI.skill.ActiveContinueSkill
import org.bukkit.Material
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

private val myContinue = object : ActiveContinueSkill(owner, context) {

    override val durationTicks: Long = 60L   // 3초 동안 활성
    override val cooldownTicks: Long = 200L  // 10초 쿨다운

    override fun register() {
        onRightClick { e ->
            if (e.player.inventory.itemInMainHand.type == Material.GLASS_BOTTLE) {
                activate()
            }
        }
    }

    // ACTIVE 상태 진입 시
    override fun onActivate() {
        owner.addPotionEffect(PotionEffect(PotionEffectType.SPEED, durationTicks.toInt() + 5, 1))
    }

    // ACTIVE → COOLDOWN 전환 시 (효과 정리)
    override fun onDeactivate() {
        owner.removePotionEffect(PotionEffectType.SPEED)
    }

    // ACTIVE 상태에서 activate() 시도 시
    override fun onActiveAttempt(remainingSeconds: Int) {}

    // ACTIVE 중 매 초 호출
    override fun onActiveRunning(remainingSeconds: Int) {}

    // ACTIVE 종료 시 (onDeactivate() 직후)
    override fun onActiveEnd() {}

    // COOLDOWN 상태에서 activate() 시도 시
    override fun onCooldownAttempt(remainingSeconds: Int) {}

    // COOLDOWN 중 매 초 호출
    override fun onCooldownRunning(remainingSeconds: Int) {}

    // COOLDOWN 완료 시
    override fun onCooldownEnd() {}

    // 침묵 상태에서 activate() 시도 시
    override fun onSilenceAttempt() {}
}
```

### StackableActiveSkill

스택을 쌓아 사용하는 스킬. 스택이 0이면 발동 불가. 스택 소모 후 `cooldownTicks`마다 1스택씩 충전.

**동작:** 시작 시 `stacks = maxStacks`. 발동 시 `stacks--`. `stacks`가 최대에서 0으로 된 시점부터 충전 타이머 시작.

```kotlin
import io.github.kdy05.abilityAPI.skill.StackableActiveSkill
import org.bukkit.Material

private val myStackable = object : StackableActiveSkill(owner, context) {

    override val maxStacks: Int = 3
    override val cooldownTicks: Long = 100L  // 스택당 충전 시간 (5초)

    override fun register() {
        onRightClick { e ->
            if (e.player.inventory.itemInMainHand.type == Material.BLAZE_ROD) {
                activate()
            }
        }
    }

    // 스택 소모 시 — stacks는 이미 감소된 상태
    override fun onActivate() {
        owner.sendMessage("스택 사용! 남은 스택: $stacks/$maxStacks")
    }

    // stacks == 0 상태에서 activate() 시도 시
    override fun onStackEmptyAttempt() {
        owner.sendMessage("스택 없음, 충전 중...")
    }

    // 스택 1개 충전 완료 시
    override fun onStackGained(currentStacks: Int) {
        owner.sendMessage("스택 충전: $currentStacks/$maxStacks")
    }

    // 충전 중 매 초 호출
    override fun onCooldownRunning(remainingSeconds: Int) {}

    // 침묵 상태에서 activate() 시도 시
    override fun onSilenceAttempt() {}
}
```

---

## SkillBase Helpers

`SkillBase`가 제공하는 편의 메서드. `register()` 안에서 호출.

```kotlin
// PlayerInteractEvent 중 좌클릭만 필터링 (owner 플레이어만)
onLeftClick { event: PlayerInteractEvent -> }

// PlayerInteractEvent 중 우클릭만 필터링 (owner 플레이어만)
onRightClick { event: PlayerInteractEvent -> }

// EntityDamageByEntityEvent 중 damager == owner인 것만
onEntityDamage { event: EntityDamageByEntityEvent -> }

// EntityDamageByEntityEvent 중 entity == owner인 것만
onEntityDamaged { event: EntityDamageByEntityEvent -> }

// 위 헬퍼로 커버되지 않는 이벤트는 on()으로 직접 구독
on(PlayerMoveEvent::class) { event -> }
```

**주의:** `EventBus`는 구독한 클래스와 **정확히 일치하는** 이벤트만 전달합니다. `EntityDamageEvent`를 구독해도 `EntityDamageByEntityEvent`는 수신되지 않습니다. 서브클래스를 직접 구독하세요.

---

## SkillContext

`SkillContext`는 스킬에게 주입되는 인터페이스로, 이벤트 구독·타이머·커스텀 이벤트 발행을 담당합니다.

```kotlin
// 이벤트 구독 — 반환값(token)으로 개별 해제 가능
val token = context.subscribe(SomeEvent::class) { event -> }
context.unsubscribe(token)

// 지연 실행 (delayTicks 후 1회)
val token = context.scheduleOnce(40L) { /* 2초 후 실행 */ }
context.cancelSchedule(token)

// 반복 실행 (periodTicks마다)
val token = context.scheduleRepeat(20L) { /* 1초마다 실행 */ }
context.cancelSchedule(token)

// 커스텀 Bukkit 이벤트 발행
context.callEvent(MyCustomEvent(owner))

// 침묵 여부 확인 (침묵 중이면 스킬 발동을 막는 것이 관례)
context.isSilenced(owner)
```

---

## Registering the Pack

`AbilityProvider` 구현체를 `META-INF/services`에 등록하면 AbilityAPI 플러그인이 ServiceLoader로 자동 로드합니다.

### 1. AbilityProvider 구현

```kotlin
// src/main/kotlin/com/example/mypack/MyAbilityProvider.kt
import io.github.kdy05.abilityAPI.AbilityProvider
import io.github.kdy05.abilityAPI.ability.Ability

class MyAbilityProvider : AbilityProvider {

    // 팩을 식별하는 고유 네임스페이스 (다른 팩과 충돌 방지)
    override fun namespace(): String = "mypack"

    // 이 팩이 제공하는 모든 Ability 클래스 목록
    override fun provide(): List<Class<out Ability>> = listOf(
        MyAbility::class.java,
        AnotherAbility::class.java,
    )
}
```

> **Java 호환성:** `provide()`가 `Class<out Ability>`를 반환하므로 Java에서도 자연스럽게 구현할 수 있습니다.
>
> ```java
> public List<Class<? extends Ability>> provide() {
>     return List.of(MyAbility.class, AnotherAbility.class);
> }
> ```

### 2. ServiceLoader 등록

`src/main/resources/META-INF/services/io.github.kdy05.abilityAPI.AbilityProvider` 파일 생성:

```
com.example.mypack.MyAbilityProvider
```

여러 `AbilityProvider` 구현체가 있다면 한 줄씩 추가합니다.

### 3. 배포

빌드된 JAR를 서버의 `plugins/AbilityAPI/abilities/` 디렉토리에 복사한 뒤 `/abilityapi reload`를 실행합니다.

**Registry key 형식:** `namespace:이름` (이름의 공백은 `_`로 변환). 예: `mypack:내_능력`

---

## Custom Events

AbilityAPI가 발행하는 Bukkit 이벤트. 게임 플러그인 또는 다른 팩에서 `@EventHandler`로 수신 가능.

### AbilityGiveEvent

능력 인스턴스가 플레이어에게 부여되어 `start()`가 호출된 직후 발행.

```kotlin
@EventHandler
fun onAbilityGive(e: AbilityGiveEvent) {
    val player: Player = e.player
    val ability: Ability = e.ability
}
```

### AbilityRemoveEvent

능력 인스턴스가 제거되어 `stop()`이 호출된 직후 발행.

```kotlin
@EventHandler
fun onAbilityRemove(e: AbilityRemoveEvent) {
    val player: Player = e.player
    val ability: Ability = e.ability
}
```

### SkillActivateEvent

`ActiveSkill`, `ActiveContinueSkill`, `StackableActiveSkill`의 `activate()`가 실제로 발동되기 직전 발행. **취소 가능** — `isCancelled = true`로 설정하면 `onActivate()`가 호출되지 않음.

```kotlin
@EventHandler
fun onSkillActivate(e: SkillActivateEvent) {
    if (someCondition) e.isCancelled = true  // 발동 차단
    val player: Player = e.player
    val skill: Skill = e.skill
}
```
