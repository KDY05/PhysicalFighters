package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.ConfigManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.kdy05.physicalFighters.core.Ability;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Ace extends Ability {
    public Ace() {
        InitAbility("에이스", Type.Active_Immediately, Rank.S,
                "철괴를 휘두를 시 20초간 자신의 주변에 있는 적들을 불태웁니다.");
        InitAbility(40, 0, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (!ConfigManager.DamageGuard && isOwner(Event.getPlayer()) && isValidItem(Ability.DefaultItem)) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        new SplashFire(Event.getPlayer()).runTaskTimer(plugin, 10L, 30L);
    }

    static class SplashFire extends BukkitRunnable {
        private int num = 0;
        private final Player caster;

        public SplashFire(Player caster) {
            this.caster = caster;
        }

        @Override
        public void run() {
            caster.getWorld().getNearbyEntities(caster.getLocation(), 10, 10, 10).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .map(entity -> (LivingEntity) entity)
                    .filter(entity -> entity != caster)
                    .forEach(entity -> entity.setFireTicks(80));
            if (this.num > 16) cancel();
            this.num += 1;
        }
    }
}
