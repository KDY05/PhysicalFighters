package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.BaseItem;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class FallArrow extends Ability implements BaseItem {
    public FallArrow(Player player) {
        super(AbilitySpec.builder("중력화살", Type.Passive_Manual, Rank.S)
                .guide("화살에 맞은 플레이어는 공중으로 뜹니다. [추가타 가능]")
                .build(), player);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamageByEntity(new EventData(this, 0));
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (event0.getDamager() instanceof Arrow) {
                Arrow a = (Arrow) event0.getDamager();
                if (a.getShooter() instanceof Player) {
                    Player player = (Player) a.getShooter();
                    if (isOwner(player) && event0.getEntity() instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity) event0.getEntity();
                        if (entity != player && !InvincibilityManager.isDamageGuard()) {
                            return 0;
                        }
                    }
                }
            }
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
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            LivingEntity entity = (LivingEntity) event0.getEntity();
            Location l1 = event0.getEntity().getLocation();
            Location l2 = event0.getEntity().getLocation();
            l2.setY(l1.getY() + 4.0D);
            AbilityUtils.goVelocity(entity, l2, 1);
            entity.getWorld().createExplosion(entity.getLocation(), 0.0F);
            entity.teleport(l2);
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
