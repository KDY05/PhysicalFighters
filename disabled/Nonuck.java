package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Nonuck extends Ability {
    public Nonuck() {
        InitAbility("무통증", Type.Passive_AutoMatic, Rank.C,
                "피격 시 80% 확률로 넉백을 무시합니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (isOwner(event0.getEntity()) && Math.random() <= 0.8D && !ConfigManager.DamageGuard
                && (event0.getCause() == DamageCause.ENTITY_ATTACK || event0.getCause() == DamageCause.PROJECTILE)) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        Player player = (Player) event0.getEntity();
        double damage = event0.getDamage();
        player.damage(damage);
        event0.setCancelled(true);
    }

}
