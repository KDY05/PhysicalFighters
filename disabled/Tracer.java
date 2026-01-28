package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.game.Ability;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import java.util.LinkedList;
import java.util.Objects;
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

public class Tracer extends Ability {
    public static Timer TracerTimer;
    int core = 3;
    int aaa = 0;
    LinkedList<Location> L = new LinkedList<>();
    LinkedList<Integer> H = new LinkedList<>();

    public Tracer() {
        InitAbility("트레이서", Type.Active_Immediately, Rank.S,
                "1.철괴를 들고 쉬프트를 누르면 바라보는 방향으로 짧게 순간이동합니다.",
                "- 5초마다 코어가 하나씩 충전되며, 3개까지 충전됩니다.",
                "2.철괴를 들고 우클릭시 자신의 모든 상태를 4초전으로 돌립니다. (쿨타임:40초)",
                "*낙하대미지를 받지않습니다.");
        InitAbility(40, 0, true);
        EventManager.onPlayerToggleSneakEvent.add(new EventData(this));
        registerRightClickEvent();
        EventManager.onEntityDamage.add(new EventData(this, 3));
        EventManager.onPluginDisable.add(new EventData(this, 4));

        TracerTimer = new Timer();
        TracerTimer.schedule(new TimerTask() {
            public void run() {
                if (getPlayer() != null) {
                    if (Tracer.this.core < 3) {
                        Tracer.this.aaa += 1;
                        if (Tracer.this.aaa == 5) {
                            Tracer.this.core += 1;
                            Tracer.this.getPlayer().sendMessage(ChatColor.AQUA + "▶".repeat(Math.max(0, Tracer.this.core)));
                            Tracer.this.aaa = 0;
                        }
                    }
                    if (Tracer.this.getPlayer().isOnline()) {
                        Tracer.this.L.add(Tracer.this.getPlayer().getLocation());
                        Tracer.this.H.add((int) getPlayer().getHealth());
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

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
                if ((isOwner(e.getPlayer())) && (e.isSneaking())) {
                    if (this.core >= 1) {
                        Location s = e.getPlayer().getLocation();
                        s.setY(s.getY() + 1.6D);
                        Location l = getLocation(s, e);
                        e.getPlayer().teleport(l);
                        e.getPlayer().playEffect(s, Effect.ENDER_SIGNAL, null);
                        e.getPlayer().playEffect(l, Effect.ENDER_SIGNAL, null);
                        e.getPlayer().playSound(e.getPlayer().getLocation(),
                                Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                        this.core -= 1;
                        getPlayer().sendMessage(ChatColor.AQUA + "▶".repeat(Math.max(0, this.core + 1)));
                    } else {
                        e.getPlayer().sendMessage(ChatColor.BLUE + "코어가 부족합니다! 5초마다 자동 생성됩니다.");
                    }
                }
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                if ((isOwner(Event2.getPlayer())) &&
                        (isValidItem(Ability.DefaultItem))) {
                    return 2;
                }
                break;
        }
        if (CustomData == 3) {
            EntityDamageEvent Event2 = (EntityDamageEvent) event;
            if ((isOwner(Event2.getEntity())) &&
                    (Event2.getCause() == EntityDamageEvent.DamageCause.FALL)) {
                Event2.setCancelled(true);
                getPlayer().sendMessage(
                        ChatColor.GREEN + "사뿐하게 떨어져 대미지를 받지 않았습니다.");
            }
        }
        if (CustomData == 4) {
            if (TracerTimer != null)
                TracerTimer.cancel();
        }
        return -1;
    }

    private Location getLocation(Location s, PlayerToggleSneakEvent e) {
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
        return l;
    }

    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 2) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if ((!this.L.isEmpty()) && (this.L.getFirst() != null) && (!this.H.isEmpty())) {
                Location l = e.getPlayer().getLocation();
                Location tol = this.L.getFirst();
                Objects.requireNonNull(l.getWorld()).playEffect(l, Effect.ENDER_SIGNAL, 1);
                l.getWorld().playSound(l, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                e.getPlayer().teleport(this.L.getFirst());
                e.getPlayer().setHealth((this.H.getFirst()));
                Objects.requireNonNull(tol.getWorld()).playEffect(tol, Effect.ENDER_SIGNAL, 1);
                e.getPlayer().sendMessage("능력을 사용하여 4초전으로 되돌립니다.");
            } else {
                e.getPlayer().sendMessage("타이머로 좌표를 불러오는데 오류가 발생했습니다.");
            }
        }
    }
}
