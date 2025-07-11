package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.PhysicalFighters;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Gaara extends Ability {
    public Gaara() {
        InitAbility("가아라", Type.Active_Immediately, Rank.B,
                "철괴 좌클릭 시 바라보는 방향에 모래를 떨어뜨리고 폭발시킨다.");
        InitAbility(30, 0, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Block b = p.getTargetBlock(null, 0);
        Location loc = b.getLocation();
        Location ploc = p.getLocation();
        if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem))) {
            if ((loc.distance(ploc) <= 40.0D) && (b.getY() != 0)) {
                if (!EventManager.DamageGuard)
                    return 0;
            } else
                p.sendMessage(String.format(ChatColor.RED + "거리가 너무 멉니다."));
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Location l1 = p.getTargetBlock(null, 0).getLocation();
        Location l2 = p.getTargetBlock(null, 0).getLocation();
        Block block = Event.getPlayer().getTargetBlock(null, 0);
        new ExplosionTimer(block).runTaskLater(PhysicalFighters.getPlugin(), 80L);
        for (int j = 4; j <= 8; j++) {
            l2.setY(l1.getY() + j);
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() + i);
                if (p.getWorld().getBlockAt(l2).getType() != Material.BEDROCK)
                    p.getWorld().getBlockAt(l2).setType(Material.SAND);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() + k);
                    if (p.getWorld().getBlockAt(l2).getType() != Material.BEDROCK)
                        p.getWorld().getBlockAt(l2).setType(Material.SAND);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() - i);
                if (p.getWorld().getBlockAt(l2).getType() != Material.BEDROCK)
                    p.getWorld().getBlockAt(l2).setType(Material.SAND);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() - k);
                    if (p.getWorld().getBlockAt(l2).getType() != Material.BEDROCK)
                        p.getWorld().getBlockAt(l2).setType(Material.SAND);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() - i);
                if (p.getWorld().getBlockAt(l2).getType() != Material.BEDROCK)
                    p.getWorld().getBlockAt(l2).setType(Material.SAND);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() + k);
                    if (p.getWorld().getBlockAt(l2).getType() != Material.BEDROCK)
                        p.getWorld().getBlockAt(l2).setType(Material.SAND);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() + i);
                if (p.getWorld().getBlockAt(l2).getType() != Material.BEDROCK)
                    p.getWorld().getBlockAt(l2).setType(Material.SAND);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() - k);
                    if (p.getWorld().getBlockAt(l2).getType() != Material.BEDROCK)
                        p.getWorld().getBlockAt(l2).setType(Material.SAND);
                }
            }
        }
    }

    static class ExplosionTimer extends BukkitRunnable {
        World world;
        Location location;

        ExplosionTimer(Block block) {
            this.world = block.getWorld();
            this.location = block.getLocation();
        }

        @Override
        public void run() {
            this.world.createExplosion(this.location, 5.0F);
            this.world.createExplosion(this.location, 5.0F);
            this.world.createExplosion(this.location, 5.0F);
        }
    }
}
