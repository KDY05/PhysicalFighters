package io.github.kdy05.physicalFighters.command;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.config.ConfigManager;
import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
import io.github.kdy05.physicalFighters.util.AbilityBook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class UtilCommand implements CommandInterface {

    private final PhysicalFighters plugin;
    private final ConfigManager configManager;

    public UtilCommand(PhysicalFighters plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public boolean onCommandEvent(CommandSender sender, Command command, String label, String[] args) {
        // 운영자 권한 필터
        if (!sender.hasPermission("va.operate")) {
            return false;
        }

        if (args[0].equalsIgnoreCase("util")) {
            handleUtil(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("inv")) {
            handleInv(sender, args);
            return true;
        } else if (args[0].equalsIgnoreCase("hung")) {
            handleHung();
            return true;
        } else if (args[0].equalsIgnoreCase("dura")) {
            handleDura();
            return true;
        } else if (args[0].equalsIgnoreCase("tc")) {
            handleTc(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("book")) {
            handleBook(sender, args);
            return true;
        } else if (args[0].equalsIgnoreCase("scan")) {
            handleScan(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("reload")) {
            handleReload(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("kit")) {
            handleKit(sender, args);
            return true;
        }

        return false;
    }

    private void handleUtil(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== 유틸리티 명령어 목록 ===");
        sender.sendMessage("");

        sender.sendMessage(ChatColor.YELLOW + "■ 게임 설정");
        sender.sendMessage(ChatColor.GOLD + "/va reload" + ChatColor.WHITE + " - 플러그인 설정(config.yml)을 다시 로드합니다.");
        sender.sendMessage(ChatColor.GOLD + "/va kit" + ChatColor.WHITE + " - 게임 시작 시 기본템을 설정합니다.");
        sender.sendMessage(ChatColor.GOLD + "/va inv [시간(분)]" + ChatColor.WHITE + " - 무적 모드를 토글하거나 지정 시간동안 무적을 시작합니다.");
        sender.sendMessage(ChatColor.GOLD + "/va hung" + ChatColor.WHITE + " - 배고픔 무한 모드를 토글합니다.");
        sender.sendMessage(ChatColor.GOLD + "/va dura" + ChatColor.WHITE + " - 내구도 무한 모드를 토글합니다.");
        sender.sendMessage("");

        sender.sendMessage(ChatColor.YELLOW + "■ 기타");
        sender.sendMessage(ChatColor.GOLD + "/va scan" + ChatColor.WHITE + " - 현재 능력자 목록을 확인합니다.");
        sender.sendMessage(ChatColor.GOLD + "/va tc" + ChatColor.WHITE + " - 모든 능력의 쿨타임과 지속시간을 초기화합니다.");
        sender.sendMessage(ChatColor.GOLD + "/va book [코드]" + ChatColor.WHITE + " - 능력 정보가 담긴 책을 생성합니다.");
        sender.sendMessage("");

        sender.sendMessage(ChatColor.AQUA + "==========================");
    }

    private void handleInv(CommandSender sender, String[] args) {
        if (args.length == 1) {
            plugin.getInvincibilityManager().toggle();
        } else if (args.length == 2) {
            try {
                int minutes = Integer.parseInt(args[1]);
                if (minutes <= 0) {
                    sender.sendMessage(ChatColor.RED + "시간은 1 이상의 숫자를 입력하세요.");
                    return;
                }
                plugin.getInvincibilityManager().startInvincibility(minutes);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "올바른 숫자를 입력하세요.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "명령어 사용법: /va inv [시간(분)]");
        }
    }

    private void handleHung() {
        if (!configManager.isNoFoodMode()) {
            configManager.setNoFoodMode(true);
            Bukkit.broadcastMessage(ChatColor.GREEN +
                    "OP에 의해 배고픔무한이 설정되었습니다.");
        } else {
            configManager.setNoFoodMode(false);
            Bukkit.broadcastMessage(ChatColor.RED +
                    "OP에 의해 배고픔무한이 해제되었습니다.");
        }
    }

    private void handleDura() {
        if (!configManager.isInfinityDur()) {
            configManager.setInfinityDur(true);
            Bukkit.broadcastMessage(ChatColor.GREEN +
                    "OP에 의해 내구도무한이 설정되었습니다.");
        } else {
            configManager.setInfinityDur(false);
            Bukkit.broadcastMessage(ChatColor.RED +
                    "OP에 의해 내구도무한이 해제되었습니다.");
        }
    }

    private void handleTc(CommandSender sender) {
        for (Ability a : AbilityRegistry.getActiveAbilities()) {
            a.cancelDTimer();
            a.cancelCTimer();
        }
        Bukkit.broadcastMessage(String.format(ChatColor.GRAY +
                "관리자 %s님이 쿨타임 및 지속시간을 초기화했습니다.", sender.getName()));
    }

    private void handleBook(CommandSender sender, String[] args) {
        if (filterConsole(sender)) return;
        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "명령이 올바르지 않습니다. [/va book [능력이름]]");
            return;
        }

        String abilityName = args[1];
        ItemStack stack = AbilityBook.create(abilityName);
        if (stack == null) {
            player.sendMessage(ChatColor.RED + "존재하지 않는 능력입니다.");
            return;
        }

        player.getInventory().addItem(stack);
        player.sendMessage("능력서를 만들었습니다. " + ChatColor.GOLD + abilityName);
    }

    private void handleScan(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "- 능력을 스캔했습니다. -");
        sender.sendMessage(ChatColor.GREEN + "---------------");
        int count = 0;
        for (Ability ability : AbilityRegistry.getActiveAbilities()) {
            Player temp = ability.getPlayer();
            if (temp == null) continue;
            sender.sendMessage(String.format(ChatColor.GREEN + "%d. " + ChatColor.WHITE +
                            "%s : " + ChatColor.RED + "%s " + ChatColor.WHITE +
                            "[" + ability.getAbilityType() + "]",
                    count, temp.getName(), ability.getAbilityName()));
            count++;
        }
        if (count == 0)
            sender.sendMessage("아직 능력자가 없습니다.");
        sender.sendMessage(ChatColor.GREEN + "---------------");
    }

    private void handleReload(CommandSender sender) {
        try {
            configManager.reloadConfigs();
            sender.sendMessage(ChatColor.GREEN + "플러그인 설정이 성공적으로 다시 로드되었습니다.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "설정 로드 중 오류가 발생했습니다: " + e.getMessage());
            plugin.getLogger().warning("설정 로드 실패: " + e.getMessage());
        }
    }

    private void handleKit(CommandSender sender, String[] args) {
        if (filterConsole(sender)) return;
        if (args.length == 2) {
            int code;
            try {
                code = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "코드가 올바르지 않습니다.");
                return;
            }
            plugin.getBaseKitManager().setKitbyPreset(code);
        }
        plugin.getBaseKitManager().openBasicItemGUI((Player) sender);
    }

}
