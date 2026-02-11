package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Sasuke extends Ability {
    public Sasuke() {
        super(AbilitySpec.builder("사스케", Type.Active_Immediately, Rank.S)
                .cooldown(30)
                .guide(Usage.IronAttack + "치도리 - 엄청난 대미지로 감전시킵니다.")
                .build());
        EventManager.registerEntityDamageByEntity(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (isOwner(event0.getDamager()) && isValidItem(Ability.DefaultItem)
                && !InvincibilityManager.isDamageGuard() && event0.getEntity() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) event0.getEntity();
        entity.getWorld().strikeLightning(entity.getLocation());
        entity.damage(25);
    }
}
