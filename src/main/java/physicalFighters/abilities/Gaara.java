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

    private Location targetLocation = null;

    public Gaara() {
        InitAbility("가아라", Type.Active_Immediately, Rank.B,
                "철괴 좌클릭 시 바라보는 방향에 모래를 떨어뜨리고",
                "잠시 후 폭발시킵니다. (바닥을 조준하세요.)");
        InitAbility(30, 0, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();

        if (!isOwner(Event.getPlayer()) || !isValidItem(Ability.DefaultItem) || EventManager.DamageGuard) {
            return -1;
        }

        // 플레이어 시선을 따라가며 첫 번째 블록 찾기
        Location eyeLoc = p.getEyeLocation();
        targetLocation = null;
        for (double distance = 0.5; distance <= 40.0; distance += 0.5) {
            Location checkLoc = eyeLoc.clone().add(eyeLoc.getDirection().multiply(distance));
            Block checkBlock = p.getWorld().getBlockAt(checkLoc);
            if (checkBlock.getType().isSolid()) {
                targetLocation = checkBlock.getLocation().clone();
                break;
            }
        }

        // 40블록 내에 블록을 찾지 못한 경우
        if (targetLocation == null) {
            p.sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
            return -1;
        }

        return 0;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();

        if (targetLocation == null) {
            p.sendMessage(ChatColor.RED + "능력을 사용할 수 없습니다.");
            return;
        }

        Location l1 = targetLocation.clone();
        Location l2 = l1.clone();
        Block targetBlock = p.getWorld().getBlockAt(l1);
        new ExplosionTimer(targetBlock).runTaskLater(plugin, 80L);

        // 4방향으로 7x7 범위에 모래 블록 생성
        for (int j = 4; j <= 8; j++) {
            l2.setY(l1.getY() + j);
            for (int i = -3; i <= 3; i++) {
                for (int k = -3; k <= 3; k++) {
                    l2.setX(l1.getX() + i);
                    l2.setZ(l1.getZ() + k);

                    Block currentBlock = p.getWorld().getBlockAt(l2);
                    if (currentBlock.getType() != Material.BEDROCK) {
                        currentBlock.setType(Material.SAND);
                    }
                }
            }
        }
        targetLocation = null;
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