package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.AbilityBase.Rank;
import Physical.Fighters.MainModule.AbilityBase.Type;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.EventData;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Ckyomi extends AbilityBase {
    public Ckyomi() {
        InitAbility("츠쿠요미", Type.Passive_AutoMatic, Rank.A, new String[]{
                "상대를 공격하면 상대에게 5초간 혼란효과와 디버프를 줍니다."});
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if ((!EventManager.DamageGuard) &&
                (PlayerCheck(Event.getDamager())) &&
                ((Event.getEntity() instanceof Player))) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Player p = (Player) Event.getEntity();
        p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100,
                0), true);
        p.addPotionEffect(
                new PotionEffect(PotionEffectType.WEAKNESS, 100, 0), true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100,
                0), true);
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Ckyomi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */