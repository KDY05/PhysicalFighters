package io.github.kdy05.physicalFighters.ability;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Function;

/**
 * 능력의 메타데이터(타입 카탈로그)와 팩토리를 담는 불변 객체.
 * 플러그인 로드 시 한 번 생성되며 게임 중 변경되지 않는다.
 */
public final class AbilityType {
    private final String name;
    private final Ability.Rank rank;
    private final Ability.Type type;
    private final String[] guide;
    private final int cooldown;
    private final int duration;
    private final int minimumPlayers;
    private final boolean deathExempt;
    private final boolean infoPrimary;
    private final Function<UUID, Ability> factory;

    AbilityType(Ability prototype, Function<UUID, Ability> factory) {
        this.name = prototype.getAbilityName();
        this.rank = prototype.getRank();
        this.type = prototype.getAbilityType();
        this.guide = prototype.getGuide().clone();
        this.cooldown = prototype.getCoolDown();
        this.duration = prototype.getDuration();
        this.minimumPlayers = prototype.getMinimumPlayers();
        this.deathExempt = prototype.isDeathExempt();
        this.infoPrimary = prototype.isInfoPrimary();
        this.factory = factory;
    }

    public Ability createInstance(Player player) {
        return factory.apply(player.getUniqueId());
    }

    public String getName() { return name; }
    public Ability.Rank getRank() { return rank; }
    public Ability.Type getType() { return type; }
    public String[] getGuide() { return guide.clone(); }
    public int getCooldown() { return cooldown; }
    public int getDuration() { return duration; }
    public int getMinimumPlayers() { return minimumPlayers; }
    public boolean isDeathExempt() { return deathExempt; }
    public boolean isInfoPrimary() { return infoPrimary; }
}
