package physicalFighters.abilities;

import physicalFighters.PhysicalFighters;
import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

public class CP9 extends Ability {
    public CP9() {
        if (PhysicalFighters.SRankUsed) {
            InitAbility(
                    "CP9",
                    Type.Active_Immediately,
                    Rank.S,
                    new String[]{
                            "철괴로 상대 타격시에 강한데미지를 줍니다.",
                            "철괴 오른클릭시 폭발과함께 바라보는 방향으로 빠르게 전진합니다. [점프와 사용하면 효율적]",
                            "*낙하데미지를 받지않습니다."});
            InitAbility(15, 0, true);
            EventManager.onEntityDamageByEntity.add(new EventData(this));
            registerRightClickEvent();
            EventManager.onEntityDamage.add(new EventData(this, 3));
        }
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                if (((Event1.getEntity() instanceof Player)) &&
                        (isOwner(Event1.getDamager())) &&
                        (isValidItem(Ability.DefaultItem)) &&
                        (!EventManager.DamageGuard)) {
                    return 1;
                }
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                if ((isOwner(Event2.getPlayer())) &&
                        (isValidItem(Ability.DefaultItem))) {
                    return 2;
                }
                break;
        }
        if (CustomData == 3) {
            EntityDamageEvent Event2 = (EntityDamageEvent) event;
            if ((isOwner(Event2.getEntity())) &&
                    (Event2.getCause() == DamageCause.FALL)) {
                Event2.setCancelled(true);
                getPlayer().sendMessage(
                        ChatColor.GREEN + "사뿐하게 떨어져 데미지를 받지 않았습니다.");
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 1:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                Player p1 = (Player) Event1.getEntity();
                p1.setHealth(((Damageable) p1).getHealth() - 6);
                getPlayer().sendMessage(
                        String.format(ChatColor.RED + "%s님에게 지건을 사용했습니다.",
                                new Object[]{p1.getName()}));
                break;
            case 2:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                Location loca = Event.getPlayer().getLocation();
                Location loca2 = Event.getPlayer().getLocation();
                Player p = Event.getPlayer();
                double degrees = Math.toRadians(-(Event.getPlayer().getLocation()
                        .getYaw() % 360.0F));
                double ydeg = Math.toRadians(-(Event.getPlayer().getLocation()
                        .getPitch() % 360.0F));
                loca.setX(Event.getPlayer().getLocation().getX() + -1.5D * (
                        Math.sin(degrees) * Math.cos(ydeg)));
                loca.setY(Event.getPlayer().getLocation().getY() + -1.5D *
                        Math.sin(ydeg));
                loca.setZ(Event.getPlayer().getLocation().getZ() + -1.5D * (
                        Math.cos(degrees) * Math.cos(ydeg)));
                Event.getPlayer().getWorld().createExplosion(loca, 0.0F);
                loca2.setX(Event.getPlayer().getLocation().getX() + 5.0D * (
                        Math.sin(degrees) * Math.cos(ydeg)));
                loca2.setY(Event.getPlayer().getLocation().getY() + 5.0D *
                        Math.sin(ydeg));
                loca2.setZ(Event.getPlayer().getLocation().getZ() + 5.0D * (
                        Math.cos(degrees) * Math.cos(ydeg)));
                p.setVelocity(((Damageable) p).getVelocity().add(
                        loca2.toVector()
                                .subtract(
                                        Event.getPlayer().getLocation().toVector())
                                .normalize().multiply(5)));
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\CP9.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */