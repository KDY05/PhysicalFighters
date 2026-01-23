package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ExplosionPa extends Ability {
    LinkedList<String> gigong = new LinkedList();
    HashMap<String, Location> pLoc = new HashMap();

    public ExplosionPa() {
        InitAbility(
                "기공파",
                Type.Active_Immediately,
                Rank.S,
                new String[]{"바라보는 방향으로 강한폭발을 여러차례 일으킵니다. 시전시간은 약 5초정도 되며, 5초간 움직일 수 없습니다."});
        InitAbility(40, 0, true);
        registerLeftClickEvent();
        EventManager.onPlayerMoveEvent.add(new EventData(this, 1));
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if ((isOwner(Event.getPlayer())) &&
                        (isValidItem(Ability.DefaultItem)) && !ConfigManager.DamageGuard)
                    return 0;
                break;
            case 1:
                PlayerMoveEvent Event2 = (PlayerMoveEvent) event;
                if ((isOwner(Event2.getPlayer())) &&
                        (this.gigong.contains(Event2.getPlayer().getName()))) {
                    Event2.getPlayer().teleport((Location) this.pLoc.get(Event2.getPlayer().getName()));
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        this.gigong.add(getPlayer().getName());
        this.pLoc.put(getPlayer().getName(), getPlayer().getLocation());
        Timer timer = new Timer();
        timer.schedule(new giTimer(), 500L, 500L);
    }

    public class giTimer extends java.util.TimerTask {
        private int ab = 10;
        private int i = 0;

        public giTimer() {
        }

        public void run() {
            Location l = ExplosionPa.this.getPlayer().getLocation();
            Location l2 = ExplosionPa.this.getPlayer().getLocation();
            double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
            double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));
            l2.setX(l.getX() + (2 * this.i + 2) * (
                    Math.sin(degrees) * Math.cos(ydeg)));
            l2.setY(l.getY() + (2 * this.i + 2) * Math.sin(ydeg));
            l2.setZ(l.getZ() + (2 * this.i + 2) * (
                    Math.cos(degrees) * Math.cos(ydeg)));
            l2.getWorld().createExplosion(l2, 0.0F);
            Player[] List = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            Player[] arrayOfPlayer1;
            int k = (arrayOfPlayer1 = List).length;
            for (int j = 0; j < k; j++) {
                Player pp = arrayOfPlayer1[j];
                if (pp != ExplosionPa.this.getPlayer()) {
                    Location loc = pp.getLocation();
                    if (l2.getWorld().getBlockAt(l2).getLocation().distance(loc) <= 4.0D) {
                        pp.damage(10, ExplosionPa.this.getPlayer());
                    }
                }
            }
            this.ab -= 1;
            this.i += 1;
            if (this.ab <= 0) {
                ExplosionPa.this.gigong.remove(ExplosionPa.this.getPlayer().getName());
                ExplosionPa.this.pLoc.remove(ExplosionPa.this.getPlayer().getName());
                cancel();
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\ExplosionPa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */