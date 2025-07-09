package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.utils.ACC;
import physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Sasuke extends AbilityBase {
    public Sasuke() {
        InitAbility("사스케", Type.Active_Immediately, Rank.S, new String[]{
                "철괴로 상대를 타격시에 치도리를 사용해 상대를 엄청난 데미지로 감전시킵니다."});
        InitAbility(30, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if ((isOwner(Event.getDamager())) && (isValidItem(ACC.DefaultItem)) &&
                (!EventManager.DamageGuard)) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Player p = (Player) Event.getEntity();
        p.damage(15);
        Event.getEntity().getWorld()
                .strikeLightning(Event.getEntity().getLocation());
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Sasuke.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */