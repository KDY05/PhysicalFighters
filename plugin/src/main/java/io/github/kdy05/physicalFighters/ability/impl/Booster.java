package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.EventData;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Random;
import java.util.UUID;

public class Booster extends Ability {
    public Booster(UUID playerUuid) {
        super(AbilitySpec.builder("부스터", Type.PassiveAutoMatic, Rank.A)
                .guide("폭주 - 매우 낮은 딜레이로 상대를 공격합니다. 단 당신의 대미지는 3~6로 랜덤입니다.",
                        "무통증 - 피격 시 80% 확률로 넉백을 무시합니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamageByEntity(new EventData(this));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (isOwner(event0.getDamager()) && event0.getEntity() instanceof LivingEntity) {
            return 0;
        }
        else if (isOwner(event0.getEntity()) && Math.random() <= 0.8D && !InvincibilityManager.isDamageGuard()
                && (event0.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || event0.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)) {
            return 1;
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            LivingEntity entity = (LivingEntity) event0.getEntity();
            Random rand = new Random();
            event0.setDamage(3 + rand.nextDouble() * 3);
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    entity.setNoDamageTicks(6), 1);
        } else if (CustomData == 1) {
            EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) event;
            Player player = (Player) event1.getEntity();
            double damage = event1.getDamage();
            player.damage(damage);
            event1.setCancelled(true);
        }
    }
}
