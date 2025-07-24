package io.github.kdy05.physicalFighters.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface CommandInterface {
    boolean onCommandEvent(CommandSender sender, Command command, String label, String[] args);
    
    default boolean requirePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.");
            return false;
        }
        return true;
    }
}
