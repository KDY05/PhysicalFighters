package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;

public class Ace extends Ability {
    public Ace(UUID playerUuid) {
        super(AbilitySpec.builder("에이스", Type.Active_Continue, Rank.S)
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
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        if (!InvincibilityManager.isDamageGuard() && isOwner(event0.getPlayer()) && isValidItem(Ability.DefaultItem)) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_DurationStart() {
        if (getPlayer() == null) return;
        new SplashFire(getPlayer()).runTaskTimer(plugin, 10L, 30L);
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
    }

    static class SplashFire extends BukkitRunnable {
        private int num = 0;
        private final Player caster;

        public SplashFire(Player caster) {
            this.caster = caster;
        }

        @Override
        public void run() {
            AbilityUtils.splashTask(caster, caster.getLocation(), 10,
                    entity -> entity.setFireTicks(80));
            if (this.num > 16) cancel();
            this.num += 1;
        }
    }
}
