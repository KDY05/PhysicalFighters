package physicalFighters.abilities;

import physicalFighters.core.Ability;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Clocking extends Ability {
    public Clocking() {
        InitAbility("클로킹", Type.Active_Continue, Rank.A, new String[]{
                "능력 사용시 일정시간동안 다른 사람에게 보이지 않습니다.",
                "클로킹 상태에서는 타인에게 공격 받지 않습니다."});
        InitAbility(35, 5, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem)))
            return 0;
        return -1;
    }

    public void A_DurationStart() {
        Player[] List = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        Player[] arrayOfPlayer1;
        int j = (arrayOfPlayer1 = List).length;
        for (int i = 0; i < j; i++) {
            Player p = arrayOfPlayer1[i];
            p.hidePlayer(getPlayer());
        }
    }

    public void A_FinalDurationEnd() {
        if (getPlayer() != null) {
            Player[] List = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            if ((List != null) && (List.length != 0)) {
                Player[] arrayOfPlayer1;
                int j = (arrayOfPlayer1 = List).length;
                for (int i = 0; i < j; i++) {
                    Player p = arrayOfPlayer1[i];
                    p.showPlayer(getPlayer());
                }
            }
        }
    }

    public void A_Effect(Event event, int CustomData) {
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Clocking.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */