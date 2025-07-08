package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.AbilityBase.Rank;
import Physical.Fighters.MainModule.AbilityBase.Type;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.EventData;
import Physical.Fighters.PhysicalFighters;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class Anorexia extends AbilityBase {
    public Anorexia() {
        if (!PhysicalFighters.Specialability) {
            InitAbility("거식증", Type.Passive_AutoMatic, Rank.A, new String[]{
                    "캐릭터의 배고픔이 꽉 찬 상태로 고정됩니다.", "모든 종류의 체력 회복량을 3배로 받습니다."});
            InitAbility(0, 0, true);
            EventManager.onFoodLevelChange.add(new EventData(this, 0));
            EventManager.onEntityRegainHealth.add(new EventData(this, 1));
        }
    }

    public int A_Condition(Event event, int CustomData) {
        Player p = null;
        switch (CustomData) {
            case 0:
                FoodLevelChangeEvent Event0 = (FoodLevelChangeEvent) event;
                p = (Player) Event0.getEntity();
                if (PlayerCheck(p)) {
                    return 0;
                }
                break;
            case 1:
                EntityRegainHealthEvent Event1 = (EntityRegainHealthEvent) event;
                if (PlayerCheck(Event1.getEntity()))
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
                if (PhysicalFighters.NoFoodMode) {
                    Event1.setAmount((int) (Event1.getAmount() * 3.0D));
                } else {
                    Event1.setAmount((int) (Event1.getAmount() * 3.0D));
                }
                break;
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Anorexia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */