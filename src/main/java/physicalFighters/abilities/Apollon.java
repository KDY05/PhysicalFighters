package physicalFighters.abilities;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import physicalFighters.utils.AUC;

public class Apollon extends Ability {
    // 타겟 위치 저장
    private Location targetLocation = null;

    public Apollon() {
        InitAbility("아폴론", Type.Active_Immediately, Rank.S,
                "철괴를 휘둘러 바라보는 방향에 불구덩이를 만듭니다.");
        InitAbility(30, 0, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();

        if (!isOwner(p) || !isValidItem(Ability.DefaultItem)) {
            return -1;
        }

        if (EventManager.DamageGuard) {
            p.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
            return -1;
        }

        targetLocation = AUC.getTargetLocation(p, 40);
        if (targetLocation == null) {
            p.sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
            return -1;
        }

        return 0;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();

        if (targetLocation == null) {
            p.sendMessage(ChatColor.RED + "능력을 사용할 수 없습니다.");
            return;
        }

        Location center = targetLocation.clone();
        buildNetherrackWalls(p, center);
        digInnerSpace(p, center);
        placeFire(p, center);
        p.sendMessage(ChatColor.GOLD + "불구덩이를 만들었습니다!");

        targetLocation = null;
    }

    private void buildNetherrackWalls(Player player, Location center) {
        for (int depth = 0; depth <= 7; depth++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    Location blockLoc = center.clone().add(x, -depth, z);
                    Block block = player.getWorld().getBlockAt(blockLoc);
                    block.setType(Material.NETHERRACK);
                }
            }
        }
    }

    private void digInnerSpace(Player player, Location center) {
        for (int depth = 0; depth <= 6; depth++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    Location blockLoc = center.clone().add(x, -depth, z);
                    Block block = player.getWorld().getBlockAt(blockLoc);
                    block.setType(Material.AIR);
                }
            }
        }
    }

    private void placeFire(Player player, Location center) {
        int fireDepth = 6; // 최하층
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                Location fireLoc = center.clone().add(x, -fireDepth, z);
                Block fireBlock = player.getWorld().getBlockAt(fireLoc);
                fireBlock.setType(Material.FIRE);
            }
        }
    }

}
