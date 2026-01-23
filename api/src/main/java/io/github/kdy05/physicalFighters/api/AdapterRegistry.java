package io.github.kdy05.physicalFighters.api;

/**
 * 버전별 어댑터를 관리하는 중앙 레지스트리.
 * 플러그인 초기화 시 register()로 어댑터를 등록하면,
 * 이후 Factory/Utils 클래스에서 사용할 수 있습니다.
 */
public class AdapterRegistry {

    private static AttributeAdapter attributeAdapter;
    private static PotionEffectTypeAdapter potionEffectTypeAdapter;

    /**
     * 어댑터를 등록합니다. 플러그인 onEnable()에서 호출해야 합니다.
     */
    public static void register(AttributeAdapter attribute, PotionEffectTypeAdapter potionEffectType) {
        attributeAdapter = attribute;
        potionEffectTypeAdapter = potionEffectType;
    }

    public static AttributeAdapter attribute() {
        return attributeAdapter;
    }

    public static PotionEffectTypeAdapter potionEffectType() {
        return potionEffectTypeAdapter;
    }

}
