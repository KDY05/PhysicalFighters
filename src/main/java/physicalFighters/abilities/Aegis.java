package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.utils.ACC;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Aegis extends AbilityBase {
    public Aegis() {
        InitAbility("이지스", Type.Active_Continue, Rank.A,
                "능력 사용시 일정시간동안 무적이 됩니다. 무적은 대부분의",
                "데미지를 무력화시키며 능력 사용중엔 Mirroring 능력도 ", "무력화됩니다.");
        InitAbility(28, 6, true);
        registerLeftClickEvent();
        EventManager.onEntityDamage.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (!PhysicalFighters.ReverseMode) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            if ((isOwner(Event.getPlayer())) && (isValidItem(ACC.DefaultItem)) && !EventManager.DamageGuard)
                return 0;
        } else {
            EntityDamageEvent Event = (EntityDamageEvent) event;
            if (isOwner(Event.getEntity()))
                return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageEvent Event = (EntityDamageEvent) event;
        if (!PhysicalFighters.ReverseMode) {
            if (isOwner(Event.getEntity())) {
                Player p = (Player) Event.getEntity();
                p.setFireTicks(0);
                Event.setCancelled(true);
            }
        } else {
            Event.setDamage((int) (Event.getDamage() * 1000.0D));
        }
    }
}
