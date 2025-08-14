package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;
import org.bukkit.*;
import io.github.kdy05.physicalFighters.core.Ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;

public class Aokizi extends Ability {
    public Aokizi() {
        InitAbility("아오키지", Type.Active_Immediately, Rank.S,
               Usage.IronLeft + "자신이 보고있는 방향으로 얼음을 날립니다.",
              Usage.IronRight + "바라보고 있는 5칸 이내의 물을 얼립니다.",
              Usage.Passive + "자신이 공격한 적을 2초간 느리게 만듭니다.");
        InitAbility(1, 0, true, ShowText.Custom_Text);
        registerLeftClickEvent();
        registerRightClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 2));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                PlayerInteractEvent event0 = (PlayerInteractEvent) event;
                if (isOwner(event0.getPlayer()) && isValidItem(DefaultItem) && !ConfigManager.DamageGuard)
                    return 0;
            }
            case 1 -> {
                PlayerInteractEvent event1 = (PlayerInteractEvent) event;
                if (isOwner(event1.getPlayer()) && isValidItem(DefaultItem) && !ConfigManager.DamageGuard) {
                    Player p = event1.getPlayer();
                    Location location = AbilityUtils.getTargetLocation(p, 5);
                    if (location == null) {
                        event1.getPlayer().sendMessage(ChatColor.GREEN + "5칸 이내의 물만 얼릴 수 있습니다.");
                        break;
                    }
                    Block block = location.getBlock();
                    if (block.getType() != Material.WATER) break;
                    block.setType(Material.ICE);
                }
            }
            case 2 -> {
                EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
                if (isOwner(event2.getDamager()) && !ConfigManager.DamageGuard
                        && event2.getEntity() instanceof LivingEntity entity) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0));
                }
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Location l = event0.getPlayer().getLocation();
        Location l2 = event0.getPlayer().getLocation();
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));
        for (int i = 1; i < 10; i++) {
            l2.setX(l.getX() + (i + 1) * (Math.sin(degrees) * Math.cos(ydeg)));
            l2.setY(l.getY() + (i + 1) * Math.sin(ydeg));
            l2.setZ(l.getZ() + (i + 1) * (Math.cos(degrees) * Math.cos(ydeg)));
            Block block = event0.getPlayer().getWorld().getBlockAt(l2);
            if (block.getType() != Material.ICE)
                new ExplosionTimer(block.getType(), block).runTaskLater(plugin, 15L);
            block.setType(Material.ICE);
            AbilityUtils.splashTask(event0.getPlayer(), block.getLocation(), 2.5, entity -> entity.damage(8));
        }
    }

    static class ExplosionTimer extends BukkitRunnable {
        private final World world;
        private final Location location;
        private final Material blockid;

        ExplosionTimer(Material blockid, Block block) {
            this.world = block.getWorld();
            this.location = block.getLocation();
            this.blockid = blockid;
        }

        public void run() {
            this.world.getBlockAt(this.location).setType(this.blockid);
        }
    }
}
