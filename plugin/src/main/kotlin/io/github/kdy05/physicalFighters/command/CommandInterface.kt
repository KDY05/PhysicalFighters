package io.github.kdy05.physicalFighters.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface CommandInterface {
    fun onCommandEvent(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean

    fun filterConsole(sender: CommandSender): Boolean {
        if (sender !is Player) {
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.")
            return true
        }
        return false
    }
}
