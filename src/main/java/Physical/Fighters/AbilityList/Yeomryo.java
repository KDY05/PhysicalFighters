package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.EventData;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Yeomryo
        extends AbilityBase {
    public static int guard = 30;
    private Timer t = new Timer();

    public Yeomryo() {
        InitAbility("야스오", Type.Passive_Manual, Rank.S, new String[]{
                "데미지를 흡수하는 30의 보호막을 얻으며", "공격시 피격 대상에게 3의 추가데미지를 입힙니다.", "보호막은 5초당 10씩 회복됩니다."});
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 2));
        EventManager.onEntityDamage.add(new EventData(this, 3));
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 2) {
            EntityDamageByEntityEvent e2 = (EntityDamageByEntityEvent) event;
            if (PlayerCheck(e2.getDamager())) {
                if ((e2.getEntity() instanceof Player)) {
                    e2.setDamage((int) (e2.getDamage() + 3.0D));
                } else {
                    e2.setDamage(50);
                }
            }
        } else if (CustomData == 3) {
            EntityDamageEvent e3 = (EntityDamageEvent) event;
            if (PlayerCheck(e3.getEntity())) {
                Player p = (Player) e3.getEntity();
                if (guard > 0) {
                    guard = (int) (guard - e3.getDamage());
                    if (guard <= 0) {
                        p.sendMessage(ChatColor.YELLOW + "보호막으로 " + (e3.getDamage() - guard) + "의 데미지를 흡수했습니다. (남은 보호막:" + guard + ")");
                        p.sendMessage(ChatColor.RED + "보호막이 깨졌습니다!");
                        e3.setDamage(e3.getDamage() - guard);
                        guard = 0;
                    } else {
                        p.sendMessage(ChatColor.YELLOW + "보호막으로 " + e3.getDamage() + "의 데미지를 흡수했습니다. (남은 보호막:" + guard + ")");
                        e3.setDamage(0);
                    }
                }
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
    }

    class Heal
            extends TimerTask {
        Player p;

        public Heal(Player pp) {
            this.p = pp;
        }

        public void run() {
            if (Yeomryo.guard != 30) {
                if (Yeomryo.guard + 10 >= 30) {
                    Yeomryo.guard = 30;
                    this.p.sendMessage(ChatColor.GREEN + "보호막이 완전히 회복되었습니다.");
                } else {
                    Yeomryo.guard += 10;
                    this.p.sendMessage(ChatColor.GREEN + "보호막이 10 회복되었습니다 (보호막 체력:" + Yeomryo.guard + ")");
                }
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Yeomryo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */