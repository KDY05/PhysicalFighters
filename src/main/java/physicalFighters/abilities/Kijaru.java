package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.utils.ACC;
import physicalFighters.utils.EventData;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Kijaru extends AbilityBase {
    public Kijaru() {
        InitAbility("키자루", Type.Active_Immediately, Rank.SS,
                "철괴로 타격을 당한 상대를 빛의 속도로 타격합니다.",
                "상대는 엄청난 속도로 멀리 날라갑니다. 당신도 상대를 따라 근접하게 날라갑니다.",
                "낙하데미지를 받지 않습니다.");
        InitAbility(45, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
        EventManager.onEntityDamage.add(new EventData(this, 3));
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 3) {
            EntityDamageEvent Event2 = (EntityDamageEvent) event;
            if ((isOwner(Event2.getEntity())) &&
                    (Event2.getCause() == DamageCause.FALL)) {
                Event2.setCancelled(true);
                getPlayer().sendMessage(ChatColor.GREEN + "사뿐하게 떨어져 데미지를 받지 않았습니다.");
            }
        } else {
            EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
            if ((isOwner(Event.getDamager())) && (isValidItem(ACC.DefaultItem)) && !EventManager.DamageGuard)
                return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Event.setDamage(8);
        Location l2 = Event.getEntity().getLocation();
        l2.setY(Event.getEntity().getLocation().getY() + 1.0D);
        Event.getEntity().teleport(l2);
        goPlayerVelocity((Player) Event.getEntity(), (Player) Event.getDamager(), -10);
        Event.getEntity().getWorld().createExplosion(Event.getEntity().getLocation(), 0.0F);
        Timer timer = new Timer();
        timer.schedule(new Kizaru(Event.getDamager(), Event.getEntity()), 1000L);
    }

    class Kizaru extends TimerTask {
        Player player22;
        Player player;

        Kizaru(Entity entity, Entity entity2) {
            this.player22 = ((Player) entity);
            this.player = ((Player) entity2);
        }

        public void run() {
            Location loc2 = this.player.getLocation();
            loc2.setY(this.player.getLocation().getY() + 2.0D);
            this.player22.teleport(loc2);
        }
    }
}
