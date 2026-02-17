package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.BaseItem;
import io.github.kdy05.physicalFighters.util.EventData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class Multishot extends Ability implements BaseItem {
    public Multishot(UUID playerUuid) {
        super(AbilitySpec.builder("멀티샷", Type.ActiveImmediately, Rank.A)
                .cooldown(3)
                .guide("화살 발사 시 여러 발이 퍼지면서 날라갑니다.",
                        "죽거나 게임 시작시 활과 화살이 고정적으로 주어집니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerProjectileLaunch(new EventData(this, 0));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        if (CustomData == 0) {
            ProjectileLaunchEvent event0 = (ProjectileLaunchEvent) event;
            if (!(event0.getEntity() instanceof Arrow)) return -1;

            Arrow a = (Arrow) event0.getEntity();
            if (a.getShooter() instanceof Player) {
                Player p = (Player) a.getShooter();
                if (isOwner(p) && isValidItem(Material.BOW)) {
                    return 0;
                }
            }
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
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

    @NotNull
    @Override
    public ItemStack[] getBaseItem() {
        return new ItemStack[] {
                new ItemStack(Material.BOW, 1),
                new ItemStack(Material.ARROW, 64)
        };
    }

    @NotNull
    @Override
    public String getItemName() {
        return "활과 화살";
    }

}
