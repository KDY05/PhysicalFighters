package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.ACC;
import Physical.Fighters.MinerModule.EventData;
import Physical.Fighters.PhysicalFighters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Aegis extends AbilityBase {
    public Aegis() {
        InitAbility("이지스", Type.Active_Continue, Rank.A, "능력 사용시 일정시간동안 무적이 됩니다. 무적은 대부분의",
                "데미지를 무력화시키며 능력 사용중엔 Mirroring 능력도 ", "무력화됩니다.");
        InitAbility(28, 6, true);
        RegisterLeftClickEvent();
        EventManager.onEntityDamage.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (!PhysicalFighters.ReverseMode) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            if ((PlayerCheck(Event.getPlayer())) && (ItemCheck(ACC.DefaultItem)) && !EventManager.DamageGuard)
                return 0;
        } else {
            EntityDamageEvent Event = (EntityDamageEvent) event;
            if (PlayerCheck(Event.getEntity()))
                return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageEvent Event = (EntityDamageEvent) event;
        if (!PhysicalFighters.ReverseMode) {
            if (PlayerCheck(Event.getEntity())) {
                Player p = (Player) Event.getEntity();
                p.setFireTicks(0);
                Event.setCancelled(true);
            }
        } else {
            Event.setDamage((int) (Event.getDamage() * 1000.0D));
        }
    }
}
