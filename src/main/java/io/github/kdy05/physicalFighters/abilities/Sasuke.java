package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Sasuke extends Ability {
    public Sasuke() {
        InitAbility("사스케", Type.Active_Immediately, Rank.S,
                "치도리 - 철퇴로 상대를 타격하여 엄청난 데미지로 감전시킵니다.");
        InitAbility(30, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (isOwner(Event.getDamager()) && isValidItem(Ability.DefaultItem) && !PhysicalFighters.DamageGuard
                && Event.getEntity() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) Event.getEntity();
        entity.getWorld().strikeLightning(entity.getLocation());
        entity.damage(30);
    }
}
