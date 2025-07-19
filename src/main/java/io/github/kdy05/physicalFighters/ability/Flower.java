package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Flower extends Ability {
    public Flower() {
        InitAbility("흡혈초", Type.Active_Immediately, Rank.SS,
               Usage.IronAttack + "맞은 사람의 체력을 흡수합니다.",
               Usage.IronRight + "자신의 체력을 소비해 레벨을 얻습니다.");
        InitAbility(30, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
        registerRightClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                if (Event1.getEntity() instanceof LivingEntity && isOwner(Event1.getDamager())
                        && isValidItem(Ability.DefaultItem) && !PhysicalFighters.DamageGuard) {
                    return 0;
                }
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                if (isOwner(Event2.getPlayer()) && isValidItem(Ability.DefaultItem)) {
                    if (getPlayer().getHealth() < 16.0) {
                        getPlayer().sendMessage(ChatColor.RED + "체력이 부족합니다.");
                        break;
                    }
                    return 1;
                }
                break;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                LivingEntity entity = (LivingEntity) Event0.getEntity();

                entity.setHealth(Math.max(0, entity.getHealth() - 4.0));
                getPlayer().setHealth(Math.min(20, getPlayer().getHealth() + 4.0));

                entity.sendMessage(String.format(ChatColor.RED
                        + "%s(이)가 당신의 체력을 흡수했습니다.", getPlayer().getName()));
                getPlayer().sendMessage(String.format(ChatColor.RED
                        + "%s의 체력을 흡수했습니다.", entity.getName()));
                break;

            case 1:
                PlayerInteractEvent Event1 = (PlayerInteractEvent) event;
                Player player = Event1.getPlayer();
                player.setLevel(player.getLevel() + 1);
                player.setHealth(Math.max(0, player.getHealth() - 15));
                player.sendMessage(ChatColor.GREEN + "레벨을 얻었습니다.");
        }
    }
}
