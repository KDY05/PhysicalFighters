package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Feather extends Ability {
    public Feather() {
        if (!PhysicalFighters.Specialability) {
            InitAbility("깃털", Type.Passive_AutoMatic, Rank.C, new String[]{
                    "낙하 데미지와 물속에서의 질식 데미지를 받지 않습니다.",
                    "90% 확률로 폭발,번개 데미지를 1로 줄여받으며 미러링 능력을",
                    "회피할 수 있습니다, 낙하시 1분간 속도, 점프력이 빨라지는 버프를 받습니다."});
            InitAbility(0, 0, true);
            EventManager.onEntityDamage.add(new EventData(this));
        }
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageEvent Event = (EntityDamageEvent) event;
        if (isOwner(Event.getEntity())) {
            if ((Event.getCause() == DamageCause.FALL) ||
                    (Event.getCause() == DamageCause.DROWNING))
                return 0;
            if ((Math.random() <= 0.9D) && (
                    (Event.getCause() == DamageCause.LIGHTNING) ||
                            (Event
                                    .getCause() == DamageCause.ENTITY_EXPLOSION))) {
                return 1;
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageEvent Event = (EntityDamageEvent) event;
        ((Player) Event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 1200, 1),
                true);
        ((Player) Event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1),
                true);
        Event.setCancelled(true);
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Feather.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */