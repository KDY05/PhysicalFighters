package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.module.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.AbilityUtils;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.AbilitySpec;

import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Boom extends Ability {
    public Boom() {
        super(AbilitySpec.builder("붐포인트", Type.Active_Continue, Rank.S)
                .cooldown(60)
                .duration(20)
                .guide(Usage.IronLeft + "20초간 10m 안에 있는 적을 폭발시킵니다.")
                .build());
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        if (!InvincibilityManager.isDamageGuard() && isOwner(event0.getPlayer()) && isValidItem(Ability.DefaultItem)) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
    }

    @Override
    public void A_DurationStart() {
        new Exploder(getPlayer()).runTaskTimer(plugin, 10L, 30L);
    }

    static class Exploder extends BukkitRunnable {
        private int count = 0;
        private final Player caster;
        public Exploder(Player caster) {
            this.caster = caster;
        }
        public void run() {
            Player caster = this.caster;
            if (caster == null) return;
            AbilityUtils.splashTask(caster, caster.getLocation(), 10, entity
                    -> entity.getWorld().createExplosion(entity.getLocation(), 0.5f));
            count++;
            if (count >= 13) cancel();
        }
    }

}
