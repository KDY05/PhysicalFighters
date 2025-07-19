package io.github.kdy05.physicalFighters.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import io.github.kdy05.physicalFighters.core.EventManager;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.utils.EventData;

public class Zombie extends Ability {
    public Zombie() {
        InitAbility("좀비", Type.Passive_AutoMatic, Rank.B,
                "모든 데미지의 반을 흡수합니다. 단, 화염 데미지를 8배로 받습니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamage.add(new EventData(this));
        EventManager.onEntityDamageByEntity.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageEvent Event = (EntityDamageEvent) event;
            if (isOwner(Event.getEntity())) {
                if (Event.getCause() == DamageCause.LAVA || Event.getCause() == DamageCause.FIRE
                        || Event.getCause() == DamageCause.FIRE_TICK)
                    return 0;
                if (Event.getCause() == DamageCause.BLOCK_EXPLOSION
                        || Event.getCause() == DamageCause.ENTITY_EXPLOSION)
                    return 1;
                if (Event.getCause() == DamageCause.FALL || Event.getCause() == DamageCause.POISON
                        || Event.getCause() == DamageCause.PROJECTILE)
                    return 1;
            }
        } else if (CustomData == 1) {
            EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
            if (isOwner(Event1.getEntity()) && Event1.getDamager() instanceof LivingEntity entity) {
                if (!(entity instanceof Player p)) return 2;
                Material handItem = p.getInventory().getItemInMainHand().getType();
                if (handItem != Material.GOLD_INGOT && handItem != DefaultItem) {
                    return 2;
                }
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageEvent Event = (EntityDamageEvent) event;
                Event.setDamage(Event.getDamage() * 8);
                break;
            case 1:
                EntityDamageEvent Event2 = (EntityDamageEvent) event;
                Event2.setDamage(Event2.getDamage() * 0.5);
                break;
            case 2:
                EntityDamageByEntityEvent Event3 = (EntityDamageByEntityEvent) event;
                Event3.setDamage(Event3.getDamage() * 0.5);
        }
    }
}
