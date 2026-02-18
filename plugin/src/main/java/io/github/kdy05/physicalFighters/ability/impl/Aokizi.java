package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.EventData;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public final class Aokizi extends Ability {
    private static final double SLOW_CHANCE = 0.30;
    private static final int SLOW_DURATION = 60;
    private static final int SLOW_AMPLIFIER = 0;

    public Aokizi(UUID playerUuid) {
        super(AbilitySpec.builder("아오키지", Type.ActiveImmediately, Rank.S)
                .cooldown(1)
                .showText(ShowText.CustomText)
                .guide(Usage.IronLeft + "자신이 보고있는 방향으로 얼음을 날립니다.",
                        Usage.Passive + "자신이 공격한 적을 30% 확률로 3초간 느리게 만듭니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
        EventManager.registerEntityDamageByEntity(new EventData(this, 1));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent event0 = (PlayerInteractEvent) event;
            if (isOwner(event0.getPlayer()) && isValidItem(DefaultItem) && !InvincibilityManager.isDamageGuard())
                return 0;
        } else if (CustomData == 1) {
            EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) event;
            if (isOwner(event1.getDamager()) && Math.random() < SLOW_CHANCE
                    && event1.getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) event1.getEntity();
                entity.addPotionEffect(PotionEffectFactory.createSlowness(SLOW_DURATION, SLOW_AMPLIFIER));
            }
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Location l = event0.getPlayer().getLocation();
        Location l2 = event0.getPlayer().getLocation();
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));
        for (int i = 1; i < 10; i++) {
            l2.setX(l.getX() + (i + 1) * (Math.sin(degrees) * Math.cos(ydeg)));
            l2.setY(l.getY() + (i + 1) * Math.sin(ydeg));
            l2.setZ(l.getZ() + (i + 1) * (Math.cos(degrees) * Math.cos(ydeg)));
            Block block = event0.getPlayer().getWorld().getBlockAt(l2);
            if (block.getType() != Material.ICE)
                new ExplosionTimer(block.getType(), block).runTaskLater(plugin, 15L);
            block.setType(Material.ICE);
            AbilityUtils.splashDamage(event0.getPlayer(), block.getLocation(), 2.5, 8, true);
        }
    }

    static class ExplosionTimer extends BukkitRunnable {
        private final World world;
        private final Location location;
        private final Material blockid;

        ExplosionTimer(Material blockid, Block block) {
            this.world = block.getWorld();
            this.location = block.getLocation();
            this.blockid = blockid;
        }

        public void run() {
            this.world.getBlockAt(this.location).setType(this.blockid);
        }
    }
}
