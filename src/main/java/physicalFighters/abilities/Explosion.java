package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Explosion extends Ability {
    public Explosion() {
        if (!PhysicalFighters.Toner) {
            InitAbility("익스플로젼", Type.Passive_Manual, Rank.B, new String[]{
                    "사망시에 엄청난 연쇄폭발을 일으켜 주변의 유저들을 죽입니다."});
            InitAbility(0, 0, true);
            EventManager.onEntityDeath.add(new EventData(this, 0));
        }
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDeathEvent Event0 = (EntityDeathEvent) event;
                if (isOwner(Event0.getEntity()))
                    return 0;
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerDeathEvent Event0 = (PlayerDeathEvent) event;
                Player killed = Event0.getEntity();
                killed.getWorld().createExplosion(killed.getLocation(), 8.0F,
                        false);
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Explosion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */