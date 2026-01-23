package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.utils.BaseItem;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import io.github.kdy05.physicalFighters.utils.PotionEffectFactory;

public class PoisonArrow extends Ability implements BaseItem {
    public PoisonArrow() {
        InitAbility("독화살", Type.Passive_Manual, Rank.B,
                "화살에 맞은 적은 6초간 독에걸립니다.",
                "죽거나 게임 시작시 활과 화살이 고정적으로 주어집니다.");
        InitAbility(0, 0, true);
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
                        LivingEntity e = (LivingEntity) event0.getEntity();
                        if (p != e) {
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
            LivingEntity target = (LivingEntity) event0.getEntity();
            target.addPotionEffect(PotionEffectFactory.createNausea(60, 0));
            target.addPotionEffect(PotionEffectFactory.createPoison(120, 0));
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
