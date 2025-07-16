package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Fittingroom extends Ability {
    public Fittingroom() {
        InitAbility("탈의실", Type.Active_Immediately, Rank.SSS,
                "능력 사용시 자기 자신을 제외한 모든 플레이어가 손에 쥐고있는 아이템을 떨어뜨립니다.");
        InitAbility(160, 0, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (EventManager.DamageGuard) return -1;
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (isOwner(Event.getPlayer()) && isValidItem(Ability.DefaultItem))
            return 0;
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player caster = Event.getPlayer();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != caster && player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                caster.getWorld().dropItem(player.getLocation(), player.getInventory().getItemInMainHand());
                player.getInventory().remove(player.getInventory().getItemInMainHand());
            }
        }
        Bukkit.broadcastMessage(ChatColor.AQUA + caster.getName() +
                "님이 능력을 사용해 모든 플레이어의 무장을 해제시켰습니다.");
    }
}
