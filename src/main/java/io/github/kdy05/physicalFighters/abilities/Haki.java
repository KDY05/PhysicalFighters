package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.PhysicalFighters;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Haki extends Ability {
    // 능력 설정 상수
    private static final double RANGE = 10.0;
    private static final double DAMAGE = 8.0;
    private static final int DURATION = 10;
    private static final long INTERVAL = 40L;
    private static final long DELAY = 10L;

    public Haki() {
        InitAbility("패기", Type.Active_Immediately, Rank.SS,
                "능력 사용시 20초간 10칸 내의 적에게 강한 데미지를 줍니다.");
        InitAbility(160, 0, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        if (!isOwner(p) || !isValidItem(Ability.DefaultItem)) {
            return -1;
        }
        if (PhysicalFighters.DamageGuard) {
            p.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
            return -1;
        }
        return 0;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player caster = Event.getPlayer();
        new ConquerorHakiTask(caster).runTaskTimer(PhysicalFighters.getPlugin(), DELAY, INTERVAL);
        caster.sendMessage(ChatColor.DARK_RED + "패기를 발산합니다!");
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
                caster.sendMessage(ChatColor.GREEN + "패기가 사라졌습니다.");
                return;
            }
            applyConquerorHaki();
            tickCount++;
        }

        private void applyConquerorHaki() {
            caster.getWorld().getNearbyEntities(caster.getLocation(), Haki.RANGE, Haki.RANGE, Haki.RANGE).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .map(entity -> (LivingEntity) entity)
                    .filter(entity -> entity != caster)
                    .forEach(entity -> {
                        entity.damage(Haki.DAMAGE, caster);
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 30, 0));
                        if (entity instanceof Player) entity.sendMessage(ChatColor.DARK_RED + "패기에 압도당했습니다!");
                    });
        }
    }
}