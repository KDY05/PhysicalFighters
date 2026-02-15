package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Crocodile extends Ability {
    // 능력 설정 상수
    private static final int SAND_RANGE = 5;
    private static final int SAND_HEIGHT = 6;
    private static final int STORM_DAMAGE = 5;
    private static final int STORM_DURATION = 7;
    private static final long STORM_INTERVAL = 30L;
    private static final double ENTITY_CHECK_RADIUS = 50.0;

    public Crocodile(UUID playerUuid) {
        super(AbilitySpec.builder("크로커다일", Type.ActiveImmediately, Rank.S)
                .cooldown(20)
                .guide(Usage.IronLeft + "자신의 주변의 있는 블록을 모래로 바꿉니다.",
                        Usage.IronRight + "모래 위에 있는 50칸 이내의 적에게",
                        "10초 동안 피해를 주며 끌어당기는 모래 바람을 일으킵니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
        registerRightClickEvent();
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player p = event0.getPlayer();
        if (!isOwner(p) || !isValidItem(Ability.DefaultItem)) {
            return -1;
        }
        if (InvincibilityManager.isDamageGuard()) {
            p.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
            return -1;
        }
        return CustomData;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player caster = event0.getPlayer();
        if (CustomData == 0) {
            convertToSand(caster);
        } else if (CustomData == 1) {
            new SandstormTask(caster).runTaskTimer(plugin, 0, STORM_INTERVAL);
            caster.sendMessage(ChatColor.GOLD + "모래 바람을 시작합니다!");
        }
    }

    private void convertToSand(Player caster) {
        World world = caster.getWorld();
        Location center = caster.getLocation();
        int converted = 0;
        for (int x = -SAND_RANGE; x <= SAND_RANGE; x++) {
            for (int z = -SAND_RANGE; z <= SAND_RANGE; z++) {
                for (double y = -0.5 * SAND_HEIGHT; y <= 0.5 * SAND_HEIGHT; y++) {
                    Location blockLoc = center.clone().add(x, y, z);
                    Material blockType = world.getBlockAt(blockLoc).getType();
                    if (blockType != Material.AIR && blockType != Material.BEDROCK) {
                        world.getBlockAt(blockLoc).setType(Material.SAND);
                        converted++;
                    }
                }
            }
        }
        caster.sendMessage(ChatColor.YELLOW + "주변 " + converted + "개의 블록을 모래로 변환했습니다!");
    }

    private static class SandstormTask extends BukkitRunnable {
        private final Player caster;
        private int tickCount = 0;

        public SandstormTask(Player caster) {
            this.caster = caster;
        }

        @Override
        public void run() {
            if (!caster.isOnline()) {
                cancel();
                return;
            }
            if (tickCount >= STORM_DURATION) {
                cancel();
                caster.sendMessage(ChatColor.GREEN + "모래 바람이 끝났습니다.");
                return;
            }
            checkNearbyEntitiesInSandstorm();
            tickCount++;
        }

        private void checkNearbyEntitiesInSandstorm() {
            Location casterLoc = caster.getLocation();
            caster.getWorld().getNearbyEntities(casterLoc, ENTITY_CHECK_RADIUS, ENTITY_CHECK_RADIUS, ENTITY_CHECK_RADIUS)
                    .stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .map(entity -> (LivingEntity) entity)
                    .filter(entity -> entity != caster) // 시전자 제외
                    .filter(this::isEntityInSandArea)
                    .forEach(entity -> applySandstormEffect(entity, caster));
        }

        private boolean isEntityInSandArea(LivingEntity entity) {
            Location entityLoc = entity.getLocation();
            World world = entity.getWorld();
            // 엔티티 주변 3x3x3 영역에 모래가 있는지 확인
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    for (int y = -1; y <= 1; y++) {
                        Location checkLoc = entityLoc.clone().add(x, y, z);
                        if (world.getBlockAt(checkLoc).getType() == Material.SAND) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private void applySandstormEffect(LivingEntity target, Player caster) {
            target.damage(STORM_DAMAGE);
            Vector pullDirection = caster.getLocation().toVector()
                    .subtract(target.getLocation().toVector())
                    .normalize().multiply(0.6);
            pullDirection.setY(pullDirection.getY() + 0.4);
            target.setVelocity(target.getVelocity().add(pullDirection));
            target.addPotionEffect(PotionEffectFactory.createBlindness(30, 0));
            target.sendMessage(ChatColor.YELLOW + "모래 바람에 휩쓸렸습니다!");
        }
    }

}