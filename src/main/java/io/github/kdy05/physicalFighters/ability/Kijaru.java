package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.ConfigManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Kijaru extends Ability {
    public Kijaru() {
        InitAbility("키자루", Type.Active_Immediately, Rank.SS,
                Usage.IronAttack + "타격한 상대를 빛의 속도로 타격합니다.",
                "상대는 엄청난 속도로 멀리 날라갑니다. 당신도 상대를 따라 근접하게 날라갑니다.",
                Usage.Passive + "낙하 데미지를 받지 않습니다.");
        InitAbility(45, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
        EventManager.onEntityDamage.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
                if (isOwner(event0.getDamager()) && isValidItem(Ability.DefaultItem)
                        && !ConfigManager.DamageGuard && event0.getEntity() instanceof LivingEntity)
                    return 0;
            }
            case 1 -> {
                EntityDamageEvent event1 = (EntityDamageEvent) event;
                if (isOwner(event1.getEntity()) && event1.getCause() == DamageCause.FALL) {
                    sendMessage(ChatColor.GREEN + "사뿐하게 떨어져 데미지를 받지 않았습니다.");
                    event1.setCancelled(true);
                }
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) event0.getEntity();
        if (getPlayer() == null) return;

//        entity.getWorld().createExplosion(entity.getLocation(), 0.0F);
        AbilityUtils.goVelocity(entity, getPlayer().getLocation().clone().add(0, -0.5, 0), -5);
        new KizaruTask(getPlayer(), entity).runTaskLater(plugin, 20L);
        entity.damage(8);
        event0.setCancelled(true);
    }

    static class KizaruTask extends BukkitRunnable {
        Player caster;
        LivingEntity target;

        KizaruTask(Player caster, LivingEntity target) {
            this.caster = caster;
            this.target = target;
        }

        @Override
        public void run() {
            Location loc2 = target.getLocation();
            loc2.setY(loc2.getY() + 2.0D);
            this.caster.teleport(loc2);
            caster.getWorld().createExplosion(caster.getLocation(), 0.0f);
        }
    }
}
