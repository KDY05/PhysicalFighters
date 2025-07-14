package physicalFighters.abilities;

import physicalFighters.PhysicalFighters;
import physicalFighters.core.EventManager;
import physicalFighters.core.Ability;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Magnet extends Ability {
    public Magnet() {
        InitAbility("자석", Type.Active_Immediately, Rank.S, new String[]{
                "철괴를 들고 왼쪽클릭시, 20칸안에 있는 모든 적을 자신의 방향으로,",
                "철괴를 들고 오른쪽클릭시, 20칸안에 있는 모든 적을 자신의 반대방향으로 날려버립니다."});
        InitAbility(40, 0, true);
        registerLeftClickEvent();
        registerRightClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        switch (CustomData) {
            case 0:
                if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem)) && !EventManager.DamageGuard) {
                    return 0;
                }
                break;
            case 1:
                if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem)) && !EventManager.DamageGuard) {
                    return 1;
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        LinkedList<Player> ts = new LinkedList();
        for (int i = 0; i < (Bukkit.getOnlinePlayers()).size(); i++) {
            if ((p.getLocation().distance((Bukkit.getOnlinePlayers().toArray(new Player[0]))[i].getLocation()) < 20.0D)
                    && ((Bukkit.getOnlinePlayers().toArray(new Player[0]))[i] != p)
                    && ((Bukkit.getOnlinePlayers().toArray(new Player[0]))[i].getGameMode() != org.bukkit.GameMode.CREATIVE)) {
                ts.add((Bukkit.getOnlinePlayers().toArray(new Player[0]))[i]);
            }
        }
        if (!ts.isEmpty()) {
            Location l = p.getLocation();
            l.setY(l.getY() + 2.0D);
            Location l2 = p.getLocation();
            l2.setY(l.getY() - 3.0D);
            switch (CustomData) {
                case 0:
                    for (int i = 0; i < ts.size(); i++) {
                        Player t = (Player) ts.get(i);
                        t.setVelocity(t.getVelocity().add(
                                t.getLocation().toVector()
                                        .subtract(
                                                l.toVector())
                                        .normalize().multiply(-3)));
                    }
                    ts.clear();
                    break;
                case 1:
                    for (int i = 0; i < ts.size(); i++) {
                        Player t = (Player) ts.get(i);
                        t.setVelocity(t.getVelocity().add(
                                t.getLocation().toVector()
                                        .subtract(
                                                l2.toVector())
                                        .normalize().multiply(3)));
                    }
                    ts.clear();
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Magnet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */