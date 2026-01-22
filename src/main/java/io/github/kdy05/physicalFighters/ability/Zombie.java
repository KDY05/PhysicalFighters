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
                "모든 대미지의 반을 흡수합니다. 단, 화염 대미지를 8배로 받습니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamage.add(new EventData(this));
        EventManager.onEntityDamageByEntity.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageEvent event0 = (EntityDamageEvent) event;
            if (isOwner(event0.getEntity())) {
                if (event0.getCause() == DamageCause.LAVA || event0.getCause() == DamageCause.FIRE
                        || event0.getCause() == DamageCause.FIRE_TICK)
                    return 0;
                if (event0.getCause() == DamageCause.BLOCK_EXPLOSION
                        || event0.getCause() == DamageCause.ENTITY_EXPLOSION)
                    return 1;
                if (event0.getCause() == DamageCause.FALL || event0.getCause() == DamageCause.POISON
                        || event0.getCause() == DamageCause.PROJECTILE)
                    return 1;
            }
        } else if (CustomData == 1) {
            EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) event;
            if (isOwner(event1.getEntity()) && event1.getDamager() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) event1.getDamager();
                if (!(entity instanceof Player)) return 2;
                Player p = (Player) entity;
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
        if (CustomData == 0) {
            EntityDamageEvent event0 = (EntityDamageEvent) event;
            event0.setDamage(event0.getDamage() * 8);
        } else if (CustomData == 1) {
            EntityDamageEvent event1 = (EntityDamageEvent) event;
            event1.setDamage(event1.getDamage() * 0.5);
        } else if (CustomData == 2) {
            EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
            event2.setDamage(event2.getDamage() * 0.5);
        }
    }
}
