package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Temari extends Ability {
    // 능력 설정 상수
    private static final int WIND_RANGE = 10;
    private static final double LIFT_HEIGHT = 4.0;
    private static final int MAX_COUNT = 13;
    private static final long INTERVAL = 30L;

    public Temari() {
        InitAbility("테마리", Type.Active_Continue, Rank.S,
                Usage.IronLeft + "20초간 자신의 주변에 있는 적들을 공중으로 날려버립니다.");
        InitAbility(60, 20, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player p = event0.getPlayer();
        if (!isOwner(p) || !isValidItem(Ability.DefaultItem)) {
            return -1;
        }
        if (ConfigManager.DamageGuard) {
            p.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
            return -1;
        }
        return 0;
    }

    @Override
    public void A_DurationStart() {
        if (getPlayer() == null) return;
        new WindBlastTask(getPlayer()).runTaskTimer(plugin, 10L, INTERVAL);
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
    }

    private static class WindBlastTask extends BukkitRunnable {
        private final Player caster;
        private int tickCount = 0;

        public WindBlastTask(Player caster) {
            this.caster = caster;
        }

        @Override
        public void run() {
            if (!caster.isOnline()) {
                cancel();
                return;
            }
            if (tickCount >= MAX_COUNT) {
                cancel();
                return;
            }
            blowAwayNearbyPlayers();
            tickCount++;
        }

        private void blowAwayNearbyPlayers() {
            AbilityUtils.splashTask(caster, caster.getLocation(), WIND_RANGE, entity -> {
                Location liftLoc = entity.getLocation().clone();
                liftLoc.setY(entity.getLocation().getY() + LIFT_HEIGHT);
                AbilityUtils.goVelocity(entity, liftLoc, 1);
            });
        }
    }

}