package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.UUID;

public class Clocking extends Ability {
    public Clocking(UUID playerUuid) {
        super(AbilitySpec.builder("클로킹", Type.ActiveContinue, Rank.A)
                .cooldown(30)
                .duration(5)
                .guide(Usage.IronLeft + "일정 시간동안 다른 사람에게 보이지 않습니다.",
                        "클로킹 상태에서는 타인에게 공격 받지 않습니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        if (isOwner(event0.getPlayer()) && isValidItem(Ability.DefaultItem))
            return 0;
        return -1;
    }

    @Override
    public void onDurationStart() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (getPlayer() == null) return;
            p.hidePlayer(plugin, getPlayer());
        }
    }

    @Override
    public void onDurationFinalize() {
        if (getPlayer() == null) return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(plugin, getPlayer());
        }
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
    }

    @Override
    public void onDeactivate(Player owner) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(plugin, owner);
        }
    }

}
