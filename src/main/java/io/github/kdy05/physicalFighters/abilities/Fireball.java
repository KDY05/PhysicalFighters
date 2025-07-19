package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.utils.EventData;

public class Fireball extends Ability {
    public Fireball() {
        InitAbility("파이어볼", Type.Active_Immediately, Rank.B,
                "바라보는 방향에 화염구를 날립니다.");
        InitAbility(15, 0, true);
        registerLeftClickEvent();
        EventManager.onProjectileHitEvent.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if (isOwner(Event.getPlayer()) && isValidItem(Ability.DefaultItem) && !PhysicalFighters.DamageGuard)
                    return 0;
                break;
            case 1:
                ProjectileHitEvent Event1 = (ProjectileHitEvent) event;
                if (Event1.getEntity() instanceof org.bukkit.entity.Fireball) {
                    Event1.getEntity().getWorld().createExplosion(Event1.getEntity().getLocation(), 3.0F, true);
                    Event1.getEntity().getWorld().createExplosion(Event1.getEntity().getLocation(), 2.5F, true);
                    Event1.getEntity().getWorld().createExplosion(Event1.getEntity().getLocation(), 3.0F, true);
                }
                break;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Location loc = p.getLocation();
        Location loc2 = p.getLocation();
        loc2.setY(p.getLocation().getY() + 2.0D);
        double degrees = Math.toRadians(-(loc2.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(loc2.getPitch() % 360.0F));
        loc.setX(loc2.getX() + 1.2D * (Math.sin(degrees) * Math.cos(ydeg)));
        loc.setY(loc2.getY() + 1.2D * Math.sin(ydeg));
        loc.setZ(loc2.getZ() + 1.2D * (Math.cos(degrees) * Math.cos(ydeg)));
        p.getWorld().spawn(loc, org.bukkit.entity.Fireball.class);
    }
}
