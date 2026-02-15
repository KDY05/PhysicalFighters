package io.github.kdy05.physicalFighters.command

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun CommandSender.sendMessages(vararg messages: String) {
    for (msg in messages) sendMessage(msg)
}

inline fun playerCommand(sender: CommandSender, action: (Player) -> Unit) {
    if (sender !is Player) {
        sender.sendMessage("콘솔에서는 사용할 수 없습니다.")
        return
    }
    action(sender)
}

fun requireOnlinePlayer(sender: CommandSender, name: String): Player? {
    return Bukkit.getPlayerExact(name) ?: run {
        sender.sendMessage("${ChatColor.RED}존재하지 않는 플레이어입니다.")
        null
    }
}
