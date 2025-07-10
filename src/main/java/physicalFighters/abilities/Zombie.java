package physicalFighters.abilities;

import physicalFighters.core.EventManager;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import physicalFighters.core.Ability;
import physicalFighters.utils.EventData;

public class Zombie extends Ability {
    public Zombie() {
        InitAbility("좀비", Type.Passive_AutoMatic,
                Rank.B, new String[]{"모든 데미지의 반을 흡수합니다.",
                        "불공격의 데미지를 8배로 받습니다."});
        InitAbility(0, 0, true);
        EventManager.onEntityDamage.add(new EventData(this));
        EventManager.onEntityDamageByEntity.add(new EventData(this, 1));
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageEvent Event = (EntityDamageEvent) event;
            if (isOwner(Event.getEntity())) {
                if ((Event.getCause() == DamageCause.LAVA) ||
                        (Event.getCause() == DamageCause.FIRE) ||
                        (Event.getCause() == DamageCause.FIRE_TICK))
                    return 0;
                if ((Event.getCause() == DamageCause.BLOCK_EXPLOSION) ||
                        (Event.getCause() == DamageCause.ENTITY_EXPLOSION))
                    return 1;
                if ((Event.getCause() == DamageCause.FALL) ||
                        (Event.getCause() == DamageCause.POISON) ||
                        (Event.getCause() == DamageCause.PROJECTILE))
                    return 2;
            }
        } else if (CustomData == 1) {
            EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
            if ((isOwner(Event1.getEntity())) &&
                    (((HumanEntity) Event1.getDamager()).getInventory().getItemInMainHand()
                            .getType() != Ability.DefaultItem)) {
                if (((HumanEntity) Event1.getDamager()).getInventory().getItemInMainHand()
                        .getType().getId() != Material.GOLD_INGOT.getId()) {
                    return 2;
                }
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageEvent Event = (EntityDamageEvent) event;
                Event.setDamage((int) Event.getDamage() * 8);
                break;
            case 1:
                EntityDamageEvent Event2 = (EntityDamageEvent) event;
                Event2.setDamage((int) Event2.getDamage() * 4);
                break;
            case 2:
                EntityDamageByEntityEvent Event3 = (EntityDamageByEntityEvent) event;
                Event3.setDamage((int) Event3.getDamage() / 2);
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Zombie.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */