package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;
import io.github.kdy05.physicalFighters.util.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Objects;
import java.util.UUID;

public final class Mirroring extends Ability {
    public Mirroring(UUID playerUuid) {
        super(AbilitySpec.builder("미러링", Type.PassiveManual, Rank.SSS)
                .guide("당신을 죽인 사람을 함께 저승으로 끌고갑니다.")
                .minimumPlayers(7)
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDeath(new EventData(this));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        EntityDeathEvent event0 = (EntityDeathEvent) event;
        if (event0.getEntity().getKiller() != null && isOwner(event0.getEntity()))
            return 0;
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        EntityDeathEvent event0 = (EntityDeathEvent) event;
        Player player = (Player) event0.getEntity();
        if (player.getKiller() == null) return;
        Bukkit.broadcastMessage(String.format(ChatColor.RED +
                "%s님의 미러링 능력이 발동되었습니다.", player.getName()));
        SoundUtils.broadcastWarningSound();

        Ability assimilation = AbilityRegistry.findByType("흡수", player.getKiller());
        if (assimilation != null) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "흡수 능력에 의해 미러링 능력이 무력화 되었습니다.");
            return;
        }

        Ability aegis = AbilityRegistry.findByType("이지스", player.getKiller());
        if (aegis != null && aegis.isDurationRunning()) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "이지스 능력에 의해 미러링 능력이 무력화 되었습니다.");
            return;
        }

        Objects.requireNonNull(player.getKiller()).damage(5000);
        Bukkit.broadcastMessage(String.format(ChatColor.RED + "%s님의 미러링에 의해 %s님이 죽었습니다.",
                player.getName(), player.getKiller().getName()));
    }
}
