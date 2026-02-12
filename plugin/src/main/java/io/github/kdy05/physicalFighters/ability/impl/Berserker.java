package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.UUID;

public class Berserker extends Ability {
    public Berserker(UUID playerUuid) {
        super(AbilitySpec.builder("광전사", Type.Passive_Manual, Rank.A)
                .guide("체력이 낮아질수록 대미지가 증폭됩니다.",
                        "6칸 ↓ - 1.5배, 4칸 ↓ - 2배, 2칸 ↓ - 3배, 반 칸 ↓ - 4배")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamageByEntity(new EventData(this, 0));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (!isOwner(event0.getDamager())) return -1;
        Player p = (Player) event0.getDamager();
        if (p.getHealth() <= 1.0D) return 0;
        else if (p.getHealth() <= 4.0D) return 1;
        else if (p.getHealth() <= 8.0D) return 2;
        else if (p.getHealth() <= 12.0D) return 3;
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (CustomData == 0) {
            event0.setDamage(event0.getDamage() * 4.0);
        } else if (CustomData == 1) {
            event0.setDamage(event0.getDamage() * 3.0);
        } else if (CustomData == 2) {
            event0.setDamage(event0.getDamage() * 2);
        } else if (CustomData == 3) {
            event0.setDamage(event0.getDamage() * 1.5);
        }
    }
}
