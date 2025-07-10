package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Cuma extends Ability {
    public Cuma() {
        if (PhysicalFighters.SRankUsed) {
            InitAbility("바솔로뮤 쿠마", Type.Passive_AutoMatic, Rank.S, new String[]{
                    "일정 확률르 받은 공격을 상대에게 되돌려주며, 공격받을시 상대를 뒤로 넉백시킵니다."});
            InitAbility(0, 0, true);
            EventManager.onEntityDamageByEntity.add(new EventData(this));
        }
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (isOwner(Event.getEntity()) && !EventManager.DamageGuard) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Player p = (Player) Event.getEntity();
        Player pn = (Player) Event.getDamager();
        if (Math.random() <= 0.15D) {
            pn.damage(Event.getDamage());
            Event.setCancelled(true);
        }
        pn.getWorld().createExplosion(pn.getLocation(), 0.0F);
        pn.setVelocity(pn.getVelocity().add(
                p.getLocation().toVector()
                        .subtract(pn.getLocation().toVector()).normalize()
                        .multiply(-1)));
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Cuma.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */