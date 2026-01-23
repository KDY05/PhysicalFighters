package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.AbilityUtils;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NuclearPunch extends Ability {
    public NuclearPunch() {
        InitAbility("핵펀치", Type.Active_Immediately, Rank.A,
                Usage.IronAttack + "대미지 20을 주며 매우 멀리 밀쳐버립니다.");
        InitAbility(45, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (isOwner(event0.getDamager()) && isValidItem(Ability.DefaultItem)
                && event0.getEntity() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) event0.getEntity();
        event0.setDamage(20);
        entity.getWorld().createExplosion(entity.getLocation(), 0.0F);
        AbilityUtils.goVelocity(entity, event0.getDamager().getLocation().add(0, -1, 0), -24);
    }
}
