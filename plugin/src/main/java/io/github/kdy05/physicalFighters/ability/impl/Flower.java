package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.EventData;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public final class Flower extends Ability {
    public Flower(UUID playerUuid) {
        super(AbilitySpec.builder("흡혈초", Type.ActiveImmediately, Rank.SS)
                .cooldown(30)
                .guide(Usage.IronAttack + "맞은 사람의 체력을 흡수합니다.",
                        Usage.IronRight + "자신의 체력을 소비해 레벨을 얻습니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamageByEntity(new EventData(this));
        registerRightClickEvent();
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (event0.getEntity() instanceof LivingEntity && isOwner(event0.getDamager())
                    && isValidItem(Ability.DefaultItem) && !InvincibilityManager.isDamageGuard()) {
                return 0;
            }
        } else if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            if (isOwner(event1.getPlayer()) && isValidItem(Ability.DefaultItem)) {
                if (event1.getPlayer().getHealth() < 16.0) {
                    sendMessage(ChatColor.RED + "체력이 부족합니다.");
                    return -1;
                }
                return 1;
            }
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            LivingEntity entity = (LivingEntity) event0.getEntity();
            Player player = (Player) event0.getDamager();

            AbilityUtils.piercingDamage(entity, 4.0);
            AbilityUtils.healEntity(player, 4.0);

            entity.sendMessage(String.format(ChatColor.RED
                    + "%s(이)가 당신의 체력을 흡수했습니다.", player.getName()));
            sendMessage(String.format(ChatColor.RED
                    + "%s의 체력을 흡수했습니다.", entity.getName()));
        }
        else if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            Player player = event1.getPlayer();
            player.setLevel(player.getLevel() + 1);
            player.setHealth(Math.max(0, player.getHealth() - 15));
            player.sendMessage(ChatColor.GREEN + "레벨을 얻었습니다.");
        }
    }
}
