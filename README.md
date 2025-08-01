# PhysicalFighters

[VisualAbility](https://cafe.naver.com/craftproducer/5066)의 가장 유명한 포크인
[PhysicalFighters](https://github.com/outstanding1301/PhysicalFighters)의 최신화 및 개선 버전입니다.

유명한 추억의 능력들이 포함되어 있습니다. (미러링, 반 연금술, 이지스, 글레디에이터, 조로, 카이지, 헐크 등)

초기 버전이라 불안정할 수 있습니다. 버그 제보는 [Github Issues](https://github.com/KDY05/PhysicalFighters/issues)
혹은 디스코드(@kdy05_)로 부탁드립니다.

<a href="https://coff.ee/ararab" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" height="60" width="217"></a>

*커피 한 잔의 응원이 큰 힘이 됩니다!*


## LICENSE

이 프로젝트는 GNU General Public License v3.0(GPL-3.0) 라이선스 하에 배포됩니다.

### 주요 내용
* 자유로운 사용 및 배포 - 소프트웨어를 상업적 목적을 포함하여 자유롭게 사용, 수정, 배포할 수 있습니다.
**단, 배포 시 원작자의 저작권 표시 및 라이선스 고지를 반드시 유지해야 합니다.**
* 소스코드 공개 의무 - 본 소프트웨어를 사용한 프로그램을 배포할 경우, 해당 **소스코드를 반드시 공개**해야 합니다.
* 동일 라이선스 적용 - 본 소프트웨어를 사용한 파생 작업물은 **동일하게 GPL 3.0 라이선스를 적용**해야 합니다.

자세한 내용은 [LICENSE](./LICENSE) 파일을 참조하세요.

## Credit

* PhysicalFighters 제작: [염료(Yeomryo)](https://github.com/outstanding1301)
* 원작(VisualAbility) 제작: [제온(Xeon)](https://cafe.naver.com/craftproducer)

본 프로젝트는 VisualAbility의 라이선스를 준수하며, 염료님의 동의하에 제작되었습니다.


## Requirements

- Spigot/Paper 1.21.8 이상
- Java 21 이상


## Usage

- `/va help`로 모든 사용법을 확인하세요.
- 능력 확인 및 확정 명령어를 제외한 모든 명령어는 `va.operate` 권한을 필요로 합니다. (default: OP)


## Configuration

```yaml
# 사망 시 처리 (0: 아무것도 안함, 1: 관전자 모드, 2: 킥, 3: 밴)
OnKill: 2
# 사망 시 죽인 사람 공개
KillerOutput: true

# 기본 지급 레벨 (0 이상 정수)
SetLev: 60
# 초반 무적 시간 (분 단위, 0 이상 정수)
EarlyInvincibleTime: 10
# 일부 능력 금지 시간 (분 단위, 0 이상 정수)
RestrictionTime: 15
# 시작 시 인벤토리 초기화
ClearInventory: true
# 시작 시 능력 추첨 생략
NoAbilitySetting: false

# 능력 중복 가능
AbilityOverLap: false
```


### TODO (업데이트 예정)

- 개별 능력 활성화 토글링(ability.yml)
- GameManager 역할 분담 리팩토링
