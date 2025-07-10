package physicalFighters.abilities;

import physicalFighters.PhysicalFighters;
import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Angel extends Ability {
    public static String pp = "false";
    public static boolean ppon = false;

    public Angel() {
        if (PhysicalFighters.Gods) {
            InitAbility("천사", Type.Active_Immediately, Rank.GOD, new String[]{
                    "철괴로 타격받은 대상에게 10초간 자신이 받는 데미지의 반을 흡수시킵니다.",
                    "독, 질식, 낙하 데미지를 받지 않습니다."});
            InitAbility(80, 0, true);
            EventManager.onEntityDamageByEntity.add(new EventData(this));
            EventManager.onEntityDamage.add(new EventData(this, 3));
        }
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
            if ((isOwner(Event.getDamager())) && (isValidItem(Ability.DefaultItem)) &&
                    (!EventManager.DamageGuard) &&
                    (pp == "false") && (!ppon)) {
                return 0;
            }
            if ((isOwner(Event.getEntity())) &&
                    (!EventManager.DamageGuard) &&
                    (pp != "false") && (ppon)) {
                org.bukkit.Bukkit.getPlayer(pp).damage((int) (Event.getDamage() / 2.0D), Event.getEntity());
                Event.setDamage((int) (Event.getDamage() / 2.0D));
            }
        } else if (CustomData == 3) {
            EntityDamageEvent Event2 = (EntityDamageEvent) event;
            if (isOwner(Event2.getEntity())) {
                if ((Event2.getCause() == DamageCause.POISON) ||
                        (Event2.getCause() == DamageCause.DROWNING)) {
                    Event2.setCancelled(true);
                } else if (Event2.getCause() == DamageCause.FALL) {
                    getPlayer().sendMessage(
                            ChatColor.GREEN + "사뿐하게 떨어져 데미지를 받지 않았습니다.");
                    Event2.setCancelled(true);
                }
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        pp = ((Player) Event.getEntity()).getName();
        getPlayer().sendMessage(ChatColor.GREEN + ((Player) Event.getEntity()).getName() + "님은 이제 10초간 당신의 데미지의 반을 흡수합니다.");
        ((Player) Event.getEntity()).sendMessage(ChatColor.RED + "당신은 10초간 " + getPlayer().getName() + "님이 받는 데미지의 반을 흡수합니다.");
        ppon = true;
        Timer timer = new Timer();
        timer.schedule(new offTimer(), 10000L);
    }

    class offTimer extends TimerTask {
        offTimer() {
        }

        public void run() {
            Angel.ppon = false;
            Angel.pp = "false";
            Angel.this.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "지속시간이 끝났습니다.");
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Angel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */