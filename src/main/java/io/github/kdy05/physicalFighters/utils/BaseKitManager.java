package io.github.kdy05.physicalFighters.utils;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BaseKitManager implements Listener {

    private final PhysicalFighters plugin;
    private final Map<Player, Inventory> editingInventories;

    // 기본 아이템 배열 (9x3 = 27칸)
    private final ItemStack[] basicItems;

    public BaseKitManager(PhysicalFighters plugin) {
        this.plugin = plugin;
        this.editingInventories = new HashMap<>();
        this.basicItems = new ItemStack[27]; // 9x3 크기

        initializeDefaultItems();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * 기본 아이템들 초기화
     */
    private void initializeDefaultItems() {
        basicItems[0] = new ItemStack(Material.ENCHANTING_TABLE);
        basicItems[1] = new ItemStack(Material.BOOKSHELF, 64);
        basicItems[2] = new ItemStack(Material.LAPIS_LAZULI, 64);
        basicItems[3] = new ItemStack(Material.GRINDSTONE);
        basicItems[4] = new ItemStack(Material.COOKED_BEEF, 64);
    }

    /**
     * 플레이어에게 기본 아이템 설정 GUI 열기
     */
    public void openBasicItemGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§l기본 아이템 설정");

        // 저장된 기본 아이템들을 GUI에 복사
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

    /**
     * 기본 아이템 배열 반환
     */
    public ItemStack[] getBasicItems() {
        return basicItems.clone();
    }

    /**
     * 기본 아이템 설정 저장
     */
    private void saveBasicItems(Player player, Inventory inventory) {
        // 인벤토리의 모든 아이템을 기본 아이템 배열에 저장
        for (int i = 0; i < 27; i++) {
            basicItems[i] = inventory.getItem(i);
        }

        player.sendMessage("§a기본 아이템 설정이 저장되었습니다!");
        plugin.getLogger().info(player.getName() + "님이 기본 아이템 설정을 변경했습니다.");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        // 편집 중인 인벤토리인지 확인
        if (!editingInventories.containsKey(player)) return;

        Inventory closedInventory = event.getInventory();

        // 인벤토리 내용을 저장
        saveBasicItems(player, closedInventory);

        // 편집 상태 제거
        editingInventories.remove(player);
    }
}