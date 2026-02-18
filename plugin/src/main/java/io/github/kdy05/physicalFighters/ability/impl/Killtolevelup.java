package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.BaseItem;
import io.github.kdy05.physicalFighters.util.EventData;
import io.github.kdy05.physicalFighters.util.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class Killtolevelup extends Ability implements BaseItem {

    private int damage = 5;

    public Killtolevelup(UUID playerUuid) {
        super(AbilitySpec.builder("폭주", Type.PassiveManual, Rank.SS)
                .guide("깃털의 처음 대미지는 5입니다.",
                        "깃털로 적을 처치할 때마다 대미지가 2만큼 늘어납니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamageByEntity(new EventData(this, 0));
        EventManager.registerEntityDeath(new EventData(this, 1));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (isOwner(event0.getDamager()) && isValidItem(Material.FEATHER))
                return 0;
        }
        else if (CustomData == 1) {
            EntityDeathEvent event1 = (EntityDeathEvent) event;
            if (event1.getEntity().getKiller() != null && isOwner(event1.getEntity().getKiller())
                    && isValidItem(Material.FEATHER) && event1.getEntity() instanceof Player)
                return 1;
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            event0.setDamage(event0.getDamage() * this.damage);
        }
        else if (CustomData == 1) {
            EntityDeathEvent event1 = (EntityDeathEvent) event;
            Player player = event1.getEntity().getKiller();
            if (player == null) return;
            this.damage += 2;
            SoundUtils.broadcastWarningSound();
            Bukkit.broadcastMessage(String.format(ChatColor.RED + "%s님을 죽이고 %s님이 폭주했습니다.",
                    event1.getEntity().getName(), player.getName()));
        }
    }

    @NotNull
    @Override
    public ItemStack[] getBaseItem() {
        return new ItemStack[] {
                new ItemStack(Material.FEATHER, 1)
        };
    }

    @NotNull
    @Override
    public String getItemName() {
        return "깃털";
    }

}
