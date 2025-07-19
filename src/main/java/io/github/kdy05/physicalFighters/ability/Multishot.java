package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.utils.BaseItem;
import org.bukkit.entity.AbstractArrow;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Multishot extends Ability implements BaseItem {
    public Multishot() {
        InitAbility("멀티샷", Type.Active_Immediately, Rank.A,
                "화살 발사 시 여러 발이 퍼지면서 날라갑니다.",
                "죽거나 게임 시작시 활과 화살이 고정적으로 주어집니다.");
        InitAbility(3, 0, true);
        EventManager.onProjectileLaunchEvent.add(new EventData(this, 0));
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                ProjectileLaunchEvent event0 = (ProjectileLaunchEvent) event;
                if (event0.getEntity() instanceof Arrow a && a.getShooter() instanceof Player p
                        && isOwner(p) && isValidItem(Material.BOW)) {
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
            ProjectileLaunchEvent event0 = (ProjectileLaunchEvent) event;
            Arrow originalArrow = (Arrow) event0.getEntity();
            Player shooter = (Player) originalArrow.getShooter();
            originalArrow.remove();

            if (shooter == null) return;
            Location shootLocation = shooter.getEyeLocation();
            Vector direction = shooter.getLocation().getDirection();

            for (int i = 0; i <= 10; i++) {
                Arrow arrow = shooter.getWorld().spawnArrow(shootLocation, direction,
                        1.5F, 10.0F);
                arrow.setVelocity(arrow.getVelocity().multiply(3.0));
                arrow.setShooter(shooter);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
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
