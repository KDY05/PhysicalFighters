package physicalFighters.abilities;

import physicalFighters.PhysicalFighters;
import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Poksi extends Ability {
    public Poksi() {
        InitAbility("이슈타르의 링", Type.Active_Immediately, Rank.S, new String[]{
                "철괴로 능력을 사용합니다.",
                "철괴를 들고 우클릭시 바라보는 방향으로 화살 두발을 발사합니다. (꾹 느르고 있으면 연사됩니다.)"});
        InitAbility(0, 0, true, ShowText.Custom_Text);
        registerRightClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 3));
        EventManager.onProjectileHitEvent.add(new EventData(this, 5));
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 1:
                PlayerInteractEvent Event0 = (PlayerInteractEvent) event;
                if ((!EventManager.DamageGuard) &&
                        (isOwner(Event0.getPlayer())) && (isValidItem(Ability.DefaultItem))) {
                    return 10;
                }
                break;
            case 3:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                if ((Event1.getDamager() instanceof Arrow)) {
                    Arrow a = (Arrow) Event1.getDamager();
                    if (isOwner((Entity) a.getShooter())) {
                        if (((Event1.getEntity() instanceof Player)) &&
                                ((Player) a.getShooter() ==
                                        (Player) Event1
                                                .getEntity()))
                            return -1;
                        return 3;
                    }
                }
                break;
            case 5:
                ProjectileHitEvent Event2 = (ProjectileHitEvent) event;
                if ((Event2.getEntity() instanceof Arrow)) {
                    Arrow a = (Arrow) Event2.getEntity();
                    if (((a.getShooter() instanceof Player)) &&
                            (isOwner((Player) a.getShooter()))) {
                        a.remove();
                        return -2;
                    }
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 3:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                Event0.setDamage(4);
                break;
            case 10:
                PlayerInteractEvent Event1 = (PlayerInteractEvent) event;
                Arrow a = (Arrow) Event1.getPlayer().launchProjectile(Arrow.class);
                a.setVelocity(a.getVelocity().multiply(3));
                Arrow a2 = (Arrow) Event1.getPlayer().launchProjectile(Arrow.class);
                a2.setVelocity(a2.getVelocity().multiply(2));
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\poksi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */