package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.utils.AUC;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Cuma extends Ability {
    public Cuma() {
        InitAbility("바솔로뮤 쿠마", Type.Passive_AutoMatic, Rank.S,
                "피격 시 상대를 넉백시키며, 일정 확률로 받은 공격을 상대에게 되돌려줍니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (isOwner(event0.getEntity()) && !PhysicalFighters.DamageGuard
                && event0.getDamager() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        Player caster = (Player) event0.getEntity();
        LivingEntity target = (LivingEntity) event0.getDamager();
        if (Math.random() <= 0.20) {
            target.damage(event0.getDamage());
            event0.setCancelled(true);
        }
        target.getWorld().createExplosion(target.getLocation(), 0.0F);
        AUC.goVelocity(target, caster.getLocation(), -1);
    }
}
