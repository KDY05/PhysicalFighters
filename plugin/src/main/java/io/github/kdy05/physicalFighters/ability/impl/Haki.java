package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public final class Haki extends Ability {
    private static final double RANGE = 10.0;
    private static final double DAMAGE = 8.0;
    private static final long INTERVAL = 40L;
    private static final long DELAY = 10L;

    private ConquerorHakiTask hakiTask;
    private BukkitRunnable rangeIndicator;

    public Haki(UUID playerUuid) {
        super(AbilitySpec.builder("패기", Type.ActiveContinue, Rank.SS)
                .cooldown(160)
                .duration(10)
                .guide(Usage.IronLeft + "능력 지속 시간동안 10칸 내의 적에게 강한 대미지를 줍니다.")
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
        hakiTask = new ConquerorHakiTask(getPlayer());
        hakiTask.runTaskTimer(plugin, DELAY, INTERVAL);
        rangeIndicator = AbilityUtils.createCircleIndicator(Objects.requireNonNull(
                getPlayer()), RANGE, Particle.REDSTONE,
                new Particle.DustOptions(Color.fromRGB(139, 0, 0), 1.5f));
    }

    @Override
    public void onDurationFinalize() {
        if (hakiTask != null && !hakiTask.isCancelled()) {
            hakiTask.cancel();
        }
        if (rangeIndicator != null && !rangeIndicator.isCancelled()) {
            rangeIndicator.cancel();
        }
    }

    static class ConquerorHakiTask extends BukkitRunnable {
        private final Player caster;

        ConquerorHakiTask(Player caster) {
            this.caster = caster;
        }

        @Override
        public void run() {
            if (caster == null) return;
            AbilityUtils.splashTask(caster, caster.getLocation(), RANGE, entity -> {
                entity.damage(DAMAGE, caster);
                entity.addPotionEffect(PotionEffectFactory.createNausea(30, 0));
                entity.sendMessage(ChatColor.DARK_RED + "패기에 압도당했습니다!");
            });
        }
    }
}
