package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;
import io.github.kdy05.physicalFighters.utils.AUC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Poseidon extends Ability {
    // 능력 설정 상수
    private static final int GLASS_RANGE = 5;       // 유리 벽 범위 (12x12)
    private static final int WATER_RANGE = 2;       // 물 범위 (6x6)
    private static final int GLASS_HEIGHT = 8;      // 유리 벽 높이 (9층)
    private static final int WATER_HEIGHT = 6;      // 물 높이 (6층)
    private static final int TARGET_RANGE = 40;     // 타겟 감지 범위
    private static final double SLOW_RANGE = 10.0;  // 물 속 슬로우 범위
    private static final int EFFECT_DURATION = 999999999; // 무한 지속시간

    public Poseidon() {
        InitAbility("포세이돈", Type.Active_Immediately, Rank.SS,
                "바라보는 곳에 거대한 어항을 만들어 가둡니다.",
                "물에서 숨을 쉴 수 있습니다.");
        InitAbility(60, 0, true);
        registerLeftClickEvent();
        EventManager.onEntityDamage.add(new EventData(this, 3));
        EventManager.onPlayerMoveEvent.add(new EventData(this, 4));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        return switch (CustomData) {
            case 0 -> handleAquariumCreation(event);
            case 3 -> handleDrowningProtection(event);
            case 4 -> handleWaterEffects(event);
            default -> -1;
        };
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Location targetLoc = AUC.getTargetLocation(p, TARGET_RANGE);
        if (targetLoc == null) {
            p.sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
            return;
        }
        createAquarium(p, targetLoc);
    }

    private int handleAquariumCreation(Event event) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        if (!isOwner(p) || !isValidItem(Ability.DefaultItem)) {
            return -1;
        }
        if (EventManager.DamageGuard) {
            p.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
            return -1;
        }
        return 0;
    }

    private int handleDrowningProtection(Event event) {
        EntityDamageEvent Event2 = (EntityDamageEvent) event;
        if (isOwner(Event2.getEntity()) && Event2.getCause() == DamageCause.DROWNING) {
            Event2.setCancelled(true);
        }
        return -1;
    }

    private int handleWaterEffects(Event event) {
        PlayerMoveEvent e = (PlayerMoveEvent) event;
        Player owner = e.getPlayer();
        if (!isOwner(owner)) return -1;
        // 소유자 물 속 버프
        applyOwnerWaterBuffs(owner);
        // 주변 적들에게 슬로우 적용
        applyEnemySlowness(owner);
        return -1;
    }

    private void applyOwnerWaterBuffs(Player owner) {
        if (owner.getLocation().getBlock().isLiquid()) {
            // 물 속에서 스피드와 저항 버프
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, EFFECT_DURATION, 1));
            owner.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, EFFECT_DURATION, 1));
        } else {
            // 물 밖에서는 버프 제거
            owner.removePotionEffect(PotionEffectType.SPEED);
            owner.removePotionEffect(PotionEffectType.RESISTANCE);
        }
    }

    /**
     * 주변 적들에게 슬로우 적용
     */
    private void applyEnemySlowness(Player owner) {
        Location ownerLoc = owner.getLocation();

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target == owner) continue;

            Location targetLoc = target.getLocation();

            // 10블록 이내 + 물 속에 있는 적에게 슬로우
            if (ownerLoc.distance(targetLoc) <= SLOW_RANGE &&
                    targetLoc.getBlock().isLiquid()) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
            }
        }
    }

    private void createAquarium(Player player, Location center) {
        for (int y = 0; y <= GLASS_HEIGHT; y++) {
            for (int x = -GLASS_RANGE; x <= GLASS_RANGE; x++) {
                for (int z = -GLASS_RANGE; z <= GLASS_RANGE; z++) {
                    Location blockLoc = center.clone().add(x, y, z);
                    player.getWorld().getBlockAt(blockLoc).setType(Material.GLASS);
                }
            }
        }
        for (int y = 1; y <= WATER_HEIGHT; y++) {
            for (int x = -WATER_RANGE; x <= WATER_RANGE; x++) {
                for (int z = -WATER_RANGE; z <= WATER_RANGE; z++) {
                    Location waterLoc = center.clone().add(x, y, z);
                    player.getWorld().getBlockAt(waterLoc).setType(Material.WATER);
                }
            }
        }
    }

}