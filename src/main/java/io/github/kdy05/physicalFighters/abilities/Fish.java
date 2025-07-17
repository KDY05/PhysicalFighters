package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.BaseItem;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

// TODO: 낚시로 얻을 수 없는 전용 물고기를 구현

public class Fish extends Ability implements BaseItem {
    // 능력 설정 상수
    private static final double FISHING_ROD_DAMAGE = 6.0;
    private static final double FISH_DAMAGE = 9.0;
    private static final double FISH_DROP_RATE = 0.03;

    public Fish() {
        InitAbility("강태공", Type.Passive_Manual, Rank.A,
                Usage.Passive + "낚싯대로 타격 시 강한 데미지를 주고, 낮은 확률로 전용 물고기를 얻습니다.",
                "물고기를 들고 타격 시, 더욱 강한 데미지를 줍니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
                if (!isOwner(damageEvent.getDamager())) break;
                if (isValidItem(Material.FISHING_ROD)) return 0;
                if (isValidItem(Material.COD)) return 1;
            }
            case ITEM_DROP_EVENT -> {
                return handleItemDropCondition(event);
            }
            case ITEM_RESPAWN_EVENT -> {
                return handleItemRespawnCondition(event);
            }
            case ITEM_DEATH_EVENT -> {
                return handleItemDeathCondition(event);
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
                damageEvent.setDamage(damageEvent.getDamage() * FISHING_ROD_DAMAGE);
                if (Math.random() < FISH_DROP_RATE) {
                    getPlayer().getInventory().addItem(new ItemStack(Material.COD));
                }
            }
            case 1 -> {
                EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
                damageEvent.setDamage(damageEvent.getDamage() * FISH_DAMAGE);
            }
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
