package io.github.kdy05.physicalFighters.core;

import io.github.kdy05.physicalFighters.util.CommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class CommandManager implements CommandExecutor, TabCompleter {

    private static final List<String> BASIC_COMMANDS = Arrays.asList("help", "check", "yes", "no");
    private static final List<String> OPERATOR_COMMANDS = Arrays.asList("start", "stop", "skip", "ob", "ablist",
            "abi", "util", "inv", "hung", "dura", "tc", "book", "scan", "reload", "kit");

    private final List<CommandInterface> handlers;

    private CommandManager(List<CommandInterface> handlers) {
        this.handlers = Collections.unmodifiableList(new ArrayList<>(handlers));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<CommandInterface> handlers = new ArrayList<>();

        private Builder() {}

        public Builder addCommand(CommandInterface handler) {
            handlers.add(handler);
            return this;
        }

        public Builder addAll(Collection<? extends CommandInterface> commands) {
            handlers.addAll(commands);
            return this;
        }

        public CommandManager build() {
            return new CommandManager(handlers);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!command.getName().equals("va")) return false;

        if (args.length != 0) {
            for (CommandInterface handler : this.handlers) {
                if (handler.onCommandEvent(sender, command, label, args)) {
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "알 수 없는 명령어입니다.");
        } else {
            String[] helpArgs = {"help"};
            for (CommandInterface handler : this.handlers) {
                if (handler.onCommandEvent(sender, command, label, helpArgs)) {
                    return true;
                }
            }
            sender.sendMessage(ChatColor.GREEN + "/va help" + ChatColor.WHITE + " 명령어로 도움말을 확인하세요.");
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {

        if (!command.getName().equals("va")) return null;

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> availableCommands = new ArrayList<>(BASIC_COMMANDS);
            if (sender.hasPermission("va.operate")) {
                availableCommands.addAll(OPERATOR_COMMANDS);
            }
            
            String input = args[0].toLowerCase();
            for (String cmd : availableCommands) {
                if (cmd.toLowerCase().startsWith(input)) {
                    completions.add(cmd);
                }
            }
        } else if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("abi") && sender.hasPermission("va.operate")) {
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
