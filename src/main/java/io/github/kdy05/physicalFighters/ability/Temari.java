package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Temari extends Ability {
    // 능력 설정 상수
    private static final int WIND_RANGE = 10;
    private static final double LIFT_HEIGHT = 4.0;
    private static final int MAX_COUNT = 14;
    private static final long INTERVAL = 30L;

    public Temari() {
        InitAbility("테마리", Type.Active_Immediately, Rank.S,
                "철괴 좌클릭 시 20초간 자신의 주변에 있는 적들을 공중으로 날려버립니다.");
        InitAbility(60, 0, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
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
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player caster = Event.getPlayer();
        new WindBlastTask(caster).runTaskTimer(plugin, 0, INTERVAL);
        caster.sendMessage(ChatColor.AQUA + "강풍을 일으킵니다!");
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
                caster.sendMessage(ChatColor.GREEN + "강풍이 멈췄습니다.");
                return;
            }
            blowAwayNearbyPlayers();
            tickCount++;
        }

        private void blowAwayNearbyPlayers() {
            caster.getWorld().getNearbyEntities(caster.getLocation(), WIND_RANGE, WIND_RANGE, WIND_RANGE).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .map(entity -> (LivingEntity) entity)
                    .filter(entity -> entity != caster)
                    .forEach(entity -> {
                        Location liftLoc = entity.getLocation().clone();
                        liftLoc.setY(entity.getLocation().getY() + LIFT_HEIGHT);
                        AbilityUtils.goVelocity(entity, liftLoc, 1);
                    });
        }
    }
}