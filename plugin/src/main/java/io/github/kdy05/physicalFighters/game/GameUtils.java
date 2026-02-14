package io.github.kdy05.physicalFighters.game;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
import io.github.kdy05.physicalFighters.ability.AbilityType;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class GameUtils {
    // 인스턴스화 방지용
    private GameUtils() {
        throw new AssertionError();
    }

    public static void assignAbility(CommandSender sender, String abilityName, Player target, boolean abilityOverLap) {
        AbilityType type = AbilityRegistry.getType(abilityName);
        if (type == null) {
            sender.sendMessage(ChatColor.RED + "존재하지 않는 능력입니다.");
            return;
        }

        // 기존 능력 해제
        if (abilityOverLap) {
            // 중복 모드에서 액티브 능력 중복은 불가함.
            if (type.getType() == Ability.Type.ActiveContinue ||
                    type.getType() == Ability.Type.ActiveImmediately) {
                for (Ability ab : AbilityRegistry.findAbilities(target)) {
                    if (ab.getAbilityType() == Ability.Type.ActiveContinue ||
                            ab.getAbilityType() == Ability.Type.ActiveImmediately) {
                        AbilityRegistry.deactivate(ab);
                    }
                }
            }
        } else {
            AbilityRegistry.deactivateAll(target);
        }

        // 새로운 능력 적용
        Ability ability = AbilityRegistry.createAndActivate(abilityName, target);
        sender.sendMessage(String.format(ChatColor.GREEN + "%s" + ChatColor.WHITE + "님에게 " +
                            ChatColor.GREEN + "%s" + ChatColor.WHITE + " 능력 할당이 완료되었습니다.",
                            Objects.requireNonNull(target).getName(), ability.getAbilityName()));
        String senderName = sender instanceof Player ? sender.getName() : "Console";
        PhysicalFighters.getPlugin().getLogger().info(String.format("%s님이 %s님에게 %s 능력을 할당했습니다.",
                senderName, target.getName(), ability.getAbilityName()));
    }

    public static void showInfo(Player player, boolean abilityOverLap) {
        Ability ability = AbilityRegistry.findPrimaryAbility(player);
        if (ability == null) {
            ability = AbilityRegistry.findAbility(player);
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
                + " [" + ability.getAbilityType() + "] " + ability.getRank());
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
        } else if (type == Ability.Type.ActiveContinue) {
            return String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "%d초", ability.getCoolDown(), ability.getDuration());
        } else if (type == Ability.Type.ActiveImmediately) {
            return String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음", ability.getCoolDown());
        } else if (type == Ability.Type.PassiveAutoMatic || type == Ability.Type.PassiveManual) {
            return ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "없음 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음";
        } else {
            return "None";
        }
    }

    /**
     * OnKill 설정에 따라 사망 처리를 실행합니다.
     * (관전자 모드 전환 / 킥 / 밴)
     */
    public static void applyDeathPenalty(Player victim) {
        PhysicalFighters plugin = PhysicalFighters.getPlugin();
        int onKill = plugin.getConfigManager().getOnKill();
        if (onKill <= 0) return;

        if (onKill == 1) {
            Location deathLocation = victim.getLocation().clone();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                victim.setGameMode(GameMode.SPECTATOR);
                victim.spigot().respawn();
                victim.teleport(deathLocation);
                victim.sendTitle(ChatColor.RED + "사망하였습니다!",
                        ChatColor.YELLOW + "관전자 모드로 전환합니다.", 10, 100, 10);
            }, 1L);
        } else if (onKill == 2) {
            victim.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.");
        } else if (onKill == 3) {
            if (!victim.isOp()) {
                Bukkit.getBanList(BanList.Type.NAME).addBan(victim.getName(),
                        "당신은 죽었습니다. 다시 들어오실 수 없습니다.", null, null);
                victim.kickPlayer("당신은 죽었습니다. 다시 들어오실 수 없습니다.");
            } else {
                victim.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.");
            }
        }
    }

}
