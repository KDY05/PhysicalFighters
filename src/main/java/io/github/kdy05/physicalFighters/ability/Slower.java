package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class Slower extends Ability {
    public Slower() {
        InitAbility("슬로워", Type.Passive_AutoMatic, Rank.C,
                "자신에게 공격받은 사람은 2초간 느려집니다,");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (isOwner(Event.getDamager()) && !PhysicalFighters.DamageGuard && Event.getEntity() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) Event.getEntity();
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0));
    }
}
