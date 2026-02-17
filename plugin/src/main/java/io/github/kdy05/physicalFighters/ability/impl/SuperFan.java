package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class SuperFan extends Ability {
    public SuperFan(UUID playerUuid) {
        super(AbilitySpec.builder("선풍기", Type.ActiveImmediately, Rank.C)
                .cooldown(20)
                .guide(Usage.IronLeft + "바라보는 방향의 플레이어들을 날려버립니다.",
                        "플레이어들은 무더위에 시원함을 느껴 체력이 회복됩니다.",
                        "하지만 강한 바람에 의해 눈을 뜨기가 힘들고 허약해집니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        if (isOwner(event0.getPlayer()) && isValidItem(Ability.DefaultItem) && !InvincibilityManager.isDamageGuard()) {
            return 0;
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        Player caster = getPlayer();
        if (caster == null) return;

        Location origin = caster.getLocation();
        Vector direction = origin.getDirection();
        Location knockbackOrigin = origin.clone().subtract(0, 1, 0);

        // 시선 방향으로 10단계에 걸쳐 범위 내 엔티티 수집
        Set<LivingEntity> affected = new HashSet<>();
        for (int step = 1; step < 10; step++) {
            Location windLoc = origin.clone().add(direction.clone().multiply(step + 2));
            caster.getWorld().getNearbyEntities(windLoc, 3, 3, 3).stream()
                    .filter(e -> e instanceof LivingEntity && e != caster)
                    .forEach(e -> affected.add((LivingEntity) e));
        }

        for (LivingEntity target : affected) {
            AbilityUtils.goVelocity(target, knockbackOrigin, -2.5);
            target.addPotionEffect(PotionEffectFactory.createRegeneration(100, 0));
            target.addPotionEffect(PotionEffectFactory.createBlindness(200, 2));
            target.addPotionEffect(PotionEffectFactory.createNausea(200, 2));
            target.addPotionEffect(PotionEffectFactory.createWeakness(200, 2));
            target.sendMessage(ChatColor.LIGHT_PURPLE + "앗! 바람이 강하지만 시원해~♥");
        }
    }
}
