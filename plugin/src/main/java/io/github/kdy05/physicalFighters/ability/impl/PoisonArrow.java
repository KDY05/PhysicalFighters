package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.BaseItem;
import io.github.kdy05.physicalFighters.util.EventData;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class PoisonArrow extends Ability implements BaseItem {
    public PoisonArrow(UUID playerUuid) {
        super(AbilitySpec.builder("독화살", Type.PassiveManual, Rank.B)
                .guide("화살에 맞은 적은 6초간 독에걸립니다.",
                        "죽거나 게임 시작시 활과 화살이 고정적으로 주어집니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamageByEntity(new EventData(this, 0));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (!(event0.getDamager() instanceof Arrow)) return -1;

            Arrow a = (Arrow) event0.getDamager();
            if (!(a.getShooter() instanceof Player)) return -1;

            Player p = (Player) a.getShooter();
            if (isOwner(p) && event0.getEntity() instanceof LivingEntity) {
                LivingEntity e = (LivingEntity) event0.getEntity();
                if (p != e) return 0;
            }
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            LivingEntity target = (LivingEntity) event0.getEntity();
            target.addPotionEffect(PotionEffectFactory.createNausea(60, 0));
            target.addPotionEffect(PotionEffectFactory.createPoison(120, 0));
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
