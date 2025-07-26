package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.utils.BaseItem;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Archer extends Ability implements BaseItem {
    public Archer() {
        InitAbility("아쳐", Type.Passive_Manual, Rank.A,
                "상대에게 쏘는 화살 대미지가 항상 3 상승합니다.",
                "60% 확률로 6초간 불을 붙이며, 40% 확률로 폭발을 일으킵니다.",
                "죽거나 게임 시작시 활과 화살이 고정적으로 주어집니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
                if (event0.getDamager() instanceof Arrow a && a.getShooter() instanceof Player p
                        && isOwner(p) && event0.getEntity() instanceof LivingEntity entity && entity != p) {
                    return 0;
                }
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
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            event0.setDamage(event0.getDamage() + 3.0D);
            if (Math.random() <= 0.6D) {
                event0.getEntity().setFireTicks(120);
            }
            if (Math.random() <= 0.4D) {
                World w = event0.getEntity().getWorld();
                w.createExplosion(event0.getEntity().getLocation(), 1.5f);
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
                new ItemStack(Material.BOW, 1),
                new ItemStack(Material.ARROW, 64)
        };
    }

    @Override
    public String getItemName() {
        return "활과 화살";
    }

}
