package io.github.kdy05.physicalFighters.game

import io.github.kdy05.physicalFighters.PhysicalFighters
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import java.io.File
import java.io.IOException
import java.util.*

class BaseKitManager(private val plugin: PhysicalFighters) : Listener {

    private val editingInventories = mutableMapOf<UUID, Boolean>()
    private val dataFile = File(plugin.dataFolder, FILE_NAME)
    private var basicItems = arrayOfNulls<ItemStack>(27)

    init {
        loadFromFile()
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private fun loadFromFile() {
        if (!dataFile.exists()) {
            plugin.logger.info("기본 아이템 설정 파일이 없습니다. 새로 생성됩니다.")
            return
        }
        val config = YamlConfiguration.loadConfiguration(dataFile)
        for (i in 0 until 27) {
            basicItems[i] = config.getItemStack("items.$i")
        }
        plugin.logger.info("기본 아이템 설정을 불러왔습니다.")
    }

    private fun saveToFile() {
        val config = YamlConfiguration()
        for (i in 0 until 27) {
            basicItems[i]?.let { config.set("items.$i", it) }
        }
        try {
            config.save(dataFile)
        } catch (e: IOException) {
            plugin.logger.severe("기본 아이템 저장 실패: ${e.message}")
        }
    }

    fun setKitbyPreset(code: Int) {
        basicItems = arrayOfNulls(27)
        when (code) {
            0 -> {
                basicItems[0] = ItemStack(Material.IRON_INGOT, 64)
                basicItems[1] = ItemStack(Material.GOLD_INGOT, 64)
                basicItems[2] = ItemStack(Material.LAPIS_LAZULI, 64)
                basicItems[3] = ItemStack(Material.OAK_LOG, 64)
                basicItems[4] = ItemStack(Material.COOKED_BEEF, 64)
            }
            1 -> {
                basicItems[0] = ItemStack(Material.IRON_PICKAXE)
                basicItems[1] = ItemStack(Material.ENCHANTING_TABLE)
                basicItems[2] = ItemStack(Material.BOOKSHELF, 64)
                basicItems[3] = ItemStack(Material.LAPIS_LAZULI, 64)
                basicItems[4] = ItemStack(Material.GRINDSTONE)
                basicItems[5] = ItemStack(Material.COOKED_BEEF, 64)
            }
        }
        saveToFile()
    }

    fun openBasicItemGUI(player: Player) {
        val gui = Bukkit.createInventory(null, 27, "§6§l기본 아이템 설정")
        for (i in 0 until 27) {
            basicItems[i]?.let { gui.setItem(i, it.clone()) }
        }
        editingInventories[player.uniqueId] = true
        player.openInventory(gui)
    }

    fun giveBasicItems(player: Player) {
        for (item in basicItems) {
            item?.let { player.inventory.addItem(it.clone()) }
        }
        player.sendMessage("§a기본 아이템이 지급되었습니다!")
    }

    private fun saveBasicItems(player: Player, inventory: org.bukkit.inventory.Inventory) {
        for (i in 0 until 27) {
            basicItems[i] = inventory.getItem(i)
        }
        saveToFile()
        player.sendMessage("§a기본 아이템 설정이 저장되었습니다!")
        plugin.logger.info("${player.name}님이 기본 아이템 설정을 변경했습니다.")
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        if (editingInventories.remove(player.uniqueId) == null) return
        saveBasicItems(player, event.inventory)
    }

    companion object {
        private const val FILE_NAME = "basekits.yml"
    }
}