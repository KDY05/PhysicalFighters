package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.BaseItem;
import io.github.kdy05.physicalFighters.util.EventData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Objects;

public class Fish extends Ability implements BaseItem {
    // 능력 설정 상수
    private static final double FISHING_ROD_DAMAGE = 6.0;
    private static final double FISH_DAMAGE = 8.0;
    private static final double FISH_DROP_RATE = 0.03;

    private final ItemStack fish = createFish();

    private ItemStack createFish() {
        ItemStack fish = new ItemStack(Material.COD);
        ItemMeta meta = fish.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.AQUA + "강태공의 물고기");
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "강태공 전용"));
        fish.setItemMeta(meta);
        return fish;
    }

    private boolean isFish(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || meta.getLore() == null) return false;
        return stack.getType().equals(Material.COD) && meta.getDisplayName().equals(ChatColor.AQUA + "강태공의 물고기")
                && meta.getLore().get(0).equals(ChatColor.GRAY + "강태공 전용");
    }

    public Fish() {
        InitAbility("강태공", Type.Passive_Manual, Rank.A,
                "낚싯대로 타격 시 강한 대미지를 주고, 낮은 확률로 전용 물고기를 얻습니다.",
                "물고기를 들고 타격 시, 더욱 강한 대미지를 줍니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            if (!isOwner(damageEvent.getDamager()) || !(damageEvent.getEntity() instanceof Player)) return -1;
            Player player = (Player) damageEvent.getDamager();

            if (isValidItem(Material.FISHING_ROD)) return 0;
            if (isFish(player.getInventory().getItemInMainHand())) return 1;
        } else if (CustomData == ITEM_DROP_EVENT) {
            return handleItemDropCondition(event);
        } else if (CustomData == ITEM_RESPAWN_EVENT) {
            return handleItemRespawnCondition(event);
        } else if (CustomData == ITEM_DEATH_EVENT) {
            return handleItemDeathCondition(event);
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            damageEvent.setDamage(damageEvent.getDamage() * FISHING_ROD_DAMAGE);
            if (Math.random() < FISH_DROP_RATE) {
                Objects.requireNonNull(getPlayer()).getInventory().addItem(fish);
            }
        } else if (CustomData == 1) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            damageEvent.setDamage(damageEvent.getDamage() * FISH_DAMAGE);
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        giveBaseItem(p);
    }

    @Override
    public void A_ResetEvent(Player p) {
        removeBaseItem(p);
    }

    @Override
    public ItemStack[] getBaseItem() {
        return new ItemStack[] {
                new ItemStack(Material.FISHING_ROD, 1)
        };
    }

    @Override
    public String getItemName() {
        return "낚싯대";
    }

}
