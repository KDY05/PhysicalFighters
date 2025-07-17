package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Berserker extends Ability {
    public Berserker() {
        InitAbility("광전사", Type.Passive_Manual, Rank.A,
                Usage.Passive + "체력이 낮아질수록 데미지가 증폭됩니다.",
                "6칸 ↓ - 1.5배, 4칸 ↓ - 2배, 2칸 ↓ - 3배, 한 칸 ↓ - 4배");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (!isOwner(event0.getDamager())) return -1;
        Player p = (Player) event0.getDamager();
        if (p.getHealth() <= 2.0D) return 0;
        else if (p.getHealth() <= 4.0D) return 1;
        else if (p.getHealth() <= 8.0D) return 2;
        else if (p.getHealth() <= 12.0D) return 3;
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        switch (CustomData) {
            case 0 -> event0.setDamage(event0.getDamage() * 4.0);
            case 1 -> event0.setDamage(event0.getDamage() * 3.0);
            case 2 -> event0.setDamage(event0.getDamage() * 2);
            case 3 -> event0.setDamage(event0.getDamage() * 1.5);
        }
    }
}
