package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.AbilityBase.Rank;
import Physical.Fighters.MainModule.AbilityBase.Type;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.ACC;
import Physical.Fighters.PhysicalFighters;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Apollon
        extends AbilityBase {
    public Apollon() {
        if ((!PhysicalFighters.Toner) &&
                (PhysicalFighters.SRankUsed)) {
            InitAbility("아폴론", Type.Active_Immediately, Rank.SS, new String[]{
                    "바라보는 방향에 불구덩이를 만듭니다."});
            InitAbility(40, 0, true);
            RegisterLeftClickEvent();
        }
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((!EventManager.DamageGuard) &&
                (PlayerCheck(Event.getPlayer())) && (ItemCheck(ACC.DefaultItem))) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Location l1 = p.getTargetBlock(null, 0).getLocation();
        Location l2 = p.getTargetBlock(null, 0).getLocation();
        for (int j = 0; j <= 7; j++) {
            l2.setY(l1.getY() - j);
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                }
            }
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                }
            }
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                }
            }
            for (int i = 0; i <= 4; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                for (int k = 0; k <= 4; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.NETHERRACK);
                }
            }
        }
        for (int j = 0; j <= 6; j++) {
            l2.setY(l1.getY() - j);
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.AIR);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.AIR);
                }
            }
        }
        for (int j = 6; j <= 6; j++) {
            l2.setY(l1.getY() - j);
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.FIRE);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.FIRE);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.FIRE);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.FIRE);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() - i);
                p.getWorld().getBlockAt(l2).setType(Material.FIRE);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() + k);
                    p.getWorld().getBlockAt(l2).setType(Material.FIRE);
                }
            }
            for (int i = 0; i <= 3; i++) {
                l2.setX(l1.getX() + i);
                p.getWorld().getBlockAt(l2).setType(Material.FIRE);
                for (int k = 0; k <= 3; k++) {
                    l2.setZ(l1.getZ() - k);
                    p.getWorld().getBlockAt(l2).setType(Material.FIRE);
                }
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Apollon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */