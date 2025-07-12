package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Demigod extends Ability {
    public Demigod() {
        InitAbility("데미갓", Type.Passive_AutoMatic, Rank.S,
                "반은 인간, 반은 신인 능력자입니다.",
                "데미지를 받으면 일정 확률로 10초간 랜덤 버프가 발동됩니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamage.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageEvent Event = (EntityDamageEvent) event;
        if (!EventManager.DamageGuard && isOwner(Event.getEntity())) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageEvent Event = (EntityDamageEvent) event;
        Player p1 = (Player) Event.getEntity();
        if (Math.random() <= 0.05D)
            p1.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 0));
        if (Math.random() <= 0.05D)
            p1.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
        if (Math.random() <= 0.05D)
            p1.setHealth(Math.min(20, p1.getHealth() + 1));
        if (Math.random() <= 0.1D)
            p1.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 200, 0));
        if (Math.random() <= 0.1D)
            p1.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 200, 0));
        if (Math.random() <= 0.1D)
            p1.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
    }
}
