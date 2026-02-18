package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.BaseItem;
import io.github.kdy05.physicalFighters.util.EventData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class FallArrow extends Ability implements BaseItem {
    public FallArrow(UUID playerUuid) {
        super(AbilitySpec.builder("중력화살", Type.PassiveManual, Rank.S)
                .guide("화살에 맞은 플레이어는 공중으로 뜹니다. [추가타 가능]")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamageByEntity(new EventData(this, 0));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (!(event0.getDamager() instanceof Arrow)) return -1;

        Arrow a = (Arrow) event0.getDamager();
        if (!(a.getShooter() instanceof Player)) return -1;

        Player player = (Player) a.getShooter();
        if (isOwner(player) && event0.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event0.getEntity();
            if (entity != player && !InvincibilityManager.isDamageGuard()) {
                return 0;
            }
        }

        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
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
