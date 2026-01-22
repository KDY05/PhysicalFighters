package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Akainu extends Ability {
    // 범위 설정 상수
    private static final int OUTER_RADIUS = 3;      // 외부 범위 반지름
    private static final int INNER_RADIUS = 2;      // 내부 용암 반지름
    private static final int DEPTH = 4;             // 깊이
    private static final int LAVA_DEPTH = 3;        // 용암 깊이
    private static final long RESTORE_DELAY = 80L;  // 복구 지연시간

    private Location targetLocation = null;
    private final Map<Location, Material> originalBlocks = new HashMap<>();

    public Akainu() {
        InitAbility("아카이누", Type.Active_Immediately, Rank.SS,
               Usage.IronLeft + "바라보는 곳의 땅을 용암으로 바꿉니다.",
                "4초 뒤에 용암이 다시 굳으며 적을 땅속에 가둡니다.",
                Usage.Passive + "화염 및 용암 대미지를 무시합니다.");
        InitAbility(45, 0, true);
        registerLeftClickEvent();
        EventManager.onEntityDamage.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent event0 = (PlayerInteractEvent) event;
            Player caster = event0.getPlayer();

            if (!isOwner(caster) || !isValidItem(Ability.DefaultItem)) {
                return -1;
            }

            if (ConfigManager.DamageGuard) {
                caster.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
                return -1;
            }

            targetLocation = AbilityUtils.getTargetLocation(caster, 40);
            if (targetLocation == null) {
                caster.sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
                return -1;
            }

            return 0;
        } else if (CustomData == 1) {
            EntityDamageEvent event1 = (EntityDamageEvent) event;
            if (isOwner(event1.getEntity()) && isLavaFireDamage(event1.getCause())) {
                event1.setCancelled(true);
            }
        }

        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player caster = event0.getPlayer();

        if (targetLocation == null) {
            caster.sendMessage(ChatColor.RED + "능력을 사용할 수 없습니다.");
            return;
        }

        World world = caster.getWorld();
        Location center = targetLocation.clone();
        saveAndReplaceBlocks(world, center);
        placeLava(center);
        new LavaRestoreTask().runTaskLater(plugin, RESTORE_DELAY);

        targetLocation = null;
    }

    private boolean isLavaFireDamage(DamageCause cause) {
        return cause == DamageCause.LAVA ||
                cause == DamageCause.FIRE ||
                cause == DamageCause.FIRE_TICK;
    }

    private void saveAndReplaceBlocks(World world, Location center) {
        originalBlocks.clear();
        for (int y = -DEPTH; y <= 0; y++) {
            for (int x = -OUTER_RADIUS; x <= OUTER_RADIUS; x++) {
                for (int z = -OUTER_RADIUS; z <= OUTER_RADIUS; z++) {
                    Location blockLoc = center.clone().add(x, y, z);
                    Block block = world.getBlockAt(blockLoc);
                    originalBlocks.put(blockLoc.clone(), block.getType());
                }
            }
        }
        AbilityUtils.createBox(center.clone().add(0, -DEPTH, 0), Material.AIR, OUTER_RADIUS, DEPTH + 1);
        
        AbilityUtils.splashTask(getPlayer(), center, OUTER_RADIUS, entity -> {
            Vector downwardVelocity = new Vector(0, -1.0, 0);
            entity.setVelocity(entity.getVelocity().add(downwardVelocity));
        });
    }

    private void placeLava(Location center) {
        AbilityUtils.createBox(center.clone().add(0, -LAVA_DEPTH, 0), Material.LAVA, INNER_RADIUS, LAVA_DEPTH);
    }

    private class LavaRestoreTask extends BukkitRunnable {
        @Override
        public void run() {
            for (Map.Entry<Location, Material> entry : originalBlocks.entrySet()) {
                Location loc = entry.getKey();
                Material originalType = entry.getValue();

                if (loc.getWorld() != null) {
                    loc.getWorld().getBlockAt(loc).setType(originalType);
                }
            }
            originalBlocks.clear();
        }
    }
}
