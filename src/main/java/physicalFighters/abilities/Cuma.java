package physicalFighters.abilities;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Cuma extends Ability {
    public Cuma() {
        InitAbility("바솔로뮤 쿠마", Type.Passive_AutoMatic, Rank.S,
                "피격 시 상대를 넉백시키며, 일정 확률로 받은 공격을 상대에게 되돌려줍니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (isOwner(Event.getEntity()) && !EventManager.DamageGuard
                && Event.getEntity() instanceof LivingEntity) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Player caster = (Player) Event.getEntity();
        LivingEntity target = (LivingEntity) Event.getDamager();
        if (Math.random() <= 0.20) {
            target.damage(Event.getDamage());
            Event.setCancelled(true);
        }
        target.getWorld().createExplosion(target.getLocation(), 0.0F);
        Vector knockback = target.getLocation().toVector().subtract(caster.getLocation().toVector()).normalize();
        target.setVelocity(target.getVelocity().add(knockback));
    }
}
