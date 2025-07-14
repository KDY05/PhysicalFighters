package io.github.kdy05.physicalFighters.abilities;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class Medic extends Ability {
    public Medic() {
        InitAbility("메딕", Type.Active_Immediately, Rank.B,
                "철괴로 타격하여 맞은 사람을, 혹은 우클릭하여 자신을 6 회복합니다.");
        InitAbility(15, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
        registerRightClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                if (Event1.getEntity() instanceof LivingEntity &&
                        isOwner(Event1.getDamager()) && isValidItem(Ability.DefaultItem)) {
                    return 0;
                }
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                if (isOwner(Event2.getPlayer()) && isValidItem(Ability.DefaultItem)) {
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
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                LivingEntity entity = (LivingEntity) Event1.getEntity();
                double maxHealth = Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getValue();
                entity.setHealth(Math.min(maxHealth, entity.getHealth() + 6));
                if (entity instanceof Player p)
                    p.sendMessage(String.format(ChatColor.GREEN +
                            "%s의 메딕 능력으로 체력을 6 회복했습니다.", getPlayer().getName()));
                getPlayer().sendMessage(
                        String.format(ChatColor.GREEN + "%s의 체력을 6 회복시켰습니다.", entity.getName()));
                Event1.setCancelled(true);
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                Player p2 = Event2.getPlayer();
                p2.setHealth(Math.min(20, p2.getHealth() + 6));
                p2.sendMessage(ChatColor.GREEN + "자신의 체력을 6 회복했습니다.");
        }
    }
}
