package io.github.kdy05.physicalFighters.util

import io.github.kdy05.abilityAPI.rank.Rank as ApiRank
import org.bukkit.ChatColor

enum class Rank(private val s: String) {
    SSS("${ChatColor.DARK_PURPLE}Special Rank"),
    SS("${ChatColor.GOLD}SS Rank"),
    S("${ChatColor.RED}S Rank"),
    A("${ChatColor.GREEN}A Rank"),
    B("${ChatColor.BLUE}B Rank"),
    C("${ChatColor.YELLOW}C Rank"),
    F("${ChatColor.BLACK}F Rank"),
    GOD("${ChatColor.WHITE}신");

    override fun toString(): String = "$s${ChatColor.WHITE}"
}

fun ApiRank.toDisplayString(): String =
    Rank.entries.firstOrNull { it.name == this.name }?.toString() ?: this.name