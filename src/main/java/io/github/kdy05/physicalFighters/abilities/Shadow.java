package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.EventManager;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.utils.EventData;

public class Shadow extends Ability {
    public Shadow() {
        InitAbility("그림자", Type.Passive_AutoMatic, Rank.B,
                "몹에게 절대로 공격받지 않습니다.",
                "피격 시 7% 확률로 회피하며, 체력 4를 회복합니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityTarget.add(new EventData(this, 0));
        EventManager.onEntityDamage.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityTargetEvent Event0 = (EntityTargetEvent) event;
                if (isOwner(Event0.getTarget()))
                    return 0;
                break;
            case 1:
                EntityDamageEvent Event1 = (EntityDamageEvent) event;
                if (isOwner(Event1.getEntity()) && Math.random() <= 0.07
                        && Event1.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    return 1;
                }
                break;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityTargetEvent Event0 = (EntityTargetEvent) event;
                Event0.setTarget(null);
                Event0.setCancelled(true);
                break;
            case 1:
                EntityDamageEvent Event1 = (EntityDamageEvent) event;
                Event1.setDamage(0);
                getPlayer().setHealth(Math.min(20, getPlayer().getHealth() + 4));
                break;
        }
    }
}
