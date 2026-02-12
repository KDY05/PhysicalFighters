package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import java.util.UUID;

public class Fly extends Ability {
    public Fly(UUID playerUuid) {
        super(AbilitySpec.builder("플라이", Type.Active_Continue, Rank.GOD)
                .cooldown(60)
                .duration(10)
                .guide(Usage.IronLeft + "10초간 하늘을 날라다닐 수 있습니다.",
                        Usage.Passive + "낙하 대미지를 받지 않습니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
        EventManager.registerEntityDamage(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent event0 = (PlayerInteractEvent) event;
            if (isOwner(event0.getPlayer()) && isValidItem(Ability.DefaultItem)) {
                return 0;
            }
        } else if (CustomData == 1) {
            EntityDamageEvent event1 = (EntityDamageEvent) event;
            if (isOwner(event1.getEntity()) && event1.getCause() == DamageCause.FALL) {
                sendMessage(ChatColor.GREEN + "사뿐하게 떨어져 대미지를 받지 않았습니다.");
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
