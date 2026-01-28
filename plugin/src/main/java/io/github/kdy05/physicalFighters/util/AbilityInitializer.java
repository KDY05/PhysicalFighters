package io.github.kdy05.physicalFighters.util;

import java.util.ArrayList;

import io.github.kdy05.physicalFighters.ability.*;
import io.github.kdy05.physicalFighters.core.Ability;

@SuppressWarnings("unused")
public final class AbilityInitializer {
    public static final ArrayList<Ability> AbilityList = new ArrayList<>();

    private static <T extends Ability> T register(T ability) {
        AbilityList.add(ability);
        return ability;
    }

    public static final CP9 cp9 = register(new CP9()); // CP9
    // ㄱ
    public static final Gaara gaara = register(new Gaara()); // 가아라
    public static final Enel enel = register(new Enel()); // 갓 에넬
    public static final Fish fish = register(new Fish()); // 강태공
    public static final Berserker berserker = register(new Berserker()); // 광전사
    public static final Shadow shadow = register(new Shadow()); // 그림자
    public static final Gladiator gladiator = register(new Gladiator()); // 글레디에이터
    public static final MachineGun machinegun = register(new MachineGun()); // 기관총
    // ㄴ
    // ㄷ
    public static final Demigod demigod = register(new Demigod()); // 데미갓
    public static final PoisonArrow poisonArrow = register(new PoisonArrow()); // 독화살
    // ㄹ
    public static final Roclee roclee = register(new Roclee()); // 록리
    public static final Luffy luffy = register(new Luffy()); // 루피
    // ㅁ
    public static final Multishot multishot = register(new Multishot()); // 멀티샷
    public static final Medic medic = register(new Medic()); // 메딕
    public static final Guard guard = register(new Guard()); // 목둔
    public static final Mirroring mirroring = register(new Mirroring()); // 미러링
    // ㅂ
    public static final Cuma cuma = register(new Cuma()); // 바솔로뮤 쿠마
    public static final ReverseAlchemy reversealchemy = register(new ReverseAlchemy()); // 반연금술
    public static final Lockdown lockdown = register(new Lockdown()); // 봉인
    public static final Booster booster = register(new Booster()); // 부스터
    public static final Phoenix phoenix = register(new Phoenix()); // 불사조
    public static final Boom boom = register(new Boom()); // 붐포인트
    // ㅅ
    public static final Sasuke sasuke = register(new Sasuke()); // 사스케
    public static final SuperFan superFan = register(new SuperFan()); // 선풍기
    public static final Teleporter teleporter = register(new Teleporter()); // 소환술사
    public static final ShockWave shockwave = register(new ShockWave()); // 쇼크웨이브
    public static final Trash trash = register(new Trash()); // 쓰레기
    // ㅇ
    public static final Aokizi aokizi = register(new Aokizi()); // 아오키지
    public static final Archer archer = register(new Archer()); // 아쳐
    public static final Akainu akainu = register(new Akainu()); // 아카이누
    public static final Apollon apolln = register(new Apollon()); // 아폴론
    public static final Ace ace = register(new Ace()); // 에이스
    public static final Aegis aegis = register(new Aegis()); // 이지스
    public static final Explosion explosion = register(new Explosion()); // 익스플로젼
    // ㅈ
    public static final Zoro zoro = register(new Zoro()); // 조로
    public static final Zombie zombie = register(new Zombie()); // 좀비
    public static final FallArrow fallArrow = register(new FallArrow()); // 중력화살
    // ㅊ
    public static final Ckyomi ckyomi = register(new Ckyomi()); // 츠쿠요미
    // ㅋ
    public static final Kaiji kaiji = register(new Kaiji()); // 카이지
    public static final Crocodile crocodile = register(new Crocodile()); // 크로커다일
    public static final Clocking clocking = register(new Clocking()); // 클로킹
    public static final Kimimaro kimimaro = register(new Kimimaro()); // 키미마로
    public static final Kijaru kijaru = register(new Kijaru()); // 키자루
    // ㅌ
    public static final Temari temari = register(new Temari()); // 테마리
    public static final Thor thor = register(new Thor()); // 토르
    public static final Tranceball tranceball = register(new Tranceball()); // 트랜스볼
    // ㅍ
    public static final Haki haki = register(new Haki()); // 패기
    public static final Poseidon poseidon = register(new Poseidon()); // 포세이돈
    public static final Poison poison = register(new Poison()); // 포이즌
    public static final Killtolevelup killtolevelup = register(new Killtolevelup()); // 폭주
    public static final Fly fly = register(new Fly()); // 플라이
    // ㅎ
    public static final NuclearPunch nuclearpunch = register(new NuclearPunch()); // 핵펀치
    public static final Hulk hulk = register(new Hulk()); // 헐크
    public static final Assimilation assimilation = register(new Assimilation()); // 흡수
    public static final Flower flower = register(new Flower()); // 흡혈초
}
