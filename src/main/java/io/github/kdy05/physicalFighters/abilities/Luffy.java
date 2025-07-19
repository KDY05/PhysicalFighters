package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import org.bukkit.util.Vector;
import io.github.kdy05.physicalFighters.core.Ability;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import io.github.kdy05.physicalFighters.utils.AUC;

public class Luffy extends Ability {
    public Luffy() {
        InitAbility("루피", Type.Active_Immediately, Rank.S,
                Usage.IronLeft + "사거리가 긴 주먹질을 합니다.",
                Usage.GoldLeft + "체력을 5 소모하여 30초간 여러 버프를 얻습니다.");
        InitAbility(0, 0, true, ShowText.Custom_Text);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        if (isOwner(event0.getPlayer()) && isValidItem(Ability.DefaultItem) && !PhysicalFighters.DamageGuard) {
            return 1;
        }
        if (isOwner(event0.getPlayer()) && event0.getPlayer().getHealth() >= 6.0D && (isValidItem(Material.GOLD_INGOT))) {
            return 2;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player caster = event0.getPlayer();
        switch (CustomData) {
            case 1 -> {
                Location origin = caster.getLocation();
                Vector direction = origin.getDirection();
                for (int i = 2; i <= 5; i++) {
                    Location blockLoc = origin.clone().add(direction.clone().multiply(i));
                    Block targetBlock = blockLoc.getBlock();
                    if (targetBlock.getType() != Material.SANDSTONE) {
                        Material originalType = targetBlock.getType();
                        targetBlock.setType(Material.SANDSTONE);
                        Bukkit.getScheduler().runTaskLater(plugin,
                                () -> targetBlock.setType(originalType), 5L);
                    }
                    AUC.splashDamage(caster, blockLoc, 2.5, 2);
                }
            }
            case 2 -> {
                AUC.piercingDamage(caster, 5.0);
                caster.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 600, 0));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 0));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 0));
                caster.sendMessage(ChatColor.RED + "기어 세컨드를 사용하였습니다.");
            }
        }
    }

}
