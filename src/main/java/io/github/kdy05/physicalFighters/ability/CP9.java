package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class CP9 extends Ability {
    public CP9() {
        InitAbility("CP9", Type.Active_Immediately, Rank.SS,
                Usage.IronAttack + "지건 - 상대에게 6의 고정 데미지를 줍니다.",
                Usage.IronRight + "월보 - 바라보는 방향으로 빠르게 도약합니다.",
                Usage.Passive + "낙하 데미지를 무시합니다.");
        InitAbility(15, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerRightClickEvent();
        EventManager.onEntityDamage.add(new EventData(this, 2));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
                if (event0.getEntity() instanceof LivingEntity && isOwner(event0.getDamager())
                        && isValidItem(Ability.DefaultItem) && !ConfigManager.DamageGuard) {
                    return 0;
                }
            }
            case 1 -> {
                PlayerInteractEvent event1 = (PlayerInteractEvent) event;
                if (isOwner(event1.getPlayer()) && isValidItem(Ability.DefaultItem)) {
                    return 1;
                }
            }
            case 2 -> {
                EntityDamageEvent event2 = (EntityDamageEvent) event;
                if (isOwner(event2.getEntity()) && event2.getCause() == DamageCause.FALL) {
                    event2.setCancelled(true);
                    sendMessage(ChatColor.GREEN + "사뿐하게 떨어져 데미지를 받지 않았습니다.");
                }
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
                LivingEntity entity = (LivingEntity) event0.getEntity();
                AbilityUtils.piercingDamage(entity, 6);
                sendMessage(String.format(ChatColor.GREEN +
                        "%s에게 지건을 사용했습니다.", entity.getName()));
                entity.sendMessage(String.format(ChatColor.RED +
                        "%s(이)가 지건을 사용했습니다.", event0.getDamager().getName()));
            }
            case 1 -> {
                PlayerInteractEvent event1 = (PlayerInteractEvent) event;
                Player player = event1.getPlayer();
                Location playerLoc = player.getLocation();
                Vector direction = playerLoc.getDirection().normalize();

                Location explosionLoc = playerLoc.clone().subtract(direction.clone().multiply(1.5));
                player.getWorld().createExplosion(explosionLoc, 0.0F);

                Location targetLoc = playerLoc.clone().add(direction.clone().multiply(5));
                AbilityUtils.goVelocity(player, targetLoc, 5);
            }
        }
    }

}
