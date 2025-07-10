package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Uppercut extends Ability {
    public Uppercut() {
        InitAbility("어퍼컷", Type.Active_Immediately, Rank.A, new String[]{
                "플레이어를 공격하면 피격플레이어는 공중으로 아주 높이 뜨게됩니다."});
        InitAbility(10, 0, true, ShowText.No_CoolDownText);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (isOwner(Event.getDamager()) && !EventManager.DamageGuard) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Entity e = Event.getEntity();
        Location lt = e.getLocation();
        lt.setY(lt.getY() + 5.0D);
        e.setVelocity(e.getVelocity().add(
                e.getLocation().toVector().subtract(lt.toVector()).normalize()
                        .multiply(-2)));
        Event.setCancelled(true);
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Uppercut.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */