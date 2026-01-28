package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.util.BaseItem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileHitEvent;
import io.github.kdy05.physicalFighters.game.Ability;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class Blitzcrank extends Ability implements BaseItem {
    public Blitzcrank() {
        InitAbility("블리츠크랭크", Type.Active_Immediately, Rank.S,
                "눈덩이를 던져, 맞은 적을 자신에게 끌어당깁니다.");
        InitAbility(1, 0, true);
        EventManager.onProjectileHitEvent.add(new EventData(this, 0));
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                ProjectileHitEvent event0 = (ProjectileHitEvent) event;
                if (event0.getEntity() instanceof Snowball s && s.getShooter() instanceof Player p
                        && isOwner(p) && event0.getHitEntity() instanceof LivingEntity e && p != e) {
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
            ProjectileHitEvent event0 = (ProjectileHitEvent) event;
            LivingEntity target = (LivingEntity) event0.getHitEntity();
            if (getPlayer() == null) return;
            if (target != null) target.teleport(getPlayer().getLocation());
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
                new ItemStack(Material.SNOWBALL, 64)
        };
    }

    @Override
    public String getItemName() {
        return "눈덩이";
    }

}
