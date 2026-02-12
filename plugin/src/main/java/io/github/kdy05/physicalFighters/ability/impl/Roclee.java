package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import java.util.UUID;

public class Roclee extends Ability {
    private boolean fallDamageImmune = false;

    public Roclee(UUID playerUuid) {
        super(AbilitySpec.builder("록리", Type.Active_Immediately, Rank.S)
                .cooldown(20)
                .guide(Usage.IronAttack + "피해를 입히며 공중으로 끌어올립니다.",
                        "이때 시전자는 5초간 낙하 대미지를 받지 않습니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamageByEntity(new EventData(this, 0));
        EventManager.registerEntityDamage(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (isOwner(event0.getDamager()) && event0.getEntity() instanceof LivingEntity
                    && isValidItem(Ability.DefaultItem) && !InvincibilityManager.isDamageGuard()) {
                return 0;
            }
        } else if (CustomData == 1) {
            EntityDamageEvent event1 = (EntityDamageEvent) event;
            if (isOwner(event1.getEntity()) && fallDamageImmune
                    && event1.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event1.setCancelled(true);
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        LivingEntity target = (LivingEntity) event0.getEntity();
        Player attacker = getPlayer();
        if (attacker == null) return;

        target.getWorld().createExplosion(target.getLocation(), 0.0F);

        Location targetNewLoc = findSafeTeleportLocation(target.getLocation());
        Location attackerNewLoc = findSafeTeleportLocation(attacker.getLocation());

        target.teleport(targetNewLoc);
        attacker.teleport(attackerNewLoc);

        fallDamageImmune = true;
        Bukkit.getScheduler().runTaskLater(plugin, () -> fallDamageImmune = false, 100);

        target.getWorld().createExplosion(targetNewLoc, 1.0F);
        target.damage(8);
    }

    private Location findSafeTeleportLocation(Location originalLoc) {
        Location safeLoc = originalLoc.clone();
        int maxHeight = 8;

        for (int y = 1; y <= 8; y++) {
            Location checkLoc = originalLoc.clone().add(0, y, 0);
            Material blockType = checkLoc.getBlock().getType();
            if (blockType.isSolid()) {
                maxHeight = y - 1;
                break;
            }
        }

        safeLoc.add(0, maxHeight, 0);
        return safeLoc;
    }

}
