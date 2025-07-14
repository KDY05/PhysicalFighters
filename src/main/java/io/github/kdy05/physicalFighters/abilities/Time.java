package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Time extends Ability {
    public Time() {
        InitAbility("타임", Type.Active_Continue, Rank.A,
                "자신을 제외한 모든 능력자의 이동을 5초동안",
                "차단합니다. 단, 직접적인 이동만 불가능합니다.", "능력이 없는 사람도 다 멈춥니다.");
        InitAbility(40, 5, true);
        registerLeftClickEvent();
        EventManager.onPlayerMoveEvent.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem)) && !EventManager.DamageGuard) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerMoveEvent Event = (PlayerMoveEvent) event;
        if (!isOwner(Event.getPlayer())) {
            Event.setCancelled(true);
        }
    }

    public void A_DurationStart() {
        Bukkit.broadcastMessage(String.format("%s" + ChatColor.RED +
                "님이 Time 능력을 사용했습니다.", getPlayer().getName()));
    }

    public void A_DurationEnd() {
        Bukkit.broadcastMessage(String.format(ChatColor.GREEN +
                "Time 능력이 해제되었습니다."));
    }
}
