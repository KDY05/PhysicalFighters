package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.utils.BaseItem;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import io.github.kdy05.physicalFighters.utils.PotionEffectFactory;

public class Kimimaro extends Ability implements BaseItem {
    public Kimimaro() {
        InitAbility("키미마로", Type.Passive_Manual, Rank.SS,
                "뼈다귀로 상대를 공격할 시에 강한 대미지를 주고,",
                "40% 확률로 상대에게 5초간 독 효과를 겁니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (isOwner(event0.getDamager()) && isValidItem(Material.BONE)
                    && event0.getEntity() instanceof LivingEntity)
                return 0;
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
            event0.setDamage(event0.getDamage() * 7);
            LivingEntity entity = (LivingEntity) event0.getEntity();
            if (Math.random() < 0.4D)
                entity.addPotionEffect(PotionEffectFactory.createPoison(20 * 5, 0));
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
                new ItemStack(Material.BONE, 1)
        };
    }

    @Override
    public String getItemName() {
        return "뼈다귀";
    }

}
