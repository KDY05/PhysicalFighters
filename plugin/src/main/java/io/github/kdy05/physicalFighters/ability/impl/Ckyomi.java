package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.EventData;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class Ckyomi extends Ability {
    public Ckyomi(UUID playerUuid) {
        super(AbilitySpec.builder("츠쿠요미", Type.PassiveAutoMatic, Rank.A)
                .guide("상대를 공격하면 상대에게 5초간 혼란 효과와 디버프를 줍니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamageByEntity(new EventData(this));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (!InvincibilityManager.isDamageGuard() && isOwner(event0.getDamager())
                && event0.getEntity() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) event0.getEntity();
        entity.addPotionEffect(PotionEffectFactory.createNausea(100, 0));
        entity.addPotionEffect(PotionEffectFactory.createWeakness(100, 0));
        entity.addPotionEffect(PotionEffectFactory.createBlindness(100, 0));
    }
}
