package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.EventData;
import Physical.Fighters.PhysicalFighters;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.BlockIterator;

public class Tracer extends AbilityBase {
    int core = 3;
    int aaa = 0;
    LinkedList<Location> L = new LinkedList<Location>();
    LinkedList<Integer> H = new LinkedList<Integer>();

    public Tracer() {
        if (PhysicalFighters.SRankUsed) {
            InitAbility(
                    "트레이서",
                    Type.Active_Immediately,
                    Rank.S,
                    new String[]{
                            "1.철괴를 들고 쉬프트를 누르면 바라보는 방향으로 짧게 순간이동합니다.",
                            "- 5초마다 코어가 하나씩 충전되며, 3개까지 충전됩니다.",
                            "2.철괴를 들고 우클릭시 자신의 모든 상태를 4초전으로 돌립니다. (쿨타임:40초)",
                            "*낙하데미지를 받지않습니다."});
            InitAbility(40, 0, true);
            EventManager.onPlayerToggleSneakEvent.add(new EventData(this));
            RegisterRightClickEvent();
            EventManager.onEntityDamage.add(new EventData(this, 3));
            PhysicalFighters.TracerTimer = new Timer();
            PhysicalFighters.TracerTimer.schedule(new TimerTask() {
                public void run() {
                    if (Tracer.this.hasPlayer()) {
                        if (Tracer.this.core < 3) {
                            Tracer.this.aaa += 1;
                            if (Tracer.this.aaa == 5) {
                                Tracer.this.core += 1;
                                String s = "";
                                for (int i = 0; i < Tracer.this.core; i++) {
                                    s = s + "▶";
                                }
                                Tracer.this.GetPlayer().sendMessage(ChatColor.AQUA + s);
                                Tracer.this.aaa = 0;
                            }
                        }
                        if (Tracer.this.GetPlayer().isOnline()) {
                            Tracer.this.L.add(Tracer.this.GetPlayer().getLocation());
                            Tracer.this.H.add((int) GetPlayer().getHealth());
                            if (Tracer.this.L.size() > 4) {
                                Tracer.this.L.removeFirst();
                            }
                            if (Tracer.this.H.size() > 4) {
                                Tracer.this.H.removeFirst();
                            }
                        }
                    }
                }
            }, 1000L, 1000L);
        }
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
                if ((PlayerCheck(e.getPlayer())) && (e.isSneaking())) {
                    if (this.core >= 1) {
                        Location s = e.getPlayer().getLocation();
                        s.setY(s.getY() + 1.6D);
                        Block b = s.getBlock();
                        BlockIterator bi = new BlockIterator(e.getPlayer(), 5);
                        while (bi.hasNext()) {
                            Block bb = bi.next();
                            if ((bb.getType().isSolid()) && (bb.getType() != Material.AIR)) break;
                            b = bb;
                        }
                        Location l = b.getLocation();
                        l.setPitch(s.getPitch());
                        l.setYaw(s.getYaw());
                        e.getPlayer().teleport(l);
                        e.getPlayer().playEffect(
                                s,
                                Effect.ENDER_SIGNAL, 0);
                        e.getPlayer().playEffect(
                                l,
                                Effect.ENDER_SIGNAL, 0);
                        e.getPlayer().playSound(
                                e.getPlayer().getLocation(),
                                Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                        this.core -= 1;
                        String ss = "";
                        for (int i = 0; i <= this.core; i++) {
                            ss = ss + "▶";
                        }
                        GetPlayer().sendMessage(ChatColor.AQUA + ss);
                    } else {
                        e.getPlayer().sendMessage(ChatColor.BLUE + "코어가 부족합니다! 5초마다 자동 생성됩니다.");
                    }
                }
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                if ((PlayerCheck(Event2.getPlayer())) &&
                        (ItemCheck(Physical.Fighters.MinerModule.ACC.DefaultItem))) {
                    return 2;
                }
                break;
        }
        if (CustomData == 3) {
            EntityDamageEvent Event2 = (EntityDamageEvent) event;
            if ((PlayerCheck(Event2.getEntity())) &&
                    (Event2.getCause() == EntityDamageEvent.DamageCause.FALL)) {
                Event2.setCancelled(true);
                GetPlayer().sendMessage(
                        ChatColor.GREEN + "사뿐하게 떨어져 데미지를 받지 않았습니다.");
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 2:
                PlayerInteractEvent e = (PlayerInteractEvent) event;
                if ((!this.L.isEmpty()) && (this.L.getFirst() != null) && (!this.H.isEmpty())) {
                    Location l = e.getPlayer().getLocation();
                    Location tol = (Location) this.L.getFirst();
                    l.getWorld().playEffect(l, Effect.ENDER_SIGNAL, 1);
                    l.getWorld().playSound(l, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                    e.getPlayer().teleport((Location) this.L.getFirst());
                    e.getPlayer().setHealth((this.H.getFirst()));
                    tol.getWorld().playEffect(tol, Effect.ENDER_SIGNAL, 1);
                    e.getPlayer().sendMessage("능력을 사용하여 4초전으로 되돌립니다.");
                } else {
                    e.getPlayer().sendMessage("타이머로 좌표를 불러오는데 오류가 발생했다고 염료한테 말하세요.");
                }
                break;
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Tracer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */