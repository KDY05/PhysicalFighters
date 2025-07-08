package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.EventData;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;

public class Paladin
        extends AbilityBase {
    boolean candmg = true;

    public Paladin() {
        InitAbility("팔라딘", Type.Passive_AutoMatic, Rank.A, new String[]{
                "칼을 들었을 때 다수의 적에게 10~20의 랜덤데미지를 가하며 사거리가 두배가 됩니다. 단, 공격속도가 감소합니다."});
        InitAbility(0, 0, true);
        RegisterLeftClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 1));
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if (PlayerCheck(e.getPlayer()) && !EventManager.DamageGuard) {
                return 0;
            }
        }
        if (CustomData == 1) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if ((PlayerCheck(e.getDamager())) &&
                    (!this.candmg)) {
                e.setCancelled(true);
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if ((PlayerCheck(e.getPlayer())) &&
                    (isSword(e.getPlayer().getInventory().getItemInMainHand()))) {
                BlockIterator bi = new BlockIterator(e.getPlayer(), 6);
                Random r = new Random();
                int i = r.nextInt(10) + 10;
                while (bi.hasNext()) {
                    Block bb = bi.next();
                    ExplosionDMG(e.getPlayer(), bb.getLocation(), 3, i);
                }
                e.getPlayer().sendMessage(ChatColor.GREEN + "" + i + "의 데미지를 가했습니다.");
                this.candmg = false;
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        Paladin.this.candmg = true;
                    }
                }, 2000L);
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Paladin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */