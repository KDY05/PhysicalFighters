package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.AbilityBase.Rank;
import Physical.Fighters.MainModule.AbilityBase.Type;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.ACC;
import Physical.Fighters.MinerModule.EventData;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Roclee extends AbilityBase {
    public Roclee() {
        InitAbility("록리", Type.Active_Immediately, Rank.S, new String[]{
                "철괴로 상대를 타격할시 맞은사람을 매우빠른속도로 높이 띄웁니다."});
        InitAbility(20, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if ((PlayerCheck(Event.getDamager())) && (ItemCheck(ACC.DefaultItem)) && !EventManager.DamageGuard) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Player p = (Player) Event.getEntity();
        Location l1 = Event.getEntity().getLocation();
        Location l2 = Event.getEntity().getLocation();
        Location l = GetPlayer().getLocation();
        Location ll = GetPlayer().getLocation();
        Event.getEntity().getWorld()
                .createExplosion(Event.getEntity().getLocation(), 0.0F);
        l2.setY(l1.getY() + 8.0D);
        p.teleport(l2);
        Event.getEntity().getWorld().createExplosion(l2, 1.0F);
        ll.setY(l.getY() + 8.0D);
        p.damage(8);
        GetPlayer().teleport(ll);
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Roclee.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */