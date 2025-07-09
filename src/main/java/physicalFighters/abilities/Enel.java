package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.PhysicalFighters;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import physicalFighters.utils.ACC;

public class Enel extends AbilityBase {
    public Enel() {
        if (PhysicalFighters.SRankUsed) {
            InitAbility("갓 에넬", Type.Active_Immediately, Rank.S, new String[]{
                    "바라보는 방향으로 번개를 연속발사합니다."});
            InitAbility(30, 0, true);
            registerLeftClickEvent();
        }
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((!EventManager.DamageGuard) &&
                (isOwner(Event.getPlayer())) && (isValidItem(ACC.DefaultItem))) {
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
            l2.setX(l.getX() + 1 * i + 4.0D * Math.sin(degrees));
            l2.setZ(l.getZ() + 1 * i + 4.0D * Math.cos(degrees));
            l2.getWorld().strikeLightning(l2);
            Player[] List = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            Player[] arrayOfPlayer1;
            int j = (arrayOfPlayer1 = List).length;
            for (int k = 0; k < j; k++) {
                Player pp = arrayOfPlayer1[k];
                if (pp != getPlayer()) {
                    Location loc = pp.getLocation();
                    if ((l2.getWorld().getBlockAt(l2).getLocation().distance(loc) <= 3.0D) &&
                            (!EventManager.DamageGuard)) {
                        pp.damage(15);
                    }
                }
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Enel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */