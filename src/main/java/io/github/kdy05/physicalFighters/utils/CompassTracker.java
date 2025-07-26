package io.github.kdy05.physicalFighters.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompassTracker implements Listener {

    private final JavaPlugin plugin;
    private static final String GUI_TITLE = ChatColor.GOLD + "플레이어 추적";
    private static final int UPDATE_INTERVAL = 20; // 1초마다 업데이트 (20 ticks)
    
    // 플레이어와 추적 대상을 매핑
    private final Map<Player, Player> trackingMap = new HashMap<>();
    private BukkitTask updateTask;

    public CompassTracker(JavaPlugin plugin) {
        this.plugin = plugin;
        registerEvents();
        startTrackingUpdate();
    }

    // 커스텀 InventoryHolder로 우리 GUI 식별
    private static class CompassGUIHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void startTrackingUpdate() {
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateCompassTargets();
            }
        }.runTaskTimer(plugin, 0, UPDATE_INTERVAL);
    }

    private void updateCompassTargets() {
        trackingMap.entrySet().removeIf(entry -> {
            Player tracker = entry.getKey();
            Player target = entry.getValue();
            
            // 추적자나 대상이 오프라인이면 제거
            if (!tracker.isOnline() || !target.isOnline()) {
                return true;
            }
            
            // 나침반 위치 업데이트
            tracker.setCompassTarget(target.getLocation());
            return false;
        });
    }

    public void stopTracking() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        trackingMap.clear();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // 나침반 우클릭 체크
        if (item.getType() != Material.COMPASS) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // GUI 열기
        openPlayerSelector(player);
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player clicker)) return;

        Inventory inventory = event.getInventory();

        // 우리가 만든 GUI인지 확인 (InventoryHolder로 체크)
        if (!(inventory.getHolder() instanceof CompassGUIHolder)) return;

        event.setCancelled(true); // 아이템 이동 방지

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() != Material.PLAYER_HEAD) return;

        // 플레이어 머리의 메타데이터에서 플레이어 이름 추출
        SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
        if (skullMeta == null || skullMeta.getOwningPlayer() == null) return;

        String targetName = skullMeta.getOwningPlayer().getName();
        Player target = null;
        if (targetName != null) {
            target = Bukkit.getPlayer(targetName);
        }

        if (target == null) {
            clicker.sendMessage(ChatColor.RED + "해당 플레이어를 찾을 수 없습니다.");
            clicker.closeInventory();
            return;
        }

        if (target.equals(clicker)) {
            clicker.sendMessage(ChatColor.RED + "자신을 추적할 수 없습니다.");
            clicker.closeInventory();
            return;
        }

        // 추적 시작
        trackingMap.put(clicker, target);
        clicker.setCompassTarget(target.getLocation());

        clicker.sendMessage(ChatColor.GREEN + "나침반이 " + ChatColor.YELLOW + target.getName() +
                ChatColor.GREEN + "을(를) 추적하기 시작했습니다!");

        clicker.closeInventory();
    }

    private void openPlayerSelector(Player player) {
        List<Player> onlinePlayers = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.equals(player)) { // 자신 제외
                onlinePlayers.add(p);
            }
        }

        if (onlinePlayers.isEmpty()) {
            player.sendMessage(ChatColor.RED + "추적할 수 있는 플레이어가 없습니다.");
            return;
        }

        // 9x3 인벤토리 생성 (27칸) - 커스텀 InventoryHolder 사용
        Inventory gui = Bukkit.createInventory(new CompassGUIHolder(), 27, GUI_TITLE);

        // 플레이어 머리 아이템들 추가
        for (int i = 0; i < Math.min(onlinePlayers.size(), 27); i++) {
            Player target = onlinePlayers.get(i);
            ItemStack playerHead = createPlayerHead(target);
            gui.setItem(i, playerHead);
        }

        player.openInventory(gui);
    }

    private ItemStack createPlayerHead(Player target) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(target);
            meta.setDisplayName(ChatColor.GREEN + target.getName());
            meta.setLore(List.of(
                    ChatColor.GRAY + "클릭하여 " + target.getName() + "을(를) 추적"
            ));
            head.setItemMeta(meta);
        }

        return head;
    }

    @EventHandler
    public void onCompassReset(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.COMPASS) return;
        if (!player.isSneaking()) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // 추적 중지
        trackingMap.remove(player);
        player.setCompassTarget(player.getWorld().getSpawnLocation());
        player.sendMessage(ChatColor.GREEN + "나침반 추적이 중지되고 스폰 지점으로 리셋되었습니다.");

        event.setCancelled(true);
    }

}