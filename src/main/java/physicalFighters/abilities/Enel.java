package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class Enel extends Ability {
    public Enel() {
        InitAbility("갓 에넬", Type.Active_Immediately, Rank.S,
                "바라보는 방향으로 번개를 연속발사합니다.");
        InitAbility(30, 0, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (!EventManager.DamageGuard &&
                isOwner(Event.getPlayer()) && isValidItem(Ability.DefaultItem)) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Location l = Event.getPlayer().getLocation();
        Location l2 = Event.getPlayer().getLocation();
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        for (int i = 1; i < 5; i++) {
            l2.setX(l.getX() + i + 4.0D * Math.sin(degrees));
            l2.setZ(l.getZ() + i + 4.0D * Math.cos(degrees));
            Objects.requireNonNull(l2.getWorld()).strikeLightning(l2);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != getPlayer()) {
                    Location loc = player.getLocation();
                    if (l2.getWorld().getBlockAt(l2).getLocation().distance(loc) <= 3.0
                            && !EventManager.DamageGuard) {
                        player.damage(15);
                    }
                }
            }
        }
    }
}
