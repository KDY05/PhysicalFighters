package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

public class Fly extends Ability {
    public Fly() {
        InitAbility("플라이", Type.Active_Continue, Rank.GOD,
                Usage.IronLeft + "10초간 하늘을 날라다닐 수 있습니다.",
                Usage.Passive + "낙하 데미지를 받지 않습니다.");
        InitAbility(60, 10, true);
        registerLeftClickEvent();
        EventManager.onEntityDamage.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            if (isOwner(Event.getPlayer()) && isValidItem(Ability.DefaultItem)) {
                return 0;
            }
        }
        if (CustomData == 1) {
            EntityDamageEvent event1 = (EntityDamageEvent) event;
            if (isOwner(event1.getEntity()) && event1.getCause() == DamageCause.FALL) {
                getPlayer().sendMessage(ChatColor.GREEN + "사뿐하게 떨어져 데미지를 받지 않았습니다.");
                event1.setCancelled(true);
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
    }

    @Override
    public void A_DurationStart() {
        if (getPlayer() == null) return;
        getPlayer().setAllowFlight(true);
        getPlayer().setFlying(true);
    }

    @Override
    public void A_FinalDurationEnd() {
        if (getPlayer() == null) return;
        getPlayer().setAllowFlight(false);
        getPlayer().setFlying(false);
    }

}
