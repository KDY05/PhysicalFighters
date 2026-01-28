package io.github.kdy05.physicalFighters.module;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class BaseKitManager implements Listener {

    private static final String FILE_NAME = "basekits.yml";

    private final PhysicalFighters plugin;
    private final Map<Player, Inventory> editingInventories;
    private final File dataFile;
    private FileConfiguration dataConfig;

    // 기본 아이템 배열 (9x3 = 27칸)
    private ItemStack[] basicItems;

    public BaseKitManager(PhysicalFighters plugin) {
        this.plugin = plugin;
        this.editingInventories = new HashMap<>();
        this.basicItems = new ItemStack[27];
        this.dataFile = new File(plugin.getDataFolder(), FILE_NAME);

        loadFromFile();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * 파일에서 기본 아이템 로드
     */
    private void loadFromFile() {
        if (!dataFile.exists()) {
            plugin.getLogger().info("기본 아이템 설정 파일이 없습니다. 새로 생성됩니다.");
            basicItems = new ItemStack[27];
            return;
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        for (int i = 0; i < 27; i++) {
            String path = "items." + i;
            if (dataConfig.contains(path)) {
                basicItems[i] = dataConfig.getItemStack(path);
            } else {
                basicItems[i] = null;
            }
        }
        plugin.getLogger().info("기본 아이템 설정을 불러왔습니다.");
    }

    /**
     * 기본 아이템을 파일에 저장
     */
    private void saveToFile() {
        dataConfig = new YamlConfiguration();
        for (int i = 0; i < 27; i++) {
            String path = "items." + i;
            if (basicItems[i] != null) {
                dataConfig.set(path, basicItems[i]);
            }
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("기본 아이템 저장 실패: " + e.getMessage());
        }
    }

    public void setKitbyPreset(int code) {
        basicItems = new ItemStack[27];
        if (code == 0) {
            basicItems[0] = new ItemStack(Material.IRON_INGOT, 64);
            basicItems[1] = new ItemStack(Material.GOLD_INGOT, 64);
            basicItems[2] = new ItemStack(Material.LAPIS_LAZULI, 64);
            basicItems[3] = new ItemStack(Material.OAK_LOG, 64);
            basicItems[4] = new ItemStack(Material.COOKED_BEEF, 64);
        }
        else if (code == 1) {
            ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
            basicItems[0] = pickaxe;
            basicItems[1] = new ItemStack(Material.ENCHANTING_TABLE);
            basicItems[2] = new ItemStack(Material.BOOKSHELF, 64);
            basicItems[3] = new ItemStack(Material.LAPIS_LAZULI, 64);
            basicItems[4] = new ItemStack(Material.GRINDSTONE);
            basicItems[5] = new ItemStack(Material.COOKED_BEEF, 64);
        }
        saveToFile();
    }

    /**
     * 플레이어에게 기본 아이템 설정 GUI 열기
     */
    public void openBasicItemGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§l기본 아이템 설정");
        for (int i = 0; i < 27; i++) {
            if (basicItems[i] != null) {
                gui.setItem(i, basicItems[i].clone());
            }
        }
        editingInventories.put(player, gui);
        player.openInventory(gui);
    }

    /**
     * 플레이어에게 기본 아이템 지급
     */
    public void giveBasicItems(Player player) {
        for (ItemStack item : basicItems) {
            if (item != null) {
                player.getInventory().addItem(item.clone());
            }
        }
        player.sendMessage("§a기본 아이템이 지급되었습니다!");
    }

    private void saveBasicItems(Player player, Inventory inventory) {
        // 인벤토리의 모든 아이템을 기본 아이템 배열에 저장
        for (int i = 0; i < 27; i++) {
            basicItems[i] = inventory.getItem(i);
        }
        saveToFile();
        player.sendMessage("§a기본 아이템 설정이 저장되었습니다!");
        plugin.getLogger().info(player.getName() + "님이 기본 아이템 설정을 변경했습니다.");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();

        if (!editingInventories.containsKey(player)) return;
        Inventory closedInventory = event.getInventory();
        saveBasicItems(player, closedInventory);
        editingInventories.remove(player);
    }
}