package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import physicalFighters.utils.ACC;

public class SuperFan extends AbilityBase {
    public SuperFan() {
        InitAbility("선풍기", Type.Active_Immediately, Rank.B, new String[]{
                "철괴를 들고 왼쪽클릭하면 바라보는 방향의 플레이어들을  날려버립니다.",
                "이때 날라간 플레이어들은 무더위에 시원함을 느껴 체력이 회복됩니다.",
                "하지만 강한 바람에 의해 눈을 뜨기가 힘들고, 허약해집니다."});
        InitAbility(20, 0, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((isOwner(Event.getPlayer())) && (isValidItem(ACC.DefaultItem)) && !EventManager.DamageGuard) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Location l = Event.getPlayer().getLocation();
        Location l2 = Event.getPlayer().getLocation();
        Location l3 = Event.getPlayer().getLocation();
        l3.setY(l.getY() - 1.0D);
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));
        for (int i = 1; i < 10; i++) {
            l2.setX(l.getX() + 1 * i + 2.0D * (Math.sin(degrees) * Math.cos(ydeg)));
            l2.setY(l.getY() + 1 * i + 2.0D * Math.sin(ydeg));
            l2.setZ(l.getZ() + 1 * i + 2.0D * (Math.cos(degrees) * Math.cos(ydeg)));
            Player[] pp = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            for (int ii = 0; ii < (Bukkit.getOnlinePlayers().toArray(new Player[0])).length; ii++) {
                if (pp[ii] != getPlayer()) {
                    Location loc = pp[ii].getLocation();
                    if (l2.distance(loc) <= 3.0D) {
                        if (!EventManager.DamageGuard) {
                            pp[ii].setVelocity(pp[ii].getVelocity().add(
                                    l3.toVector()
                                            .subtract(pp[ii].getLocation().toVector()).normalize()
                                            .multiply(-2.2D)));
                            pp[ii].addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0), true);
                            pp[ii].addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2), true);
                            pp[ii].addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 2), false);
                            pp[ii].addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 2), false);
                            pp[ii].sendMessage(org.bukkit.ChatColor.LIGHT_PURPLE + "선풍기의 강력한 바람에 힘을 잃었습니다!");
                        }
                    }
                }
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\SuperFan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */