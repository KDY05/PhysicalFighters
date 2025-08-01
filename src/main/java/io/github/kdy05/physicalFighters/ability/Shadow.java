package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.EventManager;

import io.github.kdy05.physicalFighters.utils.AbilityUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.utils.EventData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Shadow extends Ability {
    // 백스텝 각도 범위 (도 단위)
    private static final double BACKSTAB_ANGLE_THRESHOLD = 90.0; // 90도 = 후방 180도 범위

    public Shadow() {
        InitAbility("그림자", Type.Passive_AutoMatic, Rank.A,
                "은신 - 몹에게 절대로 공격받지 않습니다.",
                "회피 - 피격 시 10% 확률로 회피하며, 체력 4를 회복합니다.",
                "기습 - 뒤에서 공격할 시 대미지를 2배로 입히고, 일시적으로 추가 이동속도를 얻습니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityTarget.add(new EventData(this, 0));
        EventManager.onEntityDamage.add(new EventData(this, 1));
        EventManager.onEntityDamageByEntity.add(new EventData(this, 2));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityTargetEvent event0 = (EntityTargetEvent) event;
                if (isOwner(event0.getTarget()))
                    return 0;
            }
            case 1 -> {
                EntityDamageEvent event1 = (EntityDamageEvent) event;
                if (isOwner(event1.getEntity()) && Math.random() < 0.10
                        && event1.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    return 1;
                }
            }
            case 2 -> {
                EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
                if (!isOwner(event2.getDamager()) || !(event2.getEntity() instanceof LivingEntity target)) {
                    return -1;
                }
                Player attacker = (Player) event2.getDamager();
                if (isBackstabAttack(attacker, target)) {
                    return 2;
                }
                return -1;
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityTargetEvent event0 = (EntityTargetEvent) event;
                event0.setTarget(null);
                event0.setCancelled(true);
            }
            case 1 -> {
                EntityDamageEvent event1 = (EntityDamageEvent) event;
                event1.setDamage(0);
                if (getPlayer() == null) return;
                AbilityUtils.healEntity(getPlayer(), 4);
                sendMessage(ChatColor.GREEN + "회피하였습니다!");
            }
            case 2 -> {
                EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
                LivingEntity target = (LivingEntity) event2.getEntity();
                Player attacker = (Player) event2.getDamager();
                event2.setDamage(event2.getDamage() * 2.0D);
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 4, 0));
                attacker.sendMessage(ChatColor.GREEN + "기습 성공!");
                target.sendMessage(ChatColor.RED + "기습에 당했습니다!");
            }
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
        return dotProduct <= Math.cos(Math.toRadians(BACKSTAB_ANGLE_THRESHOLD));
    }

}
