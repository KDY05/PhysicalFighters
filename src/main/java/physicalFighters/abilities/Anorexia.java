package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class Anorexia extends Ability {
    public Anorexia() {
        if (PhysicalFighters.Specialability) return;
        InitAbility("거식증", Type.Passive_AutoMatic, Rank.A,
                "캐릭터의 배고픔이 꽉 찬 상태로 고정됩니다.", "모든 종류의 체력 회복량을 3배로 받습니다.");
        InitAbility(0, 0, true);
        EventManager.onFoodLevelChange.add(new EventData(this, 0));
        EventManager.onEntityRegainHealth.add(new EventData(this, 1));
    }

    public int A_Condition(Event event, int CustomData) {
        Player p;
        switch (CustomData) {
            case 0:
                FoodLevelChangeEvent Event0 = (FoodLevelChangeEvent) event;
                p = (Player) Event0.getEntity();
                if (isOwner(p)) {
                    return 0;
                }
                break;
            case 1:
                EntityRegainHealthEvent Event1 = (EntityRegainHealthEvent) event;
                if (isOwner(Event1.getEntity()))
                    return 1;
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                FoodLevelChangeEvent Event0 = (FoodLevelChangeEvent) event;
                Player p = (Player) Event0.getEntity();
                p.setFoodLevel(20);
                p.setSaturation(0.0F);
                Event0.setCancelled(true);
                break;
            case 1:
                EntityRegainHealthEvent Event1 = (EntityRegainHealthEvent) event;
                Event1.setAmount((int) (Event1.getAmount() * 3.0D));
                break;
        }
    }
}
