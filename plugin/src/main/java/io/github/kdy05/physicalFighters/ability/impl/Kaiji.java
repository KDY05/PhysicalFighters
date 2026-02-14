package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.BaseItem;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class Kaiji extends Ability implements BaseItem {
    public Kaiji(UUID playerUuid) {
        super(AbilitySpec.builder("카이지", Type.PassiveManual, Rank.S)
                .cooldown(20)
                .guide("다이아몬드로 상대 타격 시 30% 확률로 상대를 즉사시키고, 70% 확률로 자신이 사망합니다.")
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
            if (!InvincibilityManager.isDamageGuard() && isOwner(event0.getDamager())
                    && isValidItem(Material.DIAMOND) && event0.getEntity() instanceof Player)
                return 0;
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            Player player = (Player) event0.getEntity();
            if (Math.random() <= 0.3D) {
                player.damage(5000);
                Bukkit.broadcastMessage(String.format(ChatColor.RED +
                        "%s님이  카'의지'에 능력에 의지가 꺾였습니다.", player.getName()));
            } else {
                if (getPlayer() == null) return;
                getPlayer().damage(5000);
                Bukkit.broadcastMessage(String.format(ChatColor.RED +
                        "%s님이 도박하다가 손목이 날라갔습니다.", getPlayer().getName()));
            }
        }
    }

    @Override
    public ItemStack[] getBaseItem() {
        return new ItemStack[] {
                new ItemStack(Material.DIAMOND, 1)
        };
    }

    @Override
    public String getItemName() {
        return "다이아몬드";
    }

}
