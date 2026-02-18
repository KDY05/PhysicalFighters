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

public final class Boom extends Ability {
    private Exploder exploder;
    private BukkitRunnable rangeIndicator;

    public Boom(UUID playerUuid) {
        super(AbilitySpec.builder("붐포인트", Type.ActiveContinue, Rank.S)
                .cooldown(40)
                .duration(20)
                .guide(Usage.IronLeft + "지속 시간동안 10m 안에 있는 적을 폭발시킵니다.")
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
    public void applyEffect(Event event, int CustomData) {
    }

    @Override
    public void onDurationStart() {
        exploder = new Exploder(getPlayer());
        exploder.runTaskTimer(plugin, 10L, 30L);
        rangeIndicator = AbilityUtils.createCircleIndicator(Objects.requireNonNull(
                getPlayer()), 10.0, Particle.EXPLOSION_NORMAL);
    }

    @Override
    public void onDurationFinalize() {
        if (exploder != null && !exploder.isCancelled()) {
            exploder.cancel();
        }
        if (rangeIndicator != null && !rangeIndicator.isCancelled()) {
            rangeIndicator.cancel();
        }
    }

    static class Exploder extends BukkitRunnable {
        private final Player caster;

        public Exploder(Player caster) {
            this.caster = caster;
        }

        @Override
        public void run() {
            if (caster == null) return;
            AbilityUtils.splashTask(caster, caster.getLocation(), 10, entity
                    -> entity.getWorld().createExplosion(entity.getLocation(), 0.5f));
        }
    }

}
