package io.github.kdy05.physicalFighters.game;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
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

    public static void assignAbility(CommandSender sender, int abicode, Player target, boolean abilityOverLap) {
        // 특정 플레이어 능력 해제
        if (abicode == -1) {
            for (Ability ability : AbilityRegistry.AbilityList) {
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
        Ability ability = AbilityRegistry.AbilityList.get(abicode);
        if (abilityOverLap) {
            // 중복 모드에서 액티브 능력 중복은 불가함.
            if (ability.getAbilityType() == Ability.Type.Active_Continue ||
                    ability.getAbilityType() == Ability.Type.Active_Immediately) {
                for (Ability ab : AbilityRegistry.AbilityList) {
                    if ((ab.getAbilityType() == Ability.Type.Active_Continue || ab.getAbilityType() == Ability.Type.Active_Immediately)
                            && ab.isOwner(target)) {
                        ab.setPlayer(null, true);
                    }
                }
            }
        } else {
            for (Ability ab : AbilityRegistry.AbilityList) {
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

    public static void showInfo(Player player, boolean abilityOverLap) {
        Ability ability = findPrimaryAbility(player);
        if (ability == null) {
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

    private static Ability findPrimaryAbility(Player p) {
        for (Ability a : AbilityRegistry.AbilityList)
            if (a.isOwner(p) && a.isInfoPrimary()) return a;
        return null;
    }

    private static Ability findAbility(Player p) {
        for (Ability a : AbilityRegistry.AbilityList)
            if (a.isOwner(p)) return a;
        return null;
    }
}
