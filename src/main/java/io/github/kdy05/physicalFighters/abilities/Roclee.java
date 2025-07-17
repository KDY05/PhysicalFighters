package io.github.kdy05.physicalFighters.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Roclee extends Ability {
    public Roclee() {
        InitAbility("록리", Type.Active_Immediately, Rank.S,
                Usage.IronAttack + "피해를 입히며 공중으로 끌어올립니다.",
                "이때 시전자는 5초간 낙하 데미지를 받지 않습니다.");
        InitAbility(20, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (isOwner(event0.getDamager()) && event0.getEntity() instanceof LivingEntity
                && isValidItem(Ability.DefaultItem) && !EventManager.DamageGuard) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        LivingEntity target = (LivingEntity) event0.getEntity();
        Player attacker = getPlayer();

        target.getWorld().createExplosion(target.getLocation(), 0.0F);

        Location targetNewLoc = findSafeTeleportLocation(target.getLocation());
        Location attackerNewLoc = findSafeTeleportLocation(attacker.getLocation());

        target.teleport(targetNewLoc);
        attacker.teleport(attackerNewLoc);

        AttributeInstance safeFall = attacker.getAttribute(Attribute.SAFE_FALL_DISTANCE);
        if (safeFall == null) return;
        safeFall.setBaseValue(20.0);
        Bukkit.getScheduler().runTaskLater(plugin, () -> safeFall.setBaseValue(3.0), 100);

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
