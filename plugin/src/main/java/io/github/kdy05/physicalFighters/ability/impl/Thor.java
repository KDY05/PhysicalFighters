package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public final class Thor extends Ability {

    private Location targetLocation = null;

    public Thor(UUID playerUuid) {
        super(AbilitySpec.builder("토르", Type.ActiveImmediately, Rank.S)
                .cooldown(30)
                .guide("바라보는 지점에 번개를 떨어뜨립니다.",
                        "번개가 떨어진 지점에 강한 폭발이 일어납니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        if (!isOwner(event0.getPlayer()) || !isValidItem(Ability.DefaultItem) || InvincibilityManager.isDamageGuard()) {
            return -1;
        }

        Player caster = event0.getPlayer();
        targetLocation = AbilityUtils.getTargetLocation(caster, 40);
        if (targetLocation == null) {
            SoundUtils.playErrorSound(caster);
            caster.sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
            return -1;
        }

        return 0;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player player = event0.getPlayer();
        World world = player.getWorld();
        world.createExplosion(targetLocation, 4.0f, true);
        world.strikeLightning(targetLocation);
        world.strikeLightning(targetLocation);
    }

}
