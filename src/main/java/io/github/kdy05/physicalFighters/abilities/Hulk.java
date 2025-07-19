package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Hulk extends Ability {

    public Hulk() {
        InitAbility("헐크", Type.Active_Continue, Rank.SSS,
                Usage.IronRight + "30초간 각종 버프를 받으며 주는 데미지가 1.5배, 받는 데미지가 절반이 됩니다.");
        InitAbility(180, 30, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerRightClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 1) {
            PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
            if (isOwner(Event2.getPlayer()) &&
                    isValidItem(Ability.DefaultItem) && !PhysicalFighters.DamageGuard) {
                return 1;
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData != 0) return;
        EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
        // 공격력 1.5배
        if (isOwner(Event1.getDamager())) {
            Event1.setDamage(Event1.getDamage() * 1.5);
        }
        // 대미지 반감
        if (isOwner(Event1.getEntity())) {
            Event1.setDamage(Event1.getDamage() / 2);
        }
    }

    @Override
    public void A_DurationStart() {
        Player caster = getPlayer();
        if (caster == null) return;
        caster.setHealth(20);
        caster.getWorld().createExplosion(caster.getLocation(), 0.0F);
        caster.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 0));
        caster.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 0));
        caster.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 0));
        caster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0));
        caster.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 600, 0));
        caster.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 600, 0));
    }

    @Override
    public void A_FinalDurationEnd() {
        Player caster = getPlayer();
        if (caster == null) return;
        caster.setHealth(20);
    }

}
