package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class NuclearPunch extends Ability {
    public NuclearPunch() {
        InitAbility("핵 펀치", Type.Active_Immediately, Rank.A,
                Usage.IronAttack + "상대에게 데미지 20을 주며 매우 멀리 밀칩니다.");
        InitAbility(45, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (isOwner(Event.getDamager()) && isValidItem(Ability.DefaultItem)
                && Event.getEntity() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) Event.getEntity();
        Event.setDamage(20);
        entity.getWorld().createExplosion(entity.getLocation(), 0.0F);
        Vector knockback = entity.getVelocity().add(
                Event.getDamager().getLocation().toVector()
                        .subtract(entity.getLocation().toVector())
                        .normalize().multiply(-24));
        knockback.setY(knockback.getY() + 5);
        entity.setVelocity(knockback);
    }
}
