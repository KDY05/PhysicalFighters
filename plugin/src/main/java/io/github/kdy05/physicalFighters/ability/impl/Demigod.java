package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import java.util.UUID;

public class Demigod extends Ability {
    public Demigod(UUID playerUuid) {
        super(AbilitySpec.builder("데미갓", Type.PassiveAutoMatic, Rank.S)
                .guide("반은 인간, 반은 신인 능력자입니다.",
                        "대미지를 받으면 일정 확률로 10초간 랜덤 버프가 발동됩니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamage(new EventData(this));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        EntityDamageEvent event0 = (EntityDamageEvent) event;
        if (!InvincibilityManager.isDamageGuard() && isOwner(event0.getEntity())) {
            return 0;
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        EntityDamageEvent event0 = (EntityDamageEvent) event;
        Player player = (Player) event0.getEntity();
        if (Math.random() <= 0.05D)
            AbilityUtils.healEntity(player, 2);
        if (Math.random() <= 0.05D)
            player.addPotionEffect(PotionEffectFactory.createResistance(200, 0));
        if (Math.random() <= 0.05D)
            player.addPotionEffect(PotionEffectFactory.createRegeneration(200, 0));
        if (Math.random() <= 0.1D)
            player.addPotionEffect(PotionEffectFactory.createJumpBoost(200, 0));
        if (Math.random() <= 0.1D)
            player.addPotionEffect(PotionEffectFactory.createHaste(200, 0));
        if (Math.random() <= 0.1D)
            player.addPotionEffect(PotionEffectFactory.createSpeed(200, 0));
    }
}
