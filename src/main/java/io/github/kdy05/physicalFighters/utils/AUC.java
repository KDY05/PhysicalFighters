package io.github.kdy05.physicalFighters.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.AbilityList;
import io.github.kdy05.physicalFighters.PhysicalFighters;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class AUC {

    public static void showInfoText(Player p) {
        Ability ability;
        if (AbilityList.assimilation.getPlayer() == p) {
            ability = AbilityList.assimilation;
        } else {
            ability = findAbility(p);
        }
        if (ability == null) {
            p.sendMessage(ChatColor.RED + "능력이 없거나 옵저버입니다.");
            return;
        }
        p.sendMessage(ChatColor.GREEN + "---------------");
        p.sendMessage(ChatColor.GOLD + "- 능력 정보 -");
        if (PhysicalFighters.AbilityOverLap)
            p.sendMessage(ChatColor.DARK_AQUA + "참고 : 능력 리스트중 가장 상단의 능력만 보여줍니다.");
        p.sendMessage(ChatColor.AQUA + ability.getAbilityName() + ChatColor.WHITE
                + " [" + showTypeText(ability) + "] " + ability.getRank());
        for (int l = 0; l < ability.getGuide().length; l++) {
            p.sendMessage(ability.getGuide()[l]);
        }
        p.sendMessage(showTimerText(ability));
        p.sendMessage(ChatColor.GREEN + "---------------");
    }

    public static String showTypeText(Ability ability) {
        Ability.Type type = ability.getAbilityType();
        if (!ability.getRunAbility()) return ChatColor.RED + "능력 비활성화됨" + ChatColor.WHITE;
        if (type == Ability.Type.Active_Continue)
            return ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "지속" + ChatColor.WHITE;
        if (type == Ability.Type.Active_Immediately)
            return ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "즉발" + ChatColor.WHITE;
        if (type == Ability.Type.Passive_AutoMatic)
            return ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "자동" + ChatColor.WHITE;
        if (type == Ability.Type.Passive_Manual)
            return ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "수동" + ChatColor.WHITE;
        return "Unknown";
    }

    public static String showTimerText(Ability ability) {
        if (ability.getAbilityType() == Ability.Type.Active_Continue)
            return String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "%d초", ability.getCoolDown(), ability.getDuration());
        if (ability.getAbilityType() == Ability.Type.Active_Immediately)
            return String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음", ability.getCoolDown());
        if (ability.getAbilityType() == Ability.Type.Passive_AutoMatic)
            return ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "없음 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음";
        if (ability.getAbilityType() == Ability.Type.Passive_Manual)
            return ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "없음 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음";
        return "None";
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

    @Nullable
    public static Ability findAbility(Player p) {
        for (Ability a : AbilityList.AbilityList)
            if (a.isOwner(p)) return a;
        return null;
    }

    public static void goVelocity(LivingEntity entity, Location loc, int value) {
        entity.setVelocity(entity.getVelocity().add(loc.toVector()
                        .subtract(entity.getLocation().toVector()).normalize()
                        .multiply(value)));
    }

    public static boolean removeOneItem(Player player, Material material) {
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == material) {
                int amount = item.getAmount();
                if (amount > 1) {
                    item.setAmount(amount - 1);
                } else {
                    inventory.setItem(i, null);
                }
                return true;
            }
        }
        return false;
    }

    public static void splashDamage(Player caster, Location location, double bound, double damage) {
        caster.getWorld().getNearbyEntities(location, bound, bound, bound).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity != caster)
                .forEach(entity -> entity.damage(damage, caster));
    }

    public static void splashTask(Player caster, Location location, double bound, Consumer<LivingEntity> action) {
        caster.getWorld().getNearbyEntities(location, bound, bound, bound).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity != caster)
                .forEach(action);
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

}
