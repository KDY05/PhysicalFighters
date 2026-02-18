package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;
import io.github.kdy05.physicalFighters.util.SoundUtils;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.UUID;

public final class Zombie extends Ability {
    public Zombie(UUID playerUuid) {
        super(AbilitySpec.builder("좀비", Type.PassiveAutoMatic, Rank.B)
                .guide("모든 대미지의 반을 흡수합니다. 단, 화염 대미지를 8배로 받습니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamage(new EventData(this));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        EntityDamageEvent event0 = (EntityDamageEvent) event;
        if (!isOwner(event0.getEntity())) return -1;

        // 화염 대미지 → 8배
        if (event0.getCause() == DamageCause.LAVA || event0.getCause() == DamageCause.FIRE
                || event0.getCause() == DamageCause.FIRE_TICK)
            return 0;

        // 그 외 모든 대미지 → 0.5배
        return 1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        EntityDamageEvent event0 = (EntityDamageEvent) event;
        if (CustomData == 0) {
            event0.setDamage(event0.getDamage() * 8);
        } else {
            SoundUtils.playShieldSound(getPlayer());
            event0.setDamage(event0.getDamage() * 0.5);
        }
    }
}
