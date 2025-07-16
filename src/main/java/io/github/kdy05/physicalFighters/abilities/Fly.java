package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

public class Fly extends Ability {
    public Fly() {
        InitAbility("플라이", Type.Active_Immediately, Rank.GOD,
                "철괴를 휘두를시에 10초간 하늘을 날라다닐 수 있습니다.",
                "낙하 데미지를 받지 않습니다.");
        InitAbility(60, 0, true);
        registerLeftClickEvent();
        EventManager.onEntityDamage.add(new EventData(this, 3));
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem))) {
                return 0;
            }
        } else if (CustomData == 3) {
            EntityDamageEvent Event2 = (EntityDamageEvent) event;
            if ((isOwner(Event2.getEntity())) &&
                    (Event2.getCause() == DamageCause.FALL)) {
                getPlayer().sendMessage(
                        ChatColor.GREEN + "사뿐하게 떨어져 데미지를 받지 않았습니다.");
                Event2.setCancelled(true);
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        getPlayer().setAllowFlight(true);
        getPlayer().setFlying(true);
        Timer timer = new Timer();
        timer.schedule(new offTimer(), 10000L);
    }

    class offTimer extends TimerTask {
        offTimer() {
        }

        public void run() {
            Fly.this.getPlayer().setFlying(false);
            Fly.this.getPlayer().setAllowFlight(false);
            Fly.this.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "지속시간이 끝났습니다.");
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Fly.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */