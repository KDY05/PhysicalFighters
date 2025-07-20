package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GravityBoots extends Ability {
    Block b = null;
    Material bt;

    public GravityBoots() {
        InitAbility("중력장화", Type.Active_Immediately, Rank.S,
                "아무것도 신지않고, 철괴로 왼쪽클릭을 하면, 바라보는 블럭에 10초간 청금석이 생깁니다.",
                "능력사용자를 제외한 청금석 주변의 플레이어는 지속 데미지와 함께 청금석으로 끌려옵니다.",
                "아무것도 신지않고있는 경우 낙하데미지를 받지 않습니다.");
        InitAbility(40, 10, true);
        registerLeftClickEvent();
        EventManager.onEntityDamage.add(new EventData(this, 1));
        EventManager.onBlockBreakEvent.add(new EventData(this, 2));
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent e = (PlayerInteractEvent) event;
                if ((isOwner(e.getPlayer())) && (isValidItem(Ability.DefaultItem)) && !ConfigManager.DamageGuard) {
                    Player p = e.getPlayer();
                    if (p.getInventory().getBoots() == null) {
                        if (p.getLocation().distance(p.getTargetBlock(null, 0).getLocation()) < 30.0D) {
                            return 0;
                        }
                        p.sendMessage(ChatColor.RED + "너무 멉니다.");
                    }
                }
                break;
            case 1:
                EntityDamageEvent e2 = (EntityDamageEvent) event;
                if (isOwner(e2.getEntity())) {
                    Player p = (Player) e2.getEntity();
                    if ((p.getInventory().getBoots() == null) &&
                            (e2.getCause() == EntityDamageEvent.DamageCause.FALL)) {
                        e2.setCancelled(true);
                    }
                }
                break;
            case 2:
                BlockBreakEvent e3 = (BlockBreakEvent) event;
                if (e3.getBlock() == this.b) {
                    e3.setCancelled(true);
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        Player p = e.getPlayer();
        Block tb = p.getTargetBlock(null, 0);
        this.bt = tb.getType();
        tb.setType(Material.LAPIS_BLOCK);
        this.b = tb;
        Timer timer = new Timer();
        timer.schedule(new GTimer(), 1000L, 1000L);
    }

    public class GTimer extends TimerTask {
        private int a = 10;

        public GTimer() {
        }

        public void run() {
            Player[] arrayOfPlayer1;
            int j = (arrayOfPlayer1 = Bukkit.getOnlinePlayers().toArray(new Player[0])).length;
            for (int i = 0; i < j; i++) {
                Player p = arrayOfPlayer1[i];
                if ((GravityBoots.this.b.getLocation().distance(p.getLocation()) < 10.0D) && (p != GravityBoots.this.getPlayer())) {
                    p.teleport(GravityBoots.this.b.getLocation());
                    p.damage(3, GravityBoots.this.getPlayer());
                }
            }
            this.a -= 1;
            if (this.a <= 0) {
                Location l = GravityBoots.this.b.getLocation();
                l.setY(GravityBoots.this.b.getLocation().getY() + 1.0D);
                Player[] arrayOfPlayer2;
                int k = (arrayOfPlayer2 = Bukkit.getOnlinePlayers().toArray(new Player[0])).length;
                for (j = 0; j < k; j++) {
                    Player p = arrayOfPlayer2[j];
                    if ((GravityBoots.this.b.getLocation().distance(p.getLocation()) < 10.0D) && (p != GravityBoots.this.getPlayer())) {
                        p.teleport(l);
                        p.damage(5, GravityBoots.this.getPlayer());
                    }
                }
                GravityBoots.this.b.setType(GravityBoots.this.bt);
                GravityBoots.this.b = null;
                GravityBoots.this.bt = Material.AIR;
                cancel();
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\GravityBoots.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */