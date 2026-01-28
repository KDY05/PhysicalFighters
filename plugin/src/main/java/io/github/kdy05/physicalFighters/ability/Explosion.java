package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.AbilitySpec;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Explosion extends Ability {
    public Explosion() {
        super(AbilitySpec.builder("익스플로젼", Type.Passive_Manual, Rank.B)
                .guide("사망 시 엄청난 연쇄 폭발을 일으킵니다.")
                .build());
        EventManager.onEntityDeath.add(new EventData(this, 0));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDeathEvent event0 = (EntityDeathEvent) event;
            if (isOwner(event0.getEntity())) return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerDeathEvent event0 = (PlayerDeathEvent) event;
            Player killed = event0.getEntity();
            killed.getWorld().createExplosion(killed.getLocation(), 8.0F, false);
        }
    }
}
