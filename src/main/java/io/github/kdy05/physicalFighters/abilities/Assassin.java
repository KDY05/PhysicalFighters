package io.github.kdy05.physicalFighters.abilities;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Assassin extends Ability {
    // 백스텝 각도 범위 (도 단위)
    private static final double BACKSTAB_ANGLE_THRESHOLD = 90.0; // 90도 = 후방 180도 범위

    public Assassin() {
        InitAbility("어쌔신", Type.Passive_AutoMatic, Rank.B,
                "뒤에서 공격할시에 데미지를 두배로 입히고 눈을 가립니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (!isOwner(Event.getDamager()) || !(Event.getEntity() instanceof LivingEntity target)) {
            return -1;
        }
        Player attacker = (Player) Event.getDamager();
        if (isBackstabAttack(attacker, target)) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        LivingEntity target = (LivingEntity) Event.getEntity();
        Player attacker = (Player) Event.getDamager();
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
        Event.setDamage(Event.getDamage() * 2.0D);
        attacker.sendMessage(ChatColor.GREEN + "백스텝 성공!");
        if (target instanceof Player) {
            target.sendMessage(ChatColor.RED + "백스텝에 당했습니다!");
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
