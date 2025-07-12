package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Berserker extends Ability {
    public Berserker() {
        InitAbility("광전사", Type.Passive_Manual, Rank.A,
                "체력이 낮아질수록 데미지가 증폭됩니다.",
                "6칸 ↓ - 1.5배, 4칸 ↓ - 2배, 2칸 ↓ - 3배, 반 칸 ↓ - 5배");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (!isOwner(Event.getDamager())) return -1;
        Player p = (Player) Event.getDamager();
        if (p.getHealth() <= 1.0D) return 0;
        else if (p.getHealth() <= 4.0D) return 1;
        else if (p.getHealth() <= 8.0D) return 2;
        else if (p.getHealth() <= 12.0D) return 3;
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        switch (CustomData) {
            case 0:
                Event.setDamage(Event.getDamage() * 5.0);
                break;
            case 1:
                Event.setDamage(Event.getDamage() * 3.0);
                break;
            case 2:
                Event.setDamage(Event.getDamage() * 2);
                break;
            case 3:
                Event.setDamage(Event.getDamage() * 1.5);
        }
    }
}
