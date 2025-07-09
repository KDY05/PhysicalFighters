package physicalFighters.abilities;

import physicalFighters.PhysicalFighters;
import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import physicalFighters.utils.ACC;

public class Fittingroom extends AbilityBase {
    public Fittingroom() {
        if (!PhysicalFighters.Toner) {
            InitAbility(
                    "탈의실",
                    Type.Active_Immediately,
                    Rank.SSS,
                    new String[]{"능력 사용시 자기 자신을 제외한 모든 플레이어가 손에 쥐고있는 아이템을 떨어뜨립니다."});
            InitAbility(160, 0, true);
            registerLeftClickEvent();
        }
    }

    public int A_Condition(Event event, int CustomData) {
        if (!EventManager.DamageGuard) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            if ((isOwner(Event.getPlayer())) &&
                    (isValidItem(ACC.DefaultItem)))
                return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player[] t = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        Player p = Event.getPlayer();
        World w = p.getWorld();
        for (int i = 0; i < (Bukkit.getOnlinePlayers()).size(); i++) {
            if ((t[i].getGameMode() != GameMode.CREATIVE) && (t[i] != p) && (t[i].getInventory().getItemInMainHand().getType() != Material.AIR)) {
                w.dropItem(t[i].getLocation(), t[i].getInventory().getItemInMainHand());
                t[i].getInventory().remove(t[i].getInventory().getItemInMainHand());
            }
        }
        Bukkit.broadcastMessage(org.bukkit.ChatColor.AQUA + p.getName() +
                "님이 능력을 사용해 모든 플레이어의 무장을 해체시켰습니다.");
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Fittingroom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */