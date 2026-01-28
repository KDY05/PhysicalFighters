package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.module.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.AbilityUtils;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.AbilitySpec;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Cuma extends Ability {
    public Cuma() {
        super(AbilitySpec.builder("바솔로뮤 쿠마", Type.Passive_AutoMatic, Rank.S)
                .guide("피격 시 상대를 넉백시키며, 일정 확률로 받은 공격을 상대에게 되돌려줍니다.")
                .build());
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (isOwner(event0.getEntity()) && !InvincibilityManager.isDamageGuard()
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
        AbilityUtils.goVelocity(target, caster.getLocation(), -1);
    }
}
