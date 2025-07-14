package io.github.kdy05.physicalFighters.abilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.Random;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Booster extends Ability {
    public Booster() {
        InitAbility("부스터", Type.Passive_AutoMatic, Rank.B,
                "공격 시에 딜레이가 매우 낮습니다. 단 당신의 데미지는 3~6로 랜덤입니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (isOwner(Event.getDamager()) && Event.getEntity() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) Event.getEntity();
        Random rand = new Random();
        Event.setDamage(rand.nextDouble(3, 6));
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                entity.setNoDamageTicks(6), 1);
    }
}
