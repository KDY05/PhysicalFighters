package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.UUID;

public class Kijaru extends Ability {

    private final TeleportChargeManager teleportManager = new TeleportChargeManager();

    public Kijaru(UUID playerUuid) {
        super(AbilitySpec.builder("키자루", Type.Active_Immediately, Rank.SS)
                .cooldown(45)
                .guide(Usage.IronAttack + "타격한 상대를 빛의 속도로 타격합니다.",
                        "상대는 엄청난 속도로 멀리 날라갑니다. 당신도 상대를 따라 근접하게 날라갑니다.",
                        Usage.IronRight + "바라보는 곳으로 순간이동합니다. (충전시간: 120초 / 최대 충전량 2회)",
                        Usage.Passive + "낙하 대미지를 받지 않습니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamageByEntity(new EventData(this));
        registerRightClickEvent();
        EventManager.registerEntityDamage(new EventData(this, 2));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (isOwner(event0.getDamager()) && isValidItem(Ability.DefaultItem)
                    && !InvincibilityManager.isDamageGuard() && event0.getEntity() instanceof LivingEntity)
                return 0;
        } else if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            if (!isOwner(event1.getPlayer()) || !isValidItem(Ability.DefaultItem)) return -1;

            if (!teleportManager.canUse()) {
                int timeLeft = teleportManager.getTimeToNextCharge();
                sendMessage(ChatColor.RED + "충전 중 (다음 충전까지 " + timeLeft + "초)");
                return -1;
            }

            Player caster = event1.getPlayer();
            Location targetLocation = AbilityUtils.getTargetLocation(caster, 80);
            if (targetLocation == null) {
                sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
                return -1;
            }

            executeTeleport(caster, targetLocation);
            return -1;
        } else if (CustomData == 2) {
            EntityDamageEvent event2 = (EntityDamageEvent) event;
            if (isOwner(event2.getEntity()) && event2.getCause() == DamageCause.FALL) {
                sendMessage(ChatColor.GREEN + "사뿐하게 떨어져 대미지를 받지 않았습니다.");
                event2.setCancelled(true);
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) event0.getEntity();
        if (getPlayer() == null) return;

        AbilityUtils.goVelocity(entity, getPlayer().getLocation().clone().add(0, -0.5, 0), -5);
        new KizaruTask(getPlayer(), entity).runTaskLater(plugin, 20L);
        entity.damage(8);
        event0.setCancelled(true);
    }

    @Override
    public void A_SetEvent(Player p) {
        teleportManager.reset();
    }

    private void executeTeleport(Player caster, Location targetLocation) {
        targetLocation.setY(caster.getWorld().getHighestBlockYAt(targetLocation) + 1.0);
        targetLocation.setPitch(caster.getLocation().getPitch());
        targetLocation.setYaw(caster.getLocation().getYaw());
        caster.teleport(targetLocation);
        caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        teleportManager.use();
        int remainingCharges = teleportManager.getCurrentCharges();
        sendMessage(ChatColor.YELLOW + "순간이동! (" + remainingCharges + "/2)");
    }

    static class KizaruTask extends BukkitRunnable {
        Player caster;
        LivingEntity target;

        KizaruTask(Player caster, LivingEntity target) {
            this.caster = caster;
            this.target = target;
        }

        @Override
        public void run() {
            Location loc2 = target.getLocation();
            loc2.setY(loc2.getY() + 2.0D);
            this.caster.teleport(loc2);
            caster.getWorld().createExplosion(caster.getLocation(), 0.0f);
        }
    }

    static class TeleportChargeManager {
        private static final int MAX_CHARGES = 2;
        private static final int CHARGE_COOLDOWN_SECONDS = 120;

        private int charges = MAX_CHARGES;
        private int remainingCooldown = 0;
        private BukkitRunnable cooldownTask;

        public boolean canUse() {
            return charges > 0;
        }

        public void use() {
            if (charges > 0) {
                charges--;
                // cooldownTask가 null이거나 취소된 경우에만 새로 시작
                if (charges < MAX_CHARGES && (cooldownTask == null || cooldownTask.isCancelled())) {
                    startCooldown();
                }
            }
        }

        public int getCurrentCharges() {
            return charges;
        }

        public int getTimeToNextCharge() {
            return remainingCooldown;
        }

        public void reset() {
            charges = MAX_CHARGES;
            remainingCooldown = 0;
            if (cooldownTask != null && !cooldownTask.isCancelled()) {
                cooldownTask.cancel();
                cooldownTask = null;
            }
        }

        private void startCooldown() {
            remainingCooldown = CHARGE_COOLDOWN_SECONDS;
            cooldownTask = new BukkitRunnable() {
                @Override
                public void run() {
                    remainingCooldown--;
                    if (remainingCooldown <= 0) {
                        charges++;
                        if (charges < MAX_CHARGES) {
                            remainingCooldown = CHARGE_COOLDOWN_SECONDS;
                        } else {
                            // 최대 충전량에 도달하면 태스크 종료
                            this.cancel();
                            cooldownTask = null;
                        }
                    }
                }
            };
            cooldownTask.runTaskTimer(plugin, 0L, 20L);
        }
    }

}