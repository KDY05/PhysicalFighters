package io.github.kdy05.physicalFighters.ability.impl;

import org.bukkit.entity.Player;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.util.EventData;
import java.util.UUID;

public class Zombie extends Ability {
    public Zombie(UUID playerUuid) {
        super(AbilitySpec.builder("좀비", Type.PassiveAutoMatic, Rank.B)
                .guide("모든 대미지의 반을 흡수합니다. 단, 화염 대미지를 8배로 받습니다.")
                .build(), playerUuid);
        // onEntityDamage는 EntityDamageByEntityEvent도 수신하므로 단일 등록으로 통합
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamage(new EventData(this));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        EntityDamageEvent event0 = (EntityDamageEvent) event;
        if (!isOwner(event0.getEntity())) return -1;

        // 화염 대미지 → 8배
        if (event0.getCause() == DamageCause.LAVA || event0.getCause() == DamageCause.FIRE
                || event0.getCause() == DamageCause.FIRE_TICK)
            return 0;

        // 능력 아이템(철괴/금괴)에 의한 공격은 감소 없음
        if (event0 instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event0;
            if (entityEvent.getDamager() instanceof Player) {
                Player attacker = (Player) entityEvent.getDamager();
                Material handItem = attacker.getInventory().getItemInMainHand().getType();
                if (handItem == Material.GOLD_INGOT || handItem == DefaultItem)
                    return -1;
            }
        }

        // 그 외 모든 대미지 → 0.5배
        return 1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        EntityDamageEvent event0 = (EntityDamageEvent) event;
        if (CustomData == 0) {
            event0.setDamage(event0.getDamage() * 8);
        } else {
            event0.setDamage(event0.getDamage() * 0.5);
        }
    }
}
