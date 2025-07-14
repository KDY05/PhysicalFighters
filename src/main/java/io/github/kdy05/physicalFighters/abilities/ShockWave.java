package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import io.github.kdy05.physicalFighters.utils.AUC;

public class ShockWave extends Ability {
    public ShockWave() {
        InitAbility("쇼크웨이브", Type.Active_Immediately, Rank.A,
                "철괴를 좌클릭하여 보고있는 방향으로 막강한 직선 충격포를 쏩니다.",
                "충격포는 물과 벽 건너편까지 통과할 수 있습니다.");
        InitAbility(50, 0, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (isOwner(Event.getPlayer()) && isValidItem(DefaultItem)) {
            PlayerInventory inv = Event.getPlayer().getInventory();
            if (inv.contains(Material.IRON_INGOT, 1)) {
                return 0;
            }
            Event.getPlayer().sendMessage(org.bukkit.ChatColor.RED + "철괴가 부족합니다.");
            return -1;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        AUC.removeOneItem(getPlayer(), Material.IRON_INGOT);
        Location l = Event.getPlayer().getLocation();
        Location l2 = Event.getPlayer().getLocation();
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));
        for (int i = 1; i < 7; i++) {
            l2.setX(l.getX() + (3 * i + 3) * (Math.sin(degrees) * Math.cos(ydeg)));
            l2.setY(l.getY() + (3 * i + 3) * Math.sin(ydeg));
            l2.setZ(l.getZ() + (3 * i + 3) * (Math.cos(degrees) * Math.cos(ydeg)));
            getPlayer().getWorld().createExplosion(l2, 4.0F);
        }
    }
}
