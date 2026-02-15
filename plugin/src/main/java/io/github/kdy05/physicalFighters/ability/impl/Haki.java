package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Haki extends Ability {
    // 능력 설정 상수
    private static final double RANGE = 10.0;
    private static final double DAMAGE = 8.0;
    private static final int DURATION = 10;
    private static final long INTERVAL = 40L;
    private static final long DELAY = 10L;

    public Haki(UUID playerUuid) {
        super(AbilitySpec.builder("패기", Type.ActiveContinue, Rank.SS)
                .cooldown(160)
                .duration(10)
                .guide(Usage.IronLeft + "20초간 10칸 내의 적에게 강한 대미지를 줍니다.")
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
        new ConquerorHakiTask(getPlayer()).runTaskTimer(PhysicalFighters.getPlugin(), DELAY, INTERVAL);
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
    }

    private static class ConquerorHakiTask extends BukkitRunnable {
        private final Player caster;
        private int tickCount = 0;

        public ConquerorHakiTask(Player caster) {
            this.caster = caster;
        }

        @Override
        public void run() {
            if (!caster.isOnline()) {
                cancel();
                return;
            }
            if (tickCount >= DURATION) {
                cancel();
                return;
            }
            applyConquerorHaki();
            tickCount++;
        }

        private void applyConquerorHaki() {
            AbilityUtils.splashTask(caster, caster.getLocation(), Haki.RANGE, entity -> {
                entity.damage(Haki.DAMAGE, caster);
                entity.addPotionEffect(PotionEffectFactory.createNausea(30, 0));
                entity.sendMessage(ChatColor.DARK_RED + "패기에 압도당했습니다!");
            });
        }
    }

}