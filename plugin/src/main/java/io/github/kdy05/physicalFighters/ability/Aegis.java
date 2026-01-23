package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Aegis extends Ability {
    public Aegis() {
        InitAbility("이지스", Type.Active_Continue, Rank.A,
                Usage.IronLeft + "6초 동안 무적이 됩니다.",
                "능력 사용 중엔 미러링 능력도 무시합니다.");
        InitAbility(28, 6, true);
        registerLeftClickEvent();
        EventManager.onEntityDamage.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent event0 = (PlayerInteractEvent) event;
            if (isOwner(event0.getPlayer()) && isValidItem(Ability.DefaultItem) && !ConfigManager.DamageGuard)
                return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 1) {
            EntityDamageEvent event1 = (EntityDamageEvent) event;
            if (isOwner(event1.getEntity())) {
                Player p = (Player) event1.getEntity();
                p.setFireTicks(0);
                event1.setCancelled(true);
            }
        }
    }
}
