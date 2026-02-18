package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public final class Apollon extends Ability {
    // 타겟 위치 저장
    private Location targetLocation = null;

    public Apollon(UUID playerUuid) {
        super(AbilitySpec.builder("아폴론", Type.ActiveImmediately, Rank.SS)
                .cooldown(40)
                .guide(Usage.IronLeft + "바라보는 방향에 불구덩이를 만듭니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player caster = event0.getPlayer();

        if (!isOwner(caster) || !isValidItem(Ability.DefaultItem) || InvincibilityManager.isDamageGuard()) {
            return -1;
        }

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
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();

        if (targetLocation == null) {
            p.sendMessage(ChatColor.RED + "능력을 사용할 수 없습니다.");
            return;
        }

        Location center = targetLocation.clone();
        AbilityUtils.createBox(center.clone().add(0, -8, 0), Material.NETHERRACK, 4, 8);
        AbilityUtils.createBox(center.clone().add(0, -7, 0), Material.AIR, 3, 7);
        AbilityUtils.createBox(center.clone().add(0, -7, 0), Material.FIRE, 3, 1);

        targetLocation = null;
    }

}
