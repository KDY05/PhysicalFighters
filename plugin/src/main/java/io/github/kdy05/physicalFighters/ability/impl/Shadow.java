package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import io.github.kdy05.physicalFighters.util.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public final class Shadow extends Ability {
    private static final double AVOID_CHANCE = 0.05;
    private static final double AVOID_HEAL_AMOUNT = 4.0;
    private static final double BACKSTAB_ANGLE_THRESHOLD = 30.0;
    private static final double BACKSTAB_MULTIPLIER = 2.0;

    public Shadow(UUID playerUuid) {
        super(AbilitySpec.builder("그림자", Type.PassiveAutoMatic, Rank.A)
                .guide("회피 - 피격 시 5% 확률로 회피하며, 체력 4를 회복합니다.",
                        "기습 - 뒤에서 공격할 시 대미지를 2배로 입히고, 상대에게 일시적으로 실명을 부여합니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamage(new EventData(this, 0));
        EventManager.registerEntityDamageByEntity(new EventData(this, 1));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageEvent event0 = (EntityDamageEvent) event;
            if (isOwner(event0.getEntity()) && Math.random() < AVOID_CHANCE
                    && event0.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                return 0;
            }
        } else if (CustomData == 1) {
            EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) event;
            if (!isOwner(event1.getDamager()) || !(event1.getEntity() instanceof LivingEntity)) {
                return -1;
            }
            LivingEntity target = (LivingEntity) event1.getEntity();
            Player attacker = (Player) event1.getDamager();
            if (isBackstabAttack(attacker, target)) {
                return 1;
            }
            return -1;
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageEvent event0 = (EntityDamageEvent) event;
            event0.setDamage(0);
            if (getPlayer() == null) return;
            AbilityUtils.healEntity(getPlayer(), AVOID_HEAL_AMOUNT);
            SoundUtils.playSuccessSound(getPlayer());
            sendMessage(ChatColor.GREEN + "회피하였습니다!");
        } else if (CustomData == 1) {
            EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) event;
            LivingEntity target = (LivingEntity) event1.getEntity();
            Player attacker = (Player) event1.getDamager();
            event1.setDamage(event1.getDamage() * BACKSTAB_MULTIPLIER);
            target.addPotionEffect(PotionEffectFactory.createBlindness(20 * 3, 0));
            SoundUtils.playSuccessSound(getPlayer());
            attacker.sendMessage(ChatColor.GREEN + "기습 성공!");
        }
    }

    /**
     * pitch 무시하고 top view에서 백스텝인지 검증
     */
    private boolean isBackstabAttack(Player attacker, LivingEntity target) {
        Location attackerLoc = attacker.getLocation();
        Location targetLoc = target.getLocation();

        Vector targetDirection = targetLoc.getDirection();
        targetDirection.setY(0);
        targetDirection.normalize();

        Vector attackVector = new Vector(attackerLoc.getX() - targetLoc.getX(), 0,
                attackerLoc.getZ() - targetLoc.getZ()).normalize();

        double dotProduct = targetDirection.dot(attackVector);
        return dotProduct <= -Math.cos(Math.toRadians(BACKSTAB_ANGLE_THRESHOLD));
    }

}
