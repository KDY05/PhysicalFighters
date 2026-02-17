package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;
import io.github.kdy05.physicalFighters.util.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public final class Medic extends Ability {
    public Medic(UUID playerUuid) {
        super(AbilitySpec.builder("메딕", Type.ActiveImmediately, Rank.B)
                .cooldown(8)
                .guide(Usage.IronAttack + "타인의 체력을 6만큼 회복합니다.",
                        Usage.IronRight + "자신의 체력을 6만큼 회복합니다.")
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
            if (event0.getEntity() instanceof LivingEntity &&
                    isOwner(event0.getDamager()) && isValidItem(Ability.DefaultItem)) {
                return 0;
            }
        } else if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            if (isOwner(event1.getPlayer()) && isValidItem(Ability.DefaultItem)) {
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
            AbilityUtils.healEntity(entity, 6);
            if (getPlayer() == null) return;
            entity.sendMessage(String.format(ChatColor.GREEN
                    + "%s의 메딕 능력으로 체력을 6 회복했습니다.", getPlayer().getName()));
            SoundUtils.playSuccessSound(getPlayer());
            getPlayer().sendMessage(String.format(ChatColor.GREEN
                    + "%s의 체력을 6 회복시켰습니다.", entity.getName()));
            event0.setCancelled(true);
        } else if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            Player p2 = event1.getPlayer();
            AbilityUtils.healEntity(p2, 6);
            SoundUtils.playSuccessSound(getPlayer());
            p2.sendMessage(ChatColor.GREEN + "자신의 체력을 6 회복했습니다.");
        }
    }
}
