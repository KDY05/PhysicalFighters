package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.ConfigManager;
import org.bukkit.util.Vector;
import io.github.kdy05.physicalFighters.core.Ability;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;

import java.util.Objects;

public class Enel extends Ability {
    public Enel() {
        InitAbility("갓 에넬", Type.Active_Immediately, Rank.S,
                Usage.IronLeft + "바라보는 방향으로 번개를 발사하여 강한 범위 데미지를 줍니다.");
        InitAbility(30, 0, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (ConfigManager.DamageGuard || !isOwner(Event.getPlayer()) || !isValidItem(Ability.DefaultItem))
            return -1;
        return 0;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
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
