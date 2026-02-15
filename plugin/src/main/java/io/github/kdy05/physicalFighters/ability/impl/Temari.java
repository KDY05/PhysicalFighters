package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Temari extends Ability {
    // 능력 설정 상수
    private static final int WIND_RANGE = 10;
    private static final double LIFT_HEIGHT = 4.0;
    private static final int MAX_COUNT = 13;
    private static final long INTERVAL = 30L;

    public Temari(UUID playerUuid) {
        super(AbilitySpec.builder("테마리", Type.ActiveContinue, Rank.S)
                .cooldown(60)
                .duration(20)
                .guide(Usage.IronLeft + "능력 지속 시간동안 자신의 주변에 있는 적들을 공중으로 날려버립니다.",
                        "이때 날아간 플레이어는 일정 확률로 손에 쥐고 있는 아이템을 떨어뜨립니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player p = event0.getPlayer();
        if (!isOwner(p) || !isValidItem(Ability.DefaultItem)) {
            return -1;
        }
        if (InvincibilityManager.isDamageGuard()) {
            p.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
            return -1;
        }
        return 0;
    }

    @Override
    public void onDurationStart() {
        if (getPlayer() == null) return;
        new WindBlastTask(getPlayer()).runTaskTimer(plugin, 10L, INTERVAL);
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
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
                if (entity instanceof Player && Math.random() <= 0.20) {
                    Player player = (Player) entity;
                    ItemStack item = player.getInventory().getItemInMainHand();
                    caster.getWorld().dropItem(player.getLocation(), item);
                    player.getInventory().removeItem(item);
                    player.sendMessage(ChatColor.RED + "테마리의 강풍에 의해 손에 쥐고 있는 아이템을 떨어뜨렸습니다.");
                }
            });
        }
    }

}