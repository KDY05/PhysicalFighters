package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.BaseItem;
import io.github.kdy05.physicalFighters.util.EventData;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Kimimaro extends Ability implements BaseItem {
    public Kimimaro(UUID playerUuid) {
        super(AbilitySpec.builder("키미마로", Type.PassiveManual, Rank.SS)
                .guide("뼈다귀로 상대를 공격할 시에 강한 대미지를 주고,",
                        "40% 확률로 상대에게 5초간 독 효과를 겁니다.")
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
            if (isOwner(event0.getDamager()) && isValidItem(Material.BONE)
                    && event0.getEntity() instanceof LivingEntity)
                return 0;
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            event0.setDamage(event0.getDamage() * 7);
            LivingEntity entity = (LivingEntity) event0.getEntity();
            if (Math.random() < 0.4D)
                entity.addPotionEffect(PotionEffectFactory.createPoison(20 * 5, 0));
        }
    }

    @NotNull
    @Override
    public ItemStack[] getBaseItem() {
        return new ItemStack[] {
                new ItemStack(Material.BONE, 1)
        };
    }

    @NotNull
    @Override
    public String getItemName() {
        return "뼈다귀";
    }

}
