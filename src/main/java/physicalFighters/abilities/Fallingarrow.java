package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.utils.ACC;
import physicalFighters.PhysicalFighters;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Fallingarrow
        extends AbilityBase {
    public Fallingarrow() {
        if (!PhysicalFighters.Specialability) {
            InitAbility("낙하화살", Type.Active_Immediately, Rank.A, new String[]{
                    "바라보는곳에 빠른속도로 화살 16발을 내리꽂습니다. [실내에서 사용이 안될 수 있습니다.]"});
            InitAbility(3, 0, true);
            registerLeftClickEvent();
        }
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((isOwner(Event.getPlayer())) &&
                (isValidItem(ACC.DefaultItem)) && !EventManager.DamageGuard) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Location l2 = p.getTargetBlock(null, 0).getLocation();
        Vector v = new Vector(0, -10, 0);
        l2.setY(256.0D);
        for (int i = -1; i <= 2; i++) {
            for (int j = -1; j <= 2; j++) {
                Location l = p.getTargetBlock(null, 0).getLocation();
                l.setX(l.getX() + 0.5D * i);
                if (l.getY() + 10.0D > 250.0D) {
                    l.setY(250.0D);
                } else {
                    l.setY(l.getY() + 10.0D);
                }
                l.setZ(l.getZ() + 0.5D * j);
                Arrow a = (Arrow) p.getWorld().spawn(l, Arrow.class);
                a.setVelocity(v);
                a.setShooter(p);
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Fallingarrow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */