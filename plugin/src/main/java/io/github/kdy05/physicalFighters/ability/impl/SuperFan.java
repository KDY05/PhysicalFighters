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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SuperFan extends Ability {
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
        
        Location casterLocation = caster.getLocation();
        Location knockbackOrigin = casterLocation.clone();
        knockbackOrigin.setY(knockbackOrigin.getY() - 1.0);
        
        // 플레이어가 바라보는 방향으로 바람 효과 생성
        createWindEffect(caster, casterLocation, knockbackOrigin);
    }
    
    private void createWindEffect(Player caster, Location origin, Location knockbackOrigin) {
        double yawRadians = Math.toRadians(-(origin.getYaw() % 360.0F));
        double pitchRadians = Math.toRadians(-(origin.getPitch() % 360.0F));
        
        Set<LivingEntity> affectedEntities = new HashSet<>();
        
        // 모든 step 위치에서 영향받을 엔티티들 수집
        for (int step = 1; step < 10; step++) {
            Location windLocation = calculateWindLocation(origin, yawRadians, pitchRadians, step);
            
            caster.getWorld().getNearbyEntities(windLocation, 3.0, 3.0, 3.0).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity != caster)
                .forEach(affectedEntities::add);
        }
        
        // 수집된 엔티티들에게 한 번씩만 효과 적용
        affectedEntities.forEach(entity -> applyWindEffects(entity, knockbackOrigin));
    }
    
    private Location calculateWindLocation(Location origin, double yawRadians, double pitchRadians, int step) {
        Location windLocation = origin.clone();
        
        double horizontalDistance = step + 2.0;
        double xOffset = horizontalDistance * (Math.sin(yawRadians) * Math.cos(pitchRadians));
        double yOffset = horizontalDistance * Math.sin(pitchRadians);
        double zOffset = horizontalDistance * (Math.cos(yawRadians) * Math.cos(pitchRadians));
        
        windLocation.add(xOffset, yOffset, zOffset);
        return windLocation;
    }
    
    private void applyWindEffects(LivingEntity target, Location knockbackOrigin) {
        AbilityUtils.goVelocity(target, knockbackOrigin, -2.5);
        
        target.addPotionEffect(PotionEffectFactory.createRegeneration(100, 0));
        target.addPotionEffect(PotionEffectFactory.createBlindness(200, 2));
        target.addPotionEffect(PotionEffectFactory.createNausea(200, 2));
        target.addPotionEffect(PotionEffectFactory.createWeakness(200, 2));
        
        target.sendMessage(ChatColor.LIGHT_PURPLE + "앗! 바람이 강하지만 시원해~♥");
    }
}
