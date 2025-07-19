package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import org.bukkit.*;
import io.github.kdy05.physicalFighters.core.Ability;

import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.utils.AUC;

public class Aokizi extends Ability {
    public Aokizi() {
        InitAbility("아오키지", Type.Active_Immediately, Rank.S,
                "철괴 좌클릭 시 자신이 보고있는 방향으로 얼음을 날립니다.",
                "철괴로 우클릭 시 바라보고 있는 5칸 이내의 물을 얼립니다.");
        InitAbility(1, 0, true, ShowText.Custom_Text);
        registerLeftClickEvent();
        registerRightClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if (isOwner(Event.getPlayer()) && isValidItem(DefaultItem) && !PhysicalFighters.DamageGuard)
                    return 0;
                break;
            case 1:
                PlayerInteractEvent Event1 = (PlayerInteractEvent) event;
                if (isOwner(Event1.getPlayer()) && isValidItem(DefaultItem) && !PhysicalFighters.DamageGuard) {
                    Player p = Event1.getPlayer();
                    Location location = AUC.getTargetLocation(p, 5);
                    if (location == null) {
                        getPlayer().sendMessage(ChatColor.GREEN + "5칸 이내의 물만 얼릴 수 있습니다.");
                        break;
                    }
                    Block block = location.getBlock();
                    if (block.getType() != Material.WATER) break;
                    block.setType(Material.ICE);
                    break;
                }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Location l = Event.getPlayer().getLocation();
        Location l2 = Event.getPlayer().getLocation();
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));
        for (int i = 1; i < 10; i++) {
            l2.setX(l.getX() + (i + 1) * (Math.sin(degrees) * Math.cos(ydeg)));
            l2.setY(l.getY() + (i + 1) * Math.sin(ydeg));
            l2.setZ(l.getZ() + (i + 1) * (Math.cos(degrees) * Math.cos(ydeg)));
            Block block = getPlayer().getWorld().getBlockAt(l2);
            if (block.getType() != Material.ICE)
                new ExplosionTimer(block.getType(), block).runTaskLater(plugin, 15L);
            block.setType(Material.ICE);
            AUC.splashDamage(getPlayer(), block.getLocation(), 3, 8);
        }
    }

    static class ExplosionTimer extends BukkitRunnable {
        private final World world;
        private final Location location;
        private final Material blockid;

        ExplosionTimer(Material blockid, Block block) {
            this.world = block.getWorld();
            this.location = block.getLocation();
            this.blockid = blockid;
        }

        public void run() {
            this.world.getBlockAt(this.location).setType(this.blockid);
        }
    }
}
