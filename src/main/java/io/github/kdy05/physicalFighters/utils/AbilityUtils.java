package io.github.kdy05.physicalFighters.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class AbilityUtils {

    @Nullable
    public static Ability findAbility(Player p) {
        for (Ability a : AbilityInitializer.AbilityList)
            if (a.isOwner(p)) return a;
        return null;
    }

    @Nullable
    public static Location getTargetLocation(Player p, int bound) {
        Location location = null;
        Location eyeLoc = p.getEyeLocation();
        for (double distance = 0.5; distance <= bound; distance += 0.5) {
            Location checkLoc = eyeLoc.clone().add(eyeLoc.getDirection().multiply(distance));
            Block checkBlock = p.getWorld().getBlockAt(checkLoc);
            if (checkBlock.getType().isSolid() || checkBlock.getType() == Material.WATER) {
                location = checkBlock.getLocation().clone();
                break;
            }
        }
        return location;
    }

    public static void goVelocity(LivingEntity entity, Location target, double value) {
        entity.setVelocity(entity.getVelocity().add(target.toVector()
                        .subtract(entity.getLocation().toVector()).normalize()
                        .multiply(value)));
    }

    public static void piercingDamage(LivingEntity entity, double damage) {
        entity.setHealth(Math.max(0, entity.getHealth() - damage));
    }

    public static void healEntity(LivingEntity entity, double amount) {
        AttributeInstance maxHealth = entity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth == null) return;
        double maxHealthValue = maxHealth.getValue();
        entity.setHealth(Math.min(maxHealthValue, entity.getHealth() + amount));
    }

    public static void splashDamage(Player caster, Location location, double bound, double damage) {
        caster.getWorld().getNearbyEntities(location, bound, bound, bound).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity != caster)
                .forEach(entity -> entity.damage(damage, caster));
    }

    public static void splashTask(Player caster, Location location, double bound, Consumer<LivingEntity> action) {
        splashTask(caster, location, bound, entity -> true, action);
    }

    public static void splashTask(Player caster, Location location, double bound,
                                  Predicate<LivingEntity> filter, Consumer<LivingEntity> action) {
        caster.getWorld().getNearbyEntities(location, bound, bound, bound).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity != caster)
                .filter(filter)
                .forEach(action);
    }

    public static void createBox(Location center, Material material, int radius, int height) {
        createBox(center, material, radius, height, false);
    }

    public static void createBox(Location center, Material material, int radius, int height, boolean ignoreBedrock) {
        World world = center.getWorld();
        if (world == null) return;
        for (int y = 1; y <= height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    Location blockLoc = center.clone().add(x, y, z);
                    Block block = world.getBlockAt(blockLoc);
                    if (ignoreBedrock || block.getType() != Material.BEDROCK) {
                        block.setType(material);
                    }
                }
            }
        }
    }

}
