package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

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
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            if (isOwner(Event.getPlayer()) && isValidItem(Ability.DefaultItem) && !PhysicalFighters.DamageGuard)
                return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 1) {
            EntityDamageEvent Event = (EntityDamageEvent) event;
            if (isOwner(Event.getEntity())) {
                Player p = (Player) Event.getEntity();
                p.setFireTicks(0);
                Event.setCancelled(true);
            }
        }
    }
}
