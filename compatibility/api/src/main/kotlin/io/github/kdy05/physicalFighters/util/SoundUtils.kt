package io.github.kdy05.physicalFighters.util

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object SoundUtils {

    @JvmStatic
    fun playBreakSound(entity: Entity) {
        entity.world.playSound(entity.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
    }

    @JvmStatic
    fun playSuccessSound(player: Player?) {
        player?.playSound(player.location, Sound.ENTITY_ARROW_HIT_PLAYER, 0.4f, 1f)
    }

    @JvmStatic
    fun playErrorSound(player: Player?) {
        player?.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.2f, 1f)
    }

    @JvmStatic
    fun broadcastWarningSound() {
        Bukkit.getOnlinePlayers().forEach { player -> player.playSound(player.location,
            Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f) }
    }

    @JvmStatic
    fun playShieldSound(player: Player?) {
        player?.world?.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1f, 1f)
    }
}