package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.ConfigManager;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Ckyomi extends Ability {
    public Ckyomi() {
        InitAbility("츠쿠요미", Type.Passive_AutoMatic, Rank.A,
                "상대를 공격하면 상대에게 5초간 혼란 효과와 디버프를 줍니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (!ConfigManager.DamageGuard && isOwner(event0.getDamager())
                && event0.getEntity() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) event0.getEntity();
        entity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
    }
}
