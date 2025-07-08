package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.EventManager;

import java.util.ArrayList;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class Shadow extends Physical.Fighters.MainModule.AbilityBase {
    public Shadow() {
        InitAbility("그림자", Type.Passive_AutoMatic, Rank.C, new String[]{
                "몬스터가 절대로 공격을 하지 않습니다. 생명체로부터", "공격받을시 5% 확률로 데미지를 받지않고",
                "체력을 3 회복합니다."});
        InitAbility(0, 0, true);
        EventManager.onEntityTarget.add(new Physical.Fighters.MinerModule.EventData(this, 0));
        EventManager.onEntityDamage.add(new Physical.Fighters.MinerModule.EventData(this, 1));
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityTargetEvent Event0 = (EntityTargetEvent) event;
                if (PlayerCheck(Event0.getTarget())) {
                    return 0;
                }
                break;
            case 1:
                EntityDamageEvent Event1 = (EntityDamageEvent) event;
                if ((PlayerCheck(Event1.getEntity())) &&
                        (Event1.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) &&
                        (Math.random() <= 0.05D)) {
                    return 1;
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityTargetEvent Event0 = (EntityTargetEvent) event;
                Event0.setTarget(null);
                Event0.setCancelled(true);
                break;
            case 1:
                EntityDamageEvent Event1 = (EntityDamageEvent) event;
                Event1.setDamage(0);
                if (((Damageable) GetPlayer()).getHealth() <= 18.0D) {
                    GetPlayer().setHealth(((Damageable) GetPlayer()).getHealth() + 3);
                }
                break;
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Shadow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */