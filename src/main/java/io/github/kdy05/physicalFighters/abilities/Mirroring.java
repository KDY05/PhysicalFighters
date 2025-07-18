package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.core.AbilityList;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Objects;

public class Mirroring extends Ability {
    public Mirroring() {
        InitAbility("미러링", Type.Passive_Manual, Rank.SSS,
                "당신을 죽인 사람을 함께 저승으로 끌고갑니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDeath.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDeathEvent event0 = (EntityDeathEvent) event;
        if (event0.getEntity().getKiller() != null && isOwner(event0.getEntity()))
            return 0;
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDeathEvent event0 = (EntityDeathEvent) event;
        Player player = (Player) event0.getEntity();
        Bukkit.broadcastMessage(String.format(ChatColor.RED +
                "%s님의 미러링 능력이 발동되었습니다.", player.getName()));
        if (AbilityList.assimilation.getPlayer() == player.getKiller()) {
            AbilityList.assimilation.A_Effect(event0, 1);
            Bukkit.broadcastMessage(ChatColor.GREEN + "흡수 능력에 의해 미러링 능력이 무력화 되었습니다.");
            return;
        }
        if (AbilityList.aegis.getPlayer() == player.getKiller() && AbilityList.aegis.getDurationState()) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "이지스 능력에 의해 미러링 능력이 무력화 되었습니다.");
            return;
        }
        Objects.requireNonNull(player.getKiller()).damage(5000);
        Bukkit.broadcastMessage(String.format(ChatColor.RED + "%s님의 미러링에 의해 %s님이 죽었습니다.",
                player.getName(), player.getKiller().getName()));
    }
}
