package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.util.BaseItem;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Archer extends Ability implements BaseItem {
    public Archer() {
        super(AbilitySpec.builder("아쳐", Type.Passive_Manual, Rank.A)
                .guide("상대에게 쏘는 화살 대미지가 항상 3 상승합니다.",
                        "60% 확률로 6초간 불을 붙이며, 40% 확률로 폭발을 일으킵니다.",
                        "죽거나 게임 시작시 활과 화살이 고정적으로 주어집니다.")
                .build());
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (event0.getDamager() instanceof Arrow) {
                Arrow a = (Arrow) event0.getDamager();
                if (a.getShooter() instanceof Player) {
                    Player p = (Player) a.getShooter();
                    if (isOwner(p) && event0.getEntity() instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity) event0.getEntity();
                        if (entity != p) {
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
