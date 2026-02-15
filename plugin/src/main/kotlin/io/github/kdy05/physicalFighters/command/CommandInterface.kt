package io.github.kdy05.physicalFighters.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

fun interface CommandInterface {
    fun onCommandEvent(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean
}
