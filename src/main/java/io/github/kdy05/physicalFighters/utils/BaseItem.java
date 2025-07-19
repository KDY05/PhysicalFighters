package io.github.kdy05.physicalFighters.utils;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

/**
 * 기본 아이템을 관리하는 능력들을 위한 인터페이스
 */
public interface BaseItem {

    // 이벤트 상수
    int ITEM_DROP_EVENT = 1001;
    int ITEM_RESPAWN_EVENT = 1002;
    int ITEM_DEATH_EVENT = 1003;

    /**
     * 기본 아이템들 - 구현 클래스에서 반드시 구현
     */
    ItemStack[] getBaseItem();

    /**
     * 아이템 이름 - 구현 클래스에서 반드시 구현
     */
    String getItemName();

    /**
     * 능력 소유자인지 확인 (Ability 클래스의 메서드)
     */
    boolean isOwner(Entity e);

    /**
     * 플레이어 반환 (Ability 클래스의 메서드)
     */
    Player getPlayer();

    /**
     * 기본 아이템 이벤트 등록
     */
    default void registerBaseItemEvents() {
        if (this instanceof Ability ability) {
            EventManager.onPlayerDropItem.add(new EventData(ability, ITEM_DROP_EVENT));
            EventManager.onPlayerRespawn.add(new EventData(ability, ITEM_RESPAWN_EVENT));
            EventManager.onEntityDeath.add(new EventData(ability, ITEM_DEATH_EVENT));
        }
    }

    /**
     * 아이템 드롭 조건 처리
     */
    default int handleItemDropCondition(Event event) {
        PlayerDropItemEvent dropEvent = (PlayerDropItemEvent) event;
        if (!isOwner(dropEvent.getPlayer())) {
            return -1;
        }

        Material droppedType = dropEvent.getItemDrop().getItemStack().getType();
        for (ItemStack baseItem : getBaseItem()) {
            if (baseItem.getType() == droppedType) {
                PlayerInventory inv = dropEvent.getPlayer().getInventory();
                if (inv.contains(droppedType, baseItem.getAmount())) break;
                dropEvent.getPlayer().sendMessage(ChatColor.RED + getItemName() + "(은/는) 버릴 수 없습니다.");
                dropEvent.setCancelled(true);
                return -1;
            }
        }

        return -1;
    }

    /**
     * 아이템 리스폰 조건 처리
     */
    default int handleItemRespawnCondition(Event event) {
        PlayerRespawnEvent respawnEvent = (PlayerRespawnEvent) event;
        if (isOwner(respawnEvent.getPlayer())) {
            Player player = respawnEvent.getPlayer();
            player.sendMessage(ChatColor.GREEN + getItemName() + "(이/가) 지급됩니다.");
            giveBaseItem(player);
        }
        return -1;
    }

    /**
     * 플레이어 사망 조건 처리
     */
    default int handleItemDeathCondition(Event event) {
        EntityDeathEvent deathEvent = (EntityDeathEvent) event;
        if (isOwner(deathEvent.getEntity())) {
            List<ItemStack> itemlist = deathEvent.getDrops();
            for (ItemStack stack : getBaseItem()) {
                Material material = stack.getType();
                itemlist.removeIf(item -> item.getType() == material);
            }
        }
        return -1;
    }

    /**
     * 기본 아이템들 지급
     */
    default void giveBaseItem(Player player) {
        ItemStack[] baseItems = getBaseItem();
        if (baseItems == null) return;

        Inventory inventory = player.getInventory();
        for (ItemStack item : baseItems) {
            if (item == null) continue;
            for (int slot = 0; slot < 36; slot++) {
                if (inventory.getItem(slot) == null) {
                    inventory.setItem(slot, item.clone());
                    break;
                }
            }
        }
    }

    /**
     * 기본 아이템들 제거
     */
    default void removeBaseItem(Player player) {
        ItemStack[] baseItems = getBaseItem();
        if (baseItems == null) return;
        for (ItemStack item : baseItems) {
            if (item != null) {
                player.getInventory().removeItem(item);
            }
        }
    }

}