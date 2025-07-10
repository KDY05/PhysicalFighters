package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Nonuck extends Ability {
    public Nonuck() {
        if (PhysicalFighters.SRankUsed) {
            InitAbility("무통증", Type.Passive_AutoMatic, Rank.S, new String[]{
                    "플레이어에게 타격당할시에 80%확률로 넉백을 무시합니다."});
            InitAbility(0, 0, true);
            EventManager.onEntityDamageByEntity.add(new EventData(this));
        }
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if ((isOwner(Event.getEntity())) &&
                ((Event.getCause() == DamageCause.ENTITY_ATTACK) || (Event.getCause() == DamageCause.PROJECTILE)) &&
                (Math.random() <= 0.8D) &&
                (!EventManager.DamageGuard)) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Player p = (Player) Event.getEntity();
        int damage = (int) Event.getDamage();
        p.damage(damage);
        Event.setCancelled(true);
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Nonuck.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */