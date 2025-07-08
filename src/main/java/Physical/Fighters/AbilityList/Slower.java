package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.AbilityBase.Rank;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.EventData;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class Slower extends AbilityBase {
    public Slower() {
        InitAbility("슬로워", Type.Passive_AutoMatic, Rank.C, new String[]{
                "자신에게 공격받은 사람은 그 1.5초간 느려집니다,"});
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if ((PlayerCheck(Event.getDamager())) && !EventManager.DamageGuard &&
                ((Event.getEntity() instanceof Player))) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Player p = (Player) Event.getEntity();
        p.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SLOWNESS, 30, 0), true);
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Slower.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */