package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MainModule.AbilityBase.Rank;
import Physical.Fighters.MainModule.AbilityBase.ShowText;
import Physical.Fighters.MainModule.AbilityBase.Type;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Aokizi extends AbilityBase {
    public Aokizi() {
        InitAbility("아오키지", Type.Active_Immediately,
                Rank.S, new String[]{
                        "철괴로 왼쪽클릭시 자신이 보고있는 방향으로 얼음을 날립니다.",
                        "철괴로 오른쪽클릭시 자신이 바라보고 있는 5칸 이내의 물을 얼려버립니다."});
        InitAbility(1, 0, true, ShowText.Custom_Text);
        RegisterLeftClickEvent();
        RegisterRightClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            if ((PlayerCheck(Event.getPlayer())) &&
                    (ItemCheck(Material.IRON_INGOT)) && !EventManager.DamageGuard)
                return 0;
        } else if (CustomData == 1) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            if ((PlayerCheck(Event.getPlayer())) &&
                    (ItemCheck(Material.IRON_INGOT)) && !EventManager.DamageGuard) {
                Player p = Event.getPlayer();
                Block block = p.getTargetBlock(null, 0);
                if (block.getType() == Material.WATER) {
                    if (p.getLocation().distance(block.getLocation()) <= 5.0D) {
                        block.setType(Material.ICE);
                    } else {
                        GetPlayer().sendMessage(
                                org.bukkit.ChatColor.GREEN +
                                        "너무 멉니다 [5칸이내의 물만 얼릴 수 있습니다.]");
                    }
                }
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Location l = Event.getPlayer().getLocation();
        Location l2 = Event.getPlayer().getLocation();
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));
        Timer timer = new Timer();
        for (int i = 1; i < 10; i++) {
            l2.setX(l.getX() + (1 * i + 1) * (
                    Math.sin(degrees) * Math.cos(ydeg)));
            l2.setY(l.getY() + (1 * i + 1) * Math.sin(ydeg));
            l2.setZ(l.getZ() + (1 * i + 1) * (
                    Math.cos(degrees) * Math.cos(ydeg)));
            if (l2.getWorld().getBlockAt(l2).getType() != Material.ICE)
                timer.schedule(new ExplosionTimer2(l2.getWorld().getBlockAt(l2)
                        .getType(), l2.getWorld().getBlockAt(l2)), 988L);
            l2.getWorld().getBlockAt(l2).setType(Material.ICE);
            Player[] List = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            Player[] arrayOfPlayer1;
            int j = (arrayOfPlayer1 = List).length;
            for (int k = 0; k < j; k++) {
                Player p = arrayOfPlayer1[k];
                if (p != GetPlayer()) {
                    Location loc = p.getLocation();
                    if (l2.getWorld().getBlockAt(l2).getLocation().distance(loc) <= 3.0D)
                        p.damage((int) 7.0D, GetPlayer());
                }
            }
        }
    }

    class ExplosionTimer extends TimerTask {
        World world;
        Location location;
        Location location2;

        ExplosionTimer(int blockid, Block block) {
            this.world = block.getWorld();
            this.location = block.getLocation();
            this.location2 = block.getLocation();
        }

        public void run() {
            this.world.getBlockAt(this.location).breakNaturally();
        }
    }

    class ExplosionTimer2 extends TimerTask {
        World world;
        Location location;
        Location location2;
        private Material blockd;

        ExplosionTimer2(Material blockid, Block block) {
            this.world = block.getWorld();
            this.location = block.getLocation();
            this.location2 = block.getLocation();
            this.blockd = blockid;
        }

        public void run() {
            this.world.getBlockAt(this.location).setType(this.blockd);
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Aokizi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */