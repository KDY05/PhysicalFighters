package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.AUC;
import io.github.kdy05.physicalFighters.utils.EventData;

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

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageEvent event0 = (EntityDamageEvent) event;
        if (!PhysicalFighters.DamageGuard && isOwner(event0.getEntity())) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageEvent event0 = (EntityDamageEvent) event;
        Player player = (Player) event0.getEntity();
        if (Math.random() <= 0.05D)
            AUC.healEntity(player, 2);
        if (Math.random() <= 0.05D)
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 0));
        if (Math.random() <= 0.05D)
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
        if (Math.random() <= 0.1D)
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 200, 0));
        if (Math.random() <= 0.1D)
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 200, 0));
        if (Math.random() <= 0.1D)
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
    }
}
