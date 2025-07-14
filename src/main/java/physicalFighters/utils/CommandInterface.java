package physicalFighters.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandInterface {
    boolean onCommandEvent(CommandSender sender, Command command, String label, String[] args);
}
