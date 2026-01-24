package io.github.kdy05.physicalFighters.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface CommandInterface {
    boolean onCommandEvent(CommandSender sender, Command command, String label, String[] args);
    
    default boolean filterConsole(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.");
            return true;
        }
        return false;
    }
}
