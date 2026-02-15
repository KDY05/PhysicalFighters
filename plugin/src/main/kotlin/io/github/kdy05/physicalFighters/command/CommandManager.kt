package io.github.kdy05.physicalFighters.command

import io.github.kdy05.physicalFighters.ability.AbilityRegistry
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class CommandManager(vararg handlers: CommandInterface) : CommandExecutor, TabCompleter {

    private val handlers: List<CommandInterface> = handlers.toList()

    companion object {
        private val BASIC_COMMANDS = listOf("help", "check", "yes", "no")
        private val OPERATOR_COMMANDS = listOf(
            "start", "stop", "skip", "ob", "list",
            "assign", "reset", "util", "inv", "hung", "dura", "tc", "book", "scan", "reload", "kit"
        )
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name != "va") return false

        if (args.isNotEmpty()) {
            for (handler in handlers) {
                if (handler.onCommandEvent(sender, command, label, args)) return true
            }
            sender.sendMessage("${ChatColor.RED}알 수 없는 명령어입니다.")
        } else {
            val helpArgs = arrayOf("help")
            for (handler in handlers) {
                if (handler.onCommandEvent(sender, command, label, helpArgs)) return true
            }
            sender.sendMessage("${ChatColor.GREEN}/va help${ChatColor.WHITE} 명령어로 도움말을 확인하세요.")
            return true
        }

        return false
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command,
        alias: String, args: Array<String>
    ): List<String>? {
        if (command.name != "va") return null

        val completions = mutableListOf<String>()

        when (args.size) {
            1 -> {
                val available = BASIC_COMMANDS.toMutableList()
                if (sender.hasPermission("va.operate")) {
                    available.addAll(OPERATOR_COMMANDS)
                }
                val input = args[0].lowercase()
                available.filterTo(completions) { it.lowercase().startsWith(input) }
            }

            2 -> {
                val sub = args[0].lowercase()
                if ((sub == "assign" || sub == "reset") && sender.hasPermission("va.operate")) {
                    val input = args[1].lowercase()
                    Bukkit.getOnlinePlayers()
                        .filter { it.name.lowercase().startsWith(input) }
                        .mapTo(completions) { it.name }
                }
            }

            3 -> {
                if (args[0].lowercase() == "assign" && sender.hasPermission("va.operate")) {
                    val input = args[2].lowercase()
                    AbilityRegistry.getAllTypes()
                        .map { it.name.replace(' ', '_') }
                        .filterTo(completions) { it.lowercase().startsWith(input) }
                }
            }
        }

        return completions
    }
}
