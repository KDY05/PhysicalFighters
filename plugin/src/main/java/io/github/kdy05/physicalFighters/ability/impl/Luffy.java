package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import org.bukkit.util.Vector;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import java.util.UUID;

public class Luffy extends Ability {
    public Luffy(UUID playerUuid) {
        super(AbilitySpec.builder("루피", Type.Active_Immediately, Rank.S)
                .showText(ShowText.Custom_Text)
                .guide(Usage.IronLeft + "사거리가 긴 주먹질을 합니다.",
                        Usage.GoldLeft + "체력을 5 소모하여 30초간 여러 버프를 얻습니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        if (isOwner(event0.getPlayer()) && isValidItem(Ability.DefaultItem) && !InvincibilityManager.isDamageGuard()) {
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
        if (CustomData == 1) {
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
                AbilityUtils.splashTask(caster, blockLoc, 2.5, entity -> entity.damage(2));
            }
        } else if (CustomData == 2) {
            AbilityUtils.piercingDamage(caster, 5.0);
            caster.addPotionEffect(PotionEffectFactory.createJumpBoost(600, 0));
            caster.addPotionEffect(PotionEffectFactory.createSpeed(600, 0));
            caster.addPotionEffect(PotionEffectFactory.createNausea(200, 0));
            caster.addPotionEffect(PotionEffectFactory.createStrength(600, 0));
            caster.addPotionEffect(PotionEffectFactory.createResistance(600, 0));
            caster.sendMessage(ChatColor.RED + "기어 세컨드를 사용하였습니다.");
        }
    }

}
