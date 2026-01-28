package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;
import org.bukkit.*;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;

public class Aokizi extends Ability {
    public Aokizi() {
        super(AbilitySpec.builder("아오키지", Type.Active_Immediately, Rank.S)
                .cooldown(1)
                .showText(ShowText.Custom_Text)
                .guide(Usage.IronLeft + "자신이 보고있는 방향으로 얼음을 날립니다.",
                        Usage.IronRight + "바라보고 있는 5칸 이내의 물을 얼립니다.",
                        Usage.Passive + "자신이 공격한 적을 2초간 느리게 만듭니다.")
                .build());
        registerLeftClickEvent();
        registerRightClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 2));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent event0 = (PlayerInteractEvent) event;
            if (isOwner(event0.getPlayer()) && isValidItem(DefaultItem) && !InvincibilityManager.isDamageGuard())
                return 0;
        } else if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            if (isOwner(event1.getPlayer()) && isValidItem(DefaultItem) && !InvincibilityManager.isDamageGuard()) {
                Player p = event1.getPlayer();
                Location location = AbilityUtils.getTargetLocation(p, 5);
                if (location == null) {
                    event1.getPlayer().sendMessage(ChatColor.GREEN + "5칸 이내의 물만 얼릴 수 있습니다.");
                    return -1;
                }
                Block block = location.getBlock();
                if (block.getType() != Material.WATER) return -1;
                block.setType(Material.ICE);
            }
        } else if (CustomData == 2) {
            EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
            if (isOwner(event2.getDamager()) && !InvincibilityManager.isDamageGuard()
                    && event2.getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) event2.getEntity();
                entity.addPotionEffect(PotionEffectFactory.createSlowness(40, 0));
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
