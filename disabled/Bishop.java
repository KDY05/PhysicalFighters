package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Bishop extends Ability {
    public Bishop() {
        InitAbility("비숍", Type.Active_Immediately, Rank.B,
                "철괴 왼클릭시 맞은 사람에게 각종 축복을 겁니다.", "철괴 오른클릭시 자신에게 각종 축복을 겁니다.",
                "금괴를 적에게 왼클릭시 각종 저주를 겁니다.", "세 기능은 쿨타임을 공유하며 모든 효과 지속시간은",
                "15초입니다.");
        InitAbility(30, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
        registerRightClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                if (((Event1.getEntity() instanceof Player)) &&
                        (isOwner(Event1.getDamager())) && !ConfigManager.DamageGuard) {
                    if (isValidItem(Ability.DefaultItem))
                        return 0;
                    if (isValidItem(Material.GOLD_INGOT)) {
                        return 2;
                    }
                }
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                if ((isOwner(Event2.getPlayer())) &&
                        (isValidItem(Ability.DefaultItem)) && !ConfigManager.DamageGuard) {
                    return 1;
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                Player p0 = (Player) Event0.getEntity();
                p0.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
                        300, 0));
                p0.addPotionEffect(new PotionEffect(
                        PotionEffectType.RESISTANCE, 300, 0));
                p0.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,
                        300, 0));
                p0.addPotionEffect(new PotionEffect(
                        PotionEffectType.WATER_BREATHING, 300, 0));
                p0.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,
                        300, 0));
                p0.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
                        300, 0));
                p0.sendMessage(ChatColor.GREEN + "비숍이 당신에게 축복을 걸었습니다. 15초 지속.");
                Event0.setCancelled(true);
                break;
            case 1:
                PlayerInteractEvent Event1 = (PlayerInteractEvent) event;
                Player p1 = Event1.getPlayer();
                p1.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
                        300, 0));
                p1.addPotionEffect(new PotionEffect(
                        PotionEffectType.RESISTANCE, 300, 0));
                p1.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,
                        300, 0));
                p1.addPotionEffect(new PotionEffect(
                        PotionEffectType.WATER_BREATHING, 300, 0));
                p1.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,
                        300, 0));
                p1.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
                        300, 0));
                p1.sendMessage(ChatColor.GREEN + "자신에게 축복을 걸었습니다. 15초 지속.");
                break;
            case 2:
                EntityDamageByEntityEvent Event2 = (EntityDamageByEntityEvent) event;
                Player p2 = (Player) Event2.getEntity();
                p2.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,
                        300, 0));
                p2.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,
                        300, 0));
                p2.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
                        300, 0));
                p2.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE,
                        300, 0));
                p2.sendMessage(ChatColor.RED + "비숍이 당신에게 저주를 걸었습니다. 15초 지속.");
        }
    }
}
