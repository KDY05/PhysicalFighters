package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;

public class Hulk extends Ability {

    private double originalHealth = 20;

    public Hulk() {
        InitAbility("헐크", Type.Active_Continue, Rank.SSS,
                Usage.IronRight + "30초간 각종 버프를 받으며 주는 대미지가 1.5배, 받는 대미지가 절반이 됩니다.");
        InitAbility(180, 30, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerRightClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            if (isOwner(event1.getPlayer()) &&
                    isValidItem(Ability.DefaultItem) && !ConfigManager.DamageGuard) {
                originalHealth = event1.getPlayer().getHealth();
                return 1;
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData != 0) return;
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        // 공격력 1.5배
        if (isOwner(event0.getDamager())) {
            event0.setDamage(event0.getDamage() * 1.5);
        }
        // 대미지 반감
        if (isOwner(event0.getEntity())) {
            event0.setDamage(event0.getDamage() / 2);
        }
    }

    @Override
    public void A_DurationStart() {
        Player caster = getPlayer();
        if (caster == null) return;
        caster.setHealth(20);
        caster.getWorld().createExplosion(caster.getLocation(), 0.0F);
        caster.addPotionEffect(PotionEffectFactory.createResistance(600, 0));
        caster.addPotionEffect(PotionEffectFactory.createStrength(600, 0));
        caster.addPotionEffect(PotionEffectFactory.createRegeneration(600, 0));
        caster.addPotionEffect(PotionEffectFactory.createSpeed(600, 0));
        caster.addPotionEffect(PotionEffectFactory.createNausea(600, 0));
        caster.addPotionEffect(PotionEffectFactory.createJumpBoost(600, 0));
    }

    @Override
    public void A_FinalDurationEnd() {
        Player caster = getPlayer();
        if (caster == null) return;
        caster.setHealth(originalHealth);
        caster.sendMessage(ChatColor.RED + "원래대로 돌아왔습니다.");
    }

}
