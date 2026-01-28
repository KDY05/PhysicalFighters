package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.module.InvincibilityManager;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.AbilitySpec;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;

public class Ckyomi extends Ability {
    public Ckyomi() {
        super(AbilitySpec.builder("츠쿠요미", Type.Passive_AutoMatic, Rank.A)
                .guide("상대를 공격하면 상대에게 5초간 혼란 효과와 디버프를 줍니다.")
                .build());
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (!InvincibilityManager.isDamageGuard() && isOwner(event0.getDamager())
                && event0.getEntity() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) event0.getEntity();
        entity.addPotionEffect(PotionEffectFactory.createNausea(100, 0));
        entity.addPotionEffect(PotionEffectFactory.createWeakness(100, 0));
        entity.addPotionEffect(PotionEffectFactory.createBlindness(100, 0));
    }
}
