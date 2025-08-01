package io.github.kdy05.physicalFighters.utils;

import java.util.ArrayList;

import io.github.kdy05.physicalFighters.ability.*;
import io.github.kdy05.physicalFighters.core.Ability;

@SuppressWarnings("unused")
public class AbilityInitializer {
    public static final ArrayList<Ability> AbilityList = new ArrayList<>();
    public static final CP9 cp9 = new CP9(); // CP9
    // ㄱ
    public static final Gaara gaara = new Gaara(); // 가아라
    public static final Enel enel = new Enel(); // 갓 에넬
    public static final Fish fish = new Fish(); // 강태공
//    public static final Anorexia anorexia = new Anorexia(); // 거식증
//    public static final Genji Genji = new Genji(); // 겐지
//    public static final gongban gongban = new gongban(); // 공격반사
    public static final Berserker berserker = new Berserker(); // 광전사
    public static final Shadow shadow = new Shadow(); // 그림자
    public static final Gladiator gladiator = new Gladiator(); // 글레디에이터
//    public static final ExplosionPa Flamethrower = new ExplosionPa(); // 기공포
    public static final MachineGun machinegun = new MachineGun(); // 기관총
//    public static final Feather feather = new Feather(); // 깃털
    // ㄴ
//    public static final Sunbi Sunbi = new Sunbi(); // 나그네
//    public static final Nasus Nasus = new Nasus(); // 나서스
//    public static final Fallingarrow fallingarrow = new Fallingarrow(); // 낙하화살
//    public static final Ninja Ninja = new Ninja(); // 닌자
    // ㄷ
    public static final Demigod demigod = new Demigod(); // 데미갓
    public static final PoisonArrow poisonArrow = new PoisonArrow(); // 독화살
    // ㄹ
    public static final Roclee roclee = new Roclee(); // 록리
    public static final Luffy luffy = new Luffy(); // 루피
    // ㅁ
    public static final Multishot multishot = new Multishot(); // 멀티샷
    public static final Medic medic = new Medic(); // 메딕
    public static final Guard guard = new Guard(); // 목둔
//    public static final Nonuck nonuck = new Nonuck(); // 무통증
    public static final Minato minato = new Minato(); // 미나토
    public static final Mirroring mirroring = new Mirroring(); // 미러링
    // ㅂ
    public static final Cuma cuma = new Cuma(); // 바솔로뮤 쿠마
    public static final ReverseAlchemy reversealchemy = new ReverseAlchemy(); // 반연금술
    public static final Lockdown lockdown = new Lockdown(); // 봉인
//    public static final Trap trap = new Trap(); // 부비트랩
    public static final Booster booster = new Booster(); // 부스터
    public static final Phoenix phoenix = new Phoenix(); // 불사조
    public static final Boom boom = new Boom(); // 붐포인트
//    public static final Blind blind = new Blind(); // 블라인드
//    public static final Blaze blaze = new Blaze(); // 블레이즈
    public static final Blitzcrank blitzcrank = new Blitzcrank(); // 블리츠크랭크
//    public static final Bishop bishop = new Bishop(); // 비숍
    // ㅅ
    public static final Sasuke sasuke = new Sasuke(); // 사스케
    public static final SuperFan superFan = new SuperFan(); // 선풍기
    public static final Teleporter teleporter = new Teleporter(); // 소환술사
    public static final ShockWave shockwave = new ShockWave(); // 쇼크웨이브
//    public static final Slower slower = new Slower(); // 슬로워
//    public static final Time2 time2 = new Time2(); // 시간을 지배하는 자
//    public static final Thunder Thunder = new Thunder(); // 썬더볼트
    public static final Trash trash = new Trash(); // 쓰레기
    // ㅇ
//    public static final Ahri Ahri = new Ahri(); // 아리
//    public static final Amateras amateras = new Amateras(); // 아마테라스
    public static final Aokizi aokizi = new Aokizi(); // 아오키지
//    public static final Issac issac = new Issac(); // 아이작
    public static final Archer archer = new Archer(); // 아쳐
    public static final Akainu akainu = new Akainu(); // 아카이누
    public static final Apollon apolln = new Apollon(); // 아폴론
//    public static final Devil Devil = new Devil(); // 악마
//    public static final Pressure Pressure = new Pressure(); // 압력
//    public static final Assassin assassin = new Assassin(); // 어쌔신
//    public static final Uppercut Uppercut = new Uppercut(); // 어퍼컷
    public static final Ace ace = new Ace(); // 에이스
//    public static final Poksi poksi = new Poksi(); // 이슈타르의 링
    public static final Aegis aegis = new Aegis(); // 이지스
    public static final Explosion explosion = new Explosion(); // 익스플로젼
//    public static final Infighter Infighter = new Infighter(); // 인파이터
    // ㅈ
//    public static final Magnet Magnet = new Magnet(); // 자석
//    public static final Jumper jumper = new Jumper(); // 점퍼
    public static final Zoro zoro = new Zoro(); // 조로
    public static final Zombie zombie = new Zombie(); // 좀비
//    public static final GravityBoots gravityBoots = new GravityBoots(); // 중력장화
    public static final FallArrow fallArrow = new FallArrow(); // 중력화살
    // ㅊ
//    public static final Angel Angel = new Angel(); // 천사
    public static final Ckyomi ckyomi = new Ckyomi(); // 츠쿠요미
    // ㅋ
    public static final Kaiji kaiji = new Kaiji(); // 카이지
    public static final Crocodile crocodile = new Crocodile(); // 크로커다일
    public static final Clocking clocking = new Clocking(); // 클로킹
    public static final Kimimaro kimimaro = new Kimimaro(); // 키미마로
    public static final Kijaru kijaru = new Kijaru(); // 키자루
    // ㅌ
//    public static final Time time = new Time(); // 타임
//    public static final Fittingroom fittingroom = new Fittingroom(); // 탈의실
    public static final Temari temari = new Temari(); // 테마리
    public static final Thor thor = new Thor(); // 토르
    public static final Tranceball tranceball = new Tranceball(); // 트랜스볼
//    public static final Tracer Tracer = new Tracer(); // 트레이서
//    public static final TwistedFate TwistedFate = new TwistedFate(); // 트위스티트 페이트
    // ㅍ
    public static final Fireball fireball = new Fireball(); // 파이어볼
//    public static final Paladin Paladin = new Paladin(); // 팔라딘
    public static final Haki haki = new Haki(); // 패기
    public static final Poseidon poseidon = new Poseidon(); // 포세이돈
    public static final Poison poison = new Poison(); // 포이즌
    public static final Killtolevelup killtolevelup = new Killtolevelup(); // 폭주
//    public static final ExplosionGlove ExplosionGlove = new ExplosionGlove(); // 폭파장갑
    public static final Fly fly = new Fly(); // 플라이
    // ㅎ
    public static final NuclearPunch nuclearpunch = new NuclearPunch(); // 핵펀치
    public static final Hulk hulk = new Hulk(); // 헐크
    public static final Assimilation assimilation = new Assimilation(); // 흡수
    public static final Flower flower = new Flower(); // 흡혈초
}
