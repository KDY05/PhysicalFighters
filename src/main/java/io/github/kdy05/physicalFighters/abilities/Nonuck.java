package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Nonuck extends Ability {
    public Nonuck() {
        InitAbility("무통증", Type.Passive_AutoMatic, Rank.B,
                "피격 시 80% 확률로 넉백을 무시합니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (isOwner(Event.getEntity()) && (Math.random() <= 0.8D) && !EventManager.DamageGuard
                && (Event.getCause() == DamageCause.ENTITY_ATTACK || Event.getCause() == DamageCause.PROJECTILE)) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Player p = (Player) Event.getEntity();
        int damage = (int) Event.getDamage();
        p.damage(damage);
        Event.setCancelled(true);
    }
}
