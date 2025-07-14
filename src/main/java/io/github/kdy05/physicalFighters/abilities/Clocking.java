package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Clocking extends Ability {
    public Clocking() {
        InitAbility("클로킹", Type.Active_Continue, Rank.A,
                "철괴 좌클릭 시 일정 시간동안 다른 사람에게 보이지 않습니다.",
                "클로킹 상태에서는 타인에게 공격 받지 않습니다.");
        InitAbility(35, 5, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (isOwner(Event.getPlayer()) && isValidItem(Ability.DefaultItem))
            return 0;
        return -1;
    }

    @Override
    public void A_DurationStart() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(plugin, getPlayer());
        }
    }

    @Override
    public void A_FinalDurationEnd() {
        if (getPlayer() == null) return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(plugin, getPlayer());
        }
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
    }

}
