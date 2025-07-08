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
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Luffy extends AbilityBase {
    private Material item;

    public Luffy() {
        InitAbility("루피", Type.Active_Immediately, Rank.S, new String[]{
                "철괴를 들고 왼쪽클릭을 하면 주먹질을 합니다 [쿨타임 없음]",
                "금괴를 들고 왼쪽클릭을 하면 30초간 속도,점프력,공격력,방어력이 높아집니다. [체력 5 소모, 쿨타임없음]",
                "버프스킬을 사용시에  부작용이 있습니다.",
                "*주의* 금괴를 들고 왼쪽클릭을 난타하시다가 사망하실 수 있습니다."});
        InitAbility(0, 0, true, ShowText.Custom_Text);
        RegisterLeftClickEvent();
        this.item = Material.IRON_INGOT;
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((PlayerCheck(Event.getPlayer())) && (ItemCheck(this.item)) && !EventManager.DamageGuard) {
            return 1;
        }
        if ((PlayerCheck(Event.getPlayer())) && (((Damageable) Event.getPlayer()).getHealth() >= 6.0D) && (ItemCheck(Material.GOLD_INGOT))) {
            return 2;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Location l = Event.getPlayer().getLocation();
        Location l2 = Event.getPlayer().getLocation();
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));
        switch (CustomData) {
            case 1:
                Timer timer = new Timer();
                for (int i = 1; i < 5; i++) {
                    l2.setX(l.getX() + (1 * i + 1) * (
                            Math.sin(degrees) * Math.cos(ydeg)));
                    l2.setY(l.getY() + (1 * i + 1) * Math.sin(ydeg));
                    l2.setZ(l.getZ() + (1 * i + 1) * (
                            Math.cos(degrees) * Math.cos(ydeg)));
                    if (l2.getWorld().getBlockAt(l2).getType() != Material.SANDSTONE)
                        timer.schedule(new ExplosionTimer2(l2.getWorld().getBlockAt(l2).getType(), l2.getWorld().getBlockAt(l2)), 70L);
                    l2.getWorld().getBlockAt(l2).setType(Material.SANDSTONE);
                    Player[] List = Bukkit.getOnlinePlayers().toArray(new Player[0]);
                    Player[] arrayOfPlayer1;
                    int j = (arrayOfPlayer1 = List).length;
                    for (int k = 0; k < j; k++) {
                        Player pp = arrayOfPlayer1[k];
                        if (pp != GetPlayer()) {
                            Location loc = pp.getLocation();
                            if (l2.getWorld().getBlockAt(l2).getLocation().distance(loc) <= 3.0D) {
                                pp.damage(1, p);
                            }
                        }
                    }
                }
                break;
            case 2:
                p.setHealth(((Damageable) p).getHealth() - 5);
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 600, 0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 0), true);
                p.sendMessage(org.bukkit.ChatColor.GREEN + "기어세컨드를 사용하였습니다.");
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
        private final Material blockd;

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


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Luffy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */