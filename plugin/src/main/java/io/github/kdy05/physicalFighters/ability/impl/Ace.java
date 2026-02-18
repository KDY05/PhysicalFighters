package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

import java.util.UUID;

public final class Ace extends Ability {
    private SplashFire splashFire;
    private BukkitRunnable rangeIndicator;

    public Ace(UUID playerUuid) {
        super(AbilitySpec.builder("에이스", Type.ActiveContinue, Rank.S)
                .cooldown(40)
                .duration(20)
                .guide(Usage.IronLeft + "능력 지속시간 동안 자신의 주변에 있는 적들을 불태웁니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        if (!InvincibilityManager.isDamageGuard() && isOwner(event0.getPlayer()) && isValidItem(Ability.DefaultItem)) {
            return 0;
        }
        return -1;
    }

    @Override
    public void onDurationStart() {
        if (getPlayer() == null) return;
        splashFire = new SplashFire(getPlayer());
        splashFire.runTaskTimer(plugin, 10L, 30L);
        rangeIndicator = AbilityUtils.createCircleIndicator(Objects.requireNonNull(
                getPlayer()), 10.0, Particle.FLAME);
    }

    @Override
    public void onDurationFinalize() {
        if (splashFire != null && !splashFire.isCancelled()) {
            splashFire.cancel();
        }
        if (rangeIndicator != null && !rangeIndicator.isCancelled()) {
            rangeIndicator.cancel();
        }
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
    }

    static class SplashFire extends BukkitRunnable {
        private final Player caster;

        public SplashFire(Player caster) {
            this.caster = caster;
        }

        @Override
        public void run() {
            AbilityUtils.splashTask(caster, caster.getLocation(), 10,
                    entity -> entity.setFireTicks(80));
        }
    }
}
