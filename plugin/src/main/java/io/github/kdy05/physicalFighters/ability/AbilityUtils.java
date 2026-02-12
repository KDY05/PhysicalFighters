package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.util.AttributeUtils;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class AbilityUtils {
    // 인스턴스화 방지용
    private AbilityUtils() {
        throw new AssertionError();
    }

    public static Ability findAbility(Player p) {
        return AbilityRegistry.findAbility(p);
    }

    public static Location getTargetLocation(Player p, int bound) {
        RayTraceResult result = p.getWorld().rayTraceBlocks(
                p.getEyeLocation(), p.getEyeLocation().getDirection(),
                bound, FluidCollisionMode.ALWAYS);
        return result != null && result.getHitBlock() != null
                ? result.getHitBlock().getLocation() : null;
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
        double maxHealthValue = AttributeUtils.getMaxHealth(entity);
        entity.setHealth(Math.min(maxHealthValue, entity.getHealth() + amount));
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

    public static void splashDamage(Player caster, Location location, double bound, double damage) {
        caster.getWorld().getNearbyEntities(location, bound, bound, bound).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity != caster)
                .filter(entity -> isNotSameTeam(caster, entity))
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
                .filter(entity -> isNotSameTeam(caster, entity))
                .filter(filter)
                .forEach(action);
    }

    private static boolean isNotSameTeam(Player caster, LivingEntity target) {
        if (!(target instanceof Player)) {
            return true;
        }
        Player targetPlayer = (Player) target;

        if (Bukkit.getScoreboardManager() == null) {
            return true;
        }
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        Team casterTeam = scoreboard.getEntryTeam(caster.getName());
        Team targetTeam = scoreboard.getEntryTeam(targetPlayer.getName());
        if (casterTeam == null || targetTeam == null) {
            return true;
        }
        return !casterTeam.equals(targetTeam);
    }

}
