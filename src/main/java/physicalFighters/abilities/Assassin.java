package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Assassin
        extends AbilityBase {
    public Assassin() {
        InitAbility("어쌔신", Type.Passive_AutoMatic, Rank.C,
                "뒤에서 공격할시에 데미지를 두배로 입히고 눈을 가립니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if ((isOwner(Event.getDamager())) &&
                ((Event.getEntity() instanceof Player p))) {
            Player p1 = (Player) Event.getDamager();
            if (getDirection(p) == getDirection(p1)) {
                return 0;
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Player p = (Player) Event.getEntity();
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
        Event.setDamage((int) (Event.getDamage() * 2.0D));
        Event.getDamager().sendMessage(ChatColor.GREEN + "백스텝 성공!");
        p.sendMessage(ChatColor.RED + "백스텝을 당하셨습니다.");
    }

    public static int getDirection(Player p) {
        Location loc = p.getLocation();
        Location loc2 = p.getTargetBlock(null, 0).getLocation();
        int x = (int) Math.abs(Math.abs(loc.getX()) - Math.abs(loc2.getX()));
        int z = (int) Math.abs(Math.abs(loc.getZ()) - Math.abs(loc2.getZ()));
        if (loc == loc2) {
            return 10;
        }
        if (x > z) {
            if (loc.getX() > loc2.getX()) {
                return 1;
            }
            return 3;
        }
        if (loc.getZ() > loc2.getZ()) {
            return 2;
        }
        return 4;
    }
}
