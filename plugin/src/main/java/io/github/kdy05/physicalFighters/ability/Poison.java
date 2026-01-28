package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.AbilitySpec;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;

public class Poison extends Ability {
    public Poison() {
        super(AbilitySpec.builder("포이즌", Type.Passive_AutoMatic, Rank.A)
                .guide("자신에게 공격받은 사람은 3초간 독에 감염됩니다.")
                .build());
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (isOwner(event0.getDamager()) && event0.getEntity() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) event0.getEntity();
        entity.addPotionEffect(PotionEffectFactory.createPoison(60, 0));
    }

}
