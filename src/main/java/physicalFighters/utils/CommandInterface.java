package physicalFighters.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandInterface {
    boolean onCommandEvent(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString);
}
