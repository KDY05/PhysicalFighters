package io.github.kdy05.physicalFighters.utils;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Objects;
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

    public static void assignAbility(CommandSender sender, int abicode, Player target) {
        // 특정 플레이어 능력 해제
        if (abicode == -1) {
            for (Ability ability : AbilityInitializer.AbilityList) {
                if (ability.isOwner(target)) {
                    ability.setPlayer(null, true);
                }
            }
            target.sendMessage(ChatColor.RED + "당신의 능력이 모두 해제되었습니다.");
            sender.sendMessage(String.format(ChatColor.GREEN + "%s" +
                    ChatColor.WHITE + "님의 능력을 모두 해제했습니다.", target.getName()));
            return;
        }

        // 기존 능력 해제
        Ability ability = AbilityInitializer.AbilityList.get(abicode);
        if (ConfigManager.AbilityOverLap) {
            // 중복 모드에서 액티브 능력 중복은 불가함.
            if (ability.getAbilityType() == Ability.Type.Active_Continue ||
                    ability.getAbilityType() == Ability.Type.Active_Immediately) {
                for (Ability ab : AbilityInitializer.AbilityList) {
                    if ((ab.getAbilityType() == Ability.Type.Active_Continue || ab.getAbilityType() == Ability.Type.Active_Immediately)
                            && ab.isOwner(target)) {
                        ab.setPlayer(null, true);
                    }
                }
            }
        } else {
            for (Ability ab : AbilityInitializer.AbilityList) {
                if (ab.isOwner(target)) {
                    ab.setPlayer(null, true);
                }
            }
        }

        // 새로운 능력 적용
        ability.setPlayer(target, true);
        ability.setRunAbility(true);
        sender.sendMessage(String.format(ChatColor.GREEN + "%s" + ChatColor.WHITE + "님에게 " +
                            ChatColor.GREEN + "%s" + ChatColor.WHITE + " 능력 할당이 완료되었습니다.",
                            Objects.requireNonNull(target).getName(), ability.getAbilityName()));
        String senderName = sender instanceof Player ? sender.getName() : "Console";
        PhysicalFighters.getPlugin().getLogger().info(String.format("%s님이 %s님에게 %s 능력을 할당했습니다.",
                senderName, target.getName(), ability.getAbilityName()));
    }

    public static String getTypeText(Ability ability) {
        Ability.Type type = ability.getAbilityType();
        return switch (type) {
            case Active_Continue ->
                    ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "지속" + ChatColor.WHITE;
            case Active_Immediately ->
                    ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "즉발" + ChatColor.WHITE;
            case Passive_AutoMatic ->
                    ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "자동" + ChatColor.WHITE;
            case Passive_Manual ->
                    ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "수동" + ChatColor.WHITE;
            case null -> "Unknown";
        };
    }
}
