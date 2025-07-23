package io.github.kdy05.physicalFighters.core;

import io.github.kdy05.physicalFighters.utils.CommandInterface;
import io.github.kdy05.physicalFighters.PhysicalFighters;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final LinkedList<CommandInterface> CommandEventHandler = new LinkedList<>();
    
    // 기본 명령어들
    private static final List<String> BASIC_COMMANDS = List.of("help", "check", "yes", "no");
    
    // 운영자 명령어들
    private static final List<String> OPERATOR_COMMANDS = List.of("start", "stop", "skip", "ob", "ablist",
            "abi", "util", "inv", "hung", "dura", "tc", "book", "scan", "reload", "kit");

    public CommandManager(PhysicalFighters plugin) {
        Objects.requireNonNull(plugin.getCommand("va")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("va")).setTabCompleter(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] data) {
        if (command.getName().equals("va")) {
            if (data.length != 0) {
                for (CommandInterface handler : this.CommandEventHandler) {
                    if (handler.onCommandEvent(sender, command, label, data)) {
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED + "알 수 없는 명령어입니다.");
            } else {
                // /va만 입력했을 때는 help 명령어로 리다이렉트
                String[] helpArgs = {"help"};
                for (CommandInterface handler : this.CommandEventHandler) {
                    if (handler.onCommandEvent(sender, command, label, helpArgs)) {
                        return true;
                    }
                }
                // 만약 help 처리가 실패하면 기본 메시지
                sender.sendMessage(ChatColor.GREEN + "/va help" + ChatColor.WHITE + " 명령어로 도움말을 확인하세요.");
                return true;
            }
        }
        return false;
    }

    public void registerCommand(CommandInterface EventHandler) {
        CommandEventHandler.add(EventHandler);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!command.getName().equals("va")) {
            return null;
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 첫 번째 인수: 서브커맨드들
            List<String> availableCommands = new ArrayList<>(BASIC_COMMANDS);
            
            // 운영자 권한이 있으면 운영자 명령어도 추가
            if (sender.hasPermission("va.operate")) {
                availableCommands.addAll(OPERATOR_COMMANDS);
            }
            
            // 입력과 일치하는 것들만 필터링
            String input = args[0].toLowerCase();
            for (String cmd : availableCommands) {
                if (cmd.toLowerCase().startsWith(input)) {
                    completions.add(cmd);
                }
            }
        } else if (args.length == 2) {
            // 두 번째 인수: 특정 명령어에 따른 자동완성
            String subcommand = args[0].toLowerCase();
            
            if (subcommand.equals("abi") && sender.hasPermission("va.operate")) {
                // 플레이어 이름들
                String input = args[1].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(input)) {
                        completions.add(player.getName());
                    }
                }
            }
        }

        return completions;
    }
}
