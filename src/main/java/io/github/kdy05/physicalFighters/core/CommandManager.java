package io.github.kdy05.physicalFighters.core;

import io.github.kdy05.physicalFighters.utils.CommandInterface;
import io.github.kdy05.physicalFighters.PhysicalFighters;

import java.util.LinkedList;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandManager implements CommandExecutor {
    private final LinkedList<CommandInterface> CommandEventHandler = new LinkedList<>();

    public CommandManager(PhysicalFighters plugin) {
        Objects.requireNonNull(plugin.getCommand("va")).setExecutor(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] data) {
        if (command.getName().equals("va")) {
            if (data.length != 0) {
                for (CommandInterface handler : this.CommandEventHandler) {
                    if (handler.onCommandEvent(sender, command, label, data)) {
                        return true;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.GREEN + "명령어 목록");
                sender.sendMessage(ChatColor.YELLOW + "[1]/va start : " + ChatColor.WHITE + "게임을 시작시킵니다.");
                sender.sendMessage(ChatColor.YELLOW + "[2]/va stop : " + ChatColor.WHITE + "게임을 중지시킵니다.");
                sender.sendMessage(ChatColor.YELLOW + "[3]/va check : " + ChatColor.WHITE + "능력을 확인합니다.");
                sender.sendMessage(ChatColor.YELLOW + "[4]/va ob : " + ChatColor.WHITE + "옵저버 설정을 합니다.");
                sender.sendMessage(ChatColor.YELLOW + "[5]/va uti : " + ChatColor.WHITE + "유틸리티 명령 목록을 보여줍니다.");
                sender.sendMessage(ChatColor.YELLOW + "[6]/va debug : " + ChatColor.WHITE + "오류 방어 명령 목록을 보여줍니다.");
                sender.sendMessage(ChatColor.YELLOW + "[7]/va go : " + ChatColor.WHITE + "무적시간을 스킵합니다.");
                sender.sendMessage(ChatColor.YELLOW + "[8]/va inv : " + ChatColor.WHITE + "무적으로 만듭니다.");
                sender.sendMessage(ChatColor.YELLOW + "[9]/va hung : " + ChatColor.WHITE + "배고픔을 설정합니다.");
                sender.sendMessage(ChatColor.YELLOW + "[0]/va dura : " + ChatColor.WHITE + "내구도무한을 설정합니다.");
                return true;
            }
        }
        return false;
    }

    public void RegisterCommand(CommandInterface EventHandler) {
        CommandEventHandler.add(EventHandler);
    }
}
