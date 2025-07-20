package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Jumper extends Ability {
    private Location targetLocation = null;

    public Jumper() {
        InitAbility("점퍼", Type.Active_Immediately, Rank.B,
                "최대 80칸 거리를 순간이동 할수 있습니다.");
        InitAbility(20, 0, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (!isOwner(Event.getPlayer()) || !isValidItem(Ability.DefaultItem)) return -1;
        Player caster = Event.getPlayer();
        targetLocation = AbilityUtils.getTargetLocation(caster, 80);
        if (targetLocation == null) {
            caster.sendMessage(String.format(ChatColor.RED + "거리가 너무 멉니다."));
            return -1;
        }
        return 0;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player caster = Event.getPlayer();
        targetLocation.setY(caster.getWorld().getHighestBlockYAt(targetLocation) + 1.0);
        targetLocation.setPitch(caster.getLocation().getPitch());
        targetLocation.setYaw(caster.getLocation().getYaw());
        caster.teleport(targetLocation);
        targetLocation = null;
    }
}
