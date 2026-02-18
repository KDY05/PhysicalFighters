package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.EventData;
import io.github.kdy05.physicalFighters.util.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.FluidCollisionMode;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.UUID;

public final class Kijaru extends Ability {
    public Kijaru(UUID playerUuid) {
        super(AbilitySpec.builder("키자루", Type.ActiveImmediately, Rank.SS)
                .cooldown(45)
                .guide(Usage.IronAttack + "타격한 상대를 빛의 속도로 타격합니다.",
                        "상대는 엄청난 속도로 멀리 날라갑니다. 당신도 상대를 따라 근접하게 날라갑니다.",
                        Usage.IronRight + "바라보는 곳으로 순간이동합니다.",
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
    public int checkCondition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (isOwner(event0.getDamager()) && isValidItem(Ability.DefaultItem)
                    && !InvincibilityManager.isDamageGuard() && event0.getEntity() instanceof LivingEntity)
                return 0;
        } else if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            if (isOwner(event1.getPlayer()) && isValidItem(Ability.DefaultItem)) return 1;
        } else if (CustomData == 2) {
            EntityDamageEvent event2 = (EntityDamageEvent) event;
            if (isOwner(event2.getEntity()) && event2.getCause() == DamageCause.FALL) {
                SoundUtils.playSuccessSound(getPlayer());
                sendMessage(ChatColor.GREEN + "사뿐하게 떨어져 대미지를 받지 않았습니다.");
                event2.setCancelled(true);
            }
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            LivingEntity entity = (LivingEntity) event0.getEntity();
            if (getPlayer() == null) return;
            spawnParticlePath(getPlayer().getLocation(), entity.getLocation());
            AbilityUtils.goVelocity(entity, getPlayer().getLocation().clone().add(0, -0.25, 0), -4);
            new KizaruTask(getPlayer(), entity).runTaskLater(plugin, 20L);
            event0.setCancelled(true);
        } else if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            Player caster = event1.getPlayer();
            Location origin = caster.getEyeLocation();
            Vector direction = origin.getDirection().normalize();

            RayTraceResult result = caster.getWorld().rayTraceBlocks(
                    origin, direction, 80, FluidCollisionMode.NEVER, true);

            Location dest;
            if (result != null) {
                dest = result.getHitPosition().subtract(direction).toLocation(caster.getWorld());
            } else {
                dest = origin.clone().add(direction.multiply(80));
            }

            spawnParticlePath(origin, dest);
            dest.setPitch(caster.getLocation().getPitch());
            dest.setYaw(caster.getLocation().getYaw());
            caster.teleport(dest);
            caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
    }

    private void spawnParticlePath(Location from, Location to) {
        Vector direction = to.toVector().subtract(from.toVector()).normalize();
        double distance = from.distance(to);
        Particle.DustOptions dust = new Particle.DustOptions(Color.YELLOW, 1.0f);
        for (double d = 0; d <= distance; d += 0.5) {
            Objects.requireNonNull(from.getWorld()).spawnParticle(Particle.REDSTONE,
                    from.clone().add(direction.clone().multiply(d)),
                    1, 0, 0, 0, 0, dust);
        }
    }

    static class KizaruTask extends BukkitRunnable {
        final Player caster;
        final LivingEntity target;

        KizaruTask(Player caster, LivingEntity target) {
            this.caster = caster;
            this.target = target;
        }

        @Override
        public void run() {
            Location loc2 = target.getLocation();
            loc2.setY(loc2.getY() + 2.0D);
            this.caster.teleport(loc2);
            target.damage(8, caster);
            caster.getWorld().createExplosion(caster.getLocation(), 0.0f);
        }
    }
}
