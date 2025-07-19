package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Blind extends Ability {
    public Blind() {
        InitAbility("블라인드", Type.Passive_AutoMatic, Rank.C,
                "자신에게 공격받은 사람은 3초간 시야를 잃습니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if ((!PhysicalFighters.DamageGuard) &&
                (isOwner(Event.getDamager())) &&
                ((Event.getEntity() instanceof Player))) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Player p = (Player) Event.getEntity();
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
    }
}
