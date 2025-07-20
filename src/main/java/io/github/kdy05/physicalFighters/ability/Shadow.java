package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.EventManager;

import io.github.kdy05.physicalFighters.utils.AUC;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.Objects;

public class Shadow extends Ability {
    public Shadow() {
        InitAbility("그림자", Type.Passive_AutoMatic, Rank.B,
                "몹에게 절대로 공격받지 않습니다.",
                "피격 시 10% 확률로 회피하며, 체력 4를 회복합니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityTarget.add(new EventData(this, 0));
        EventManager.onEntityDamage.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityTargetEvent event0 = (EntityTargetEvent) event;
                if (isOwner(event0.getTarget()))
                    return 0;
            }
            case 1 -> {
                EntityDamageEvent event1 = (EntityDamageEvent) event;
                if (isOwner(event1.getEntity()) && Math.random() < 0.10
                        && event1.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    return 1;
                }
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityTargetEvent event0 = (EntityTargetEvent) event;
                event0.setTarget(null);
                event0.setCancelled(true);
            }
            case 1 -> {
                EntityDamageEvent event1 = (EntityDamageEvent) event;
                event1.setDamage(0);
                if (getPlayer() == null) return;
                AUC.healEntity(getPlayer(), 4);
            }
        }
    }
}
