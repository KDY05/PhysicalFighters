package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.AUC;
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

import java.util.HashMap;
import java.util.Map;

// TODO: 박스 범위 (y무시)에 있는 적 아래로 velocity 주기

public class Akainu extends Ability {
    // 범위 설정 상수
    private static final int OUTER_RADIUS = 4;      // 외부 범위 반지름
    private static final int INNER_RADIUS = 3;      // 내부 용암 반지름
    private static final int DEPTH = 4;             // 깊이
    private static final int LAVA_DEPTH = 3;        // 용암 깊이
    private static final long RESTORE_DELAY = 100L;  // 복구 지연시간

    private Location targetLocation = null;
    private final Map<Location, Material> originalBlocks = new HashMap<>();

    public Akainu() {
        InitAbility("아카이누", Type.Active_Immediately, Rank.SS,
                "철괴를 휘둘러 바라보는 곳의 땅을 용암으로 바꿉니다.",
                "5초 뒤에 용암이 다시 굳으며 적을 땅속에 가둡니다.",
                "(!) 화염 및 용암 데미지에 면역");
        InitAbility(30, 0, true);
        registerLeftClickEvent();
        EventManager.onEntityDamage.add(new EventData(this, 3));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            Player caster = Event.getPlayer();

            if (!isOwner(caster) || !isValidItem(Ability.DefaultItem)) {
                return -1;
            }

            if (PhysicalFighters.DamageGuard) {
                caster.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
                return -1;
            }

            targetLocation = AUC.getTargetLocation(caster, 40);
            if (targetLocation == null) {
                caster.sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
                return -1;
            }

            return 0;
        }

        if (CustomData == 3) {
            EntityDamageEvent Event2 = (EntityDamageEvent) event;
            if (isOwner(Event2.getEntity()) && isLavaFireDamage(Event2.getCause())) {
                Event2.setCancelled(true);
            }
        }

        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player caster = Event.getPlayer();

        if (targetLocation == null) {
            caster.sendMessage(ChatColor.RED + "능력을 사용할 수 없습니다.");
            return;
        }

        World world = caster.getWorld();
        Location center = targetLocation.clone();
        saveAndReplaceBlocks(world, center);
        placeLava(world, center);
        new LavaRestoreTask().runTaskLater(plugin, RESTORE_DELAY);
        caster.sendMessage(ChatColor.GOLD + "용암 지대를 만들었습니다!");

        targetLocation = null;
    }

    private boolean isLavaFireDamage(DamageCause cause) {
        return cause == DamageCause.LAVA ||
                cause == DamageCause.FIRE ||
                cause == DamageCause.FIRE_TICK;
    }

    private void saveAndReplaceBlocks(World world, Location center) {
        originalBlocks.clear();
        for (int x = -OUTER_RADIUS; x <= OUTER_RADIUS; x++) {
            for (int z = -OUTER_RADIUS; z <= OUTER_RADIUS; z++) {
                for (int y = -DEPTH; y <= 0; y++) {
                    Location blockLoc = center.clone().add(x, y, z);
                    Block block = world.getBlockAt(blockLoc);
                    originalBlocks.put(blockLoc.clone(), block.getType());
                    block.setType(Material.AIR);
                }
            }
        }
    }

    private void placeLava(World world, Location center) {
        for (int x = -INNER_RADIUS; x <= INNER_RADIUS; x++) {
            for (int z = -INNER_RADIUS; z <= INNER_RADIUS; z++) {
                for (int y = -LAVA_DEPTH; y <= 0; y++) {
                    Location lavaLoc = center.clone().add(x, y, z);
                    Block lavaBlock = world.getBlockAt(lavaLoc);
                    lavaBlock.setType(Material.LAVA);
                }
            }
        }
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
