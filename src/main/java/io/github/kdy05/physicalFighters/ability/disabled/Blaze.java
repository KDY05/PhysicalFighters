package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Blaze extends Ability {
    public Blaze() {
        InitAbility("블레이즈", Type.Passive_AutoMatic, Rank.C,
                "용암과 불 데미지를 입지 않습니다.", "능력에서 파생되는 화염 데미지도 막습니다.",
                "모든 종류의 폭발 데미지를 50%로 줄여 받습니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamage.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageEvent Event = (EntityDamageEvent) event;
        if (isOwner(Event.getEntity())) {
            if ((Event.getCause() == DamageCause.LAVA) ||
                    (Event.getCause() == DamageCause.FIRE) ||
                    (Event.getCause() == DamageCause.FIRE_TICK))
                return 0;
            if ((Event.getCause() == DamageCause.BLOCK_EXPLOSION) ||
                    (Event.getCause() == DamageCause.ENTITY_EXPLOSION)) {
                return 1;
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageEvent Event = (EntityDamageEvent) event;
        switch (CustomData) {
            case 0:
                Event.setCancelled(true);
                Event.getEntity().setFireTicks(0);
                break;
            case 1:
                Event.setDamage((int) (Event.getDamage() / 2.0D));
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Blaze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */