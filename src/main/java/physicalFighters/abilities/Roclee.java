package physicalFighters.abilities;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Roclee extends Ability {
    public Roclee() {
        InitAbility("록리", Type.Active_Immediately, Rank.S,
                "철괴로 상대 타격 시 피해를 입히며 공중으로 끌어올립니다.",
                "시전자는 낙하 데미지를 받지 않습니다.");
        InitAbility(20, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (isOwner(Event.getDamager()) && isValidItem(Ability.DefaultItem) && !EventManager.DamageGuard) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        LivingEntity target = (LivingEntity) e.getEntity();
        Player attacker = getPlayer();

        target.getWorld().createExplosion(target.getLocation(), 0.0F);
        Location targetNewLoc = target.getLocation().add(0, 8, 0);
        Location attackerNewLoc = attacker.getLocation().add(0, 8, 0);
        target.teleport(targetNewLoc);
        attacker.teleport(attackerNewLoc);

        AttributeInstance safeFall = attacker.getAttribute(Attribute.SAFE_FALL_DISTANCE);
        if (safeFall != null) {
            safeFall.setBaseValue(20.0);
            Bukkit.getScheduler().runTaskLater(plugin, ()
                    -> safeFall.setBaseValue(3.0), 80);
        }
        target.getWorld().createExplosion(targetNewLoc, 1.0F);
        target.damage(8);
    }
}
