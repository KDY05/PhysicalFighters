package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.utils.ACC;
import physicalFighters.utils.EventData;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class gongban extends AbilityBase {
    public static boolean ppon = false;

    public gongban() {
        InitAbility("공격반사", Type.Active_Immediately, Rank.S, new String[]{
                "철괴 좌클릭으로 능력을 사용합니다.",
                "능력 사용 후 5초간 받는 모든 데미지를 돌려줍니다."});
        InitAbility(60, 0, true);
        registerLeftClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 1));
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if ((isOwner(e.getPlayer())) && (isValidItem(ACC.DefaultItem)) && !EventManager.DamageGuard) {
                return 0;
            }
        }
        if (CustomData == 1) {
            EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
            if ((isOwner(Event.getEntity())) && (ppon) &&
                    ((Event.getDamager() instanceof Player)) && !EventManager.DamageGuard) {
                Player p = (Player) Event.getEntity();
                Player t = (Player) Event.getDamager();
                t.damage(Event.getDamage(), p);
                Event.setCancelled(true);
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        Bukkit.broadcastMessage(ChatColor.GRAY + "지금부터 5초간 " + e.getPlayer().getName() + "님을 공격시 가한 데미지를 돌려받습니다.");
        ppon = true;
        Timer timer = new Timer();
        timer.schedule(new offTimer(), 5000L);
    }

    class offTimer extends TimerTask {
        offTimer() {
        }

        public void run() {
            gongban.ppon = false;
            gongban.this.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "지속시간이 끝났습니다.");
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\gongban.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */