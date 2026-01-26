package io.github.kdy05.physicalFighters.util;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class AbilityUtils {
    // 인스턴스화 방지용
    private AbilityUtils() {
        throw new AssertionError();
    }

    public static Ability findAbility(Player p) {
        for (Ability a : AbilityInitializer.AbilityList)
            if (a.isOwner(p)) return a;
        return null;
    }

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
        double maxHealthValue = AttributeUtils.getMaxHealth(entity);
        entity.setHealth(Math.min(maxHealthValue, entity.getHealth() + amount));
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

    public static void assignAbility(CommandSender sender, int abicode, Player target, boolean abilityOverLap) {
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
        if (abilityOverLap) {
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
        if (type == null) {
            return "Unknown";
        } else if (type == Ability.Type.Active_Continue) {
            return ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "지속" + ChatColor.WHITE;
        } else if (type == Ability.Type.Active_Immediately) {
            return ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "즉발" + ChatColor.WHITE;
        } else if (type == Ability.Type.Passive_AutoMatic) {
            return ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "자동" + ChatColor.WHITE;
        } else if (type == Ability.Type.Passive_Manual) {
            return ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "수동" + ChatColor.WHITE;
        } else {
            return "Unknown";
        }
    }

    public static void showInfo(Player player, boolean abilityOverLap) {
        Ability ability;
        if (AbilityInitializer.assimilation.getPlayer() == player) {
            ability = AbilityInitializer.assimilation;
        } else {
            ability = findAbility(player);
        }
        if (ability == null) {
            player.sendMessage(ChatColor.RED + "능력이 없거나 옵저버입니다.");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "---------------");
        player.sendMessage(ChatColor.GOLD + "- 능력 정보 -");
        if (abilityOverLap)
            player.sendMessage(ChatColor.DARK_AQUA + "참고 : 능력 리스트중 가장 상단의 능력만 보여줍니다.");
        player.sendMessage(ChatColor.AQUA + ability.getAbilityName() + ChatColor.WHITE
                + " [" + getTypeText(ability) + "] " + ability.getRank());
        for (int l = 0; l < ability.getGuide().length; l++) {
            player.sendMessage(ability.getGuide()[l]);
        }
        player.sendMessage(getTimerText(ability));
        player.sendMessage(ChatColor.GREEN + "---------------");
    }

    private static String getTimerText(Ability ability) {
        Ability.Type type = ability.getAbilityType();
        if (type == null) {
            return "None";
        } else if (type == Ability.Type.Active_Continue) {
            return String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "%d초", ability.getCoolDown(), ability.getDuration());
        } else if (type == Ability.Type.Active_Immediately) {
            return String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음", ability.getCoolDown());
        } else if (type == Ability.Type.Passive_AutoMatic || type == Ability.Type.Passive_Manual) {
            return ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "없음 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음";
        } else {
            return "None";
        }
    }
}
