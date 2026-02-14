package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import org.bukkit.util.Vector;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;

import java.util.Objects;
import java.util.UUID;

public class Enel extends Ability {
    public Enel(UUID playerUuid) {
        super(AbilitySpec.builder("갓 에넬", Type.ActiveImmediately, Rank.S)
                .cooldown(30)
                .guide(Usage.IronLeft + "바라보는 방향으로 번개를 발사하여 강한 범위 대미지를 줍니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (InvincibilityManager.isDamageGuard() || !isOwner(Event.getPlayer()) || !isValidItem(Ability.DefaultItem))
            return -1;
        return 0;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        Player caster = e.getPlayer();

        Location startLoc = caster.getLocation();
        Vector direction = caster.getLocation().getDirection();

        for (int i = 3; i <= 10; i++) {
            Location lightningLoc = startLoc.clone().add(direction.clone().multiply(i));
            Objects.requireNonNull(lightningLoc.getWorld()).strikeLightning(lightningLoc);
            AbilityUtils.splashDamage(caster, lightningLoc, 2, 20);
        }
    }
}
