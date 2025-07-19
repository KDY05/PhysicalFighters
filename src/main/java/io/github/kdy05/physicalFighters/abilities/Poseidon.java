package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;
import io.github.kdy05.physicalFighters.utils.AUC;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Poseidon extends Ability {
    // 능력 설정 상수
    private static final int GLASS_RANGE = 5;
    private static final int WATER_RANGE = 3;
    private static final int TARGET_RANGE = 40;
    private static final double SLOW_RANGE = 10.0;

    public Poseidon() {
        InitAbility("포세이돈", Type.Active_Immediately, Rank.SS,
                "바라보는 곳에 거대한 어항을 만들어 가둡니다.",
                "물 속에서 자신에게는 버프, 상대에게는 디버프를 겁니다.");
        InitAbility(60, 0, true);
        registerLeftClickEvent();
        EventManager.onPlayerMoveEvent.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                PlayerInteractEvent event0 = (PlayerInteractEvent) event;
                Player p = event0.getPlayer();
                if (!isOwner(p) || !isValidItem(Ability.DefaultItem)) {
                    return -1;
                }
                if (PhysicalFighters.DamageGuard) {
                    p.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
                    return -1;
                }
                return 0;
            }
            case 1 -> {
                PlayerMoveEvent event1 = (PlayerMoveEvent) event;
                Player caster = event1.getPlayer();
                if (!isOwner(caster)) return -1;
                if (!caster.getLocation().getBlock().getType().equals(Material.WATER)) return -1;
                caster.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 60, 0));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0));
                caster.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 0));
                AUC.splashTask(caster, caster.getLocation(), SLOW_RANGE,
                        entity -> entity.getLocation().getBlock().getType().equals(Material.WATER),
                        entity -> entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 0)));
                return -1;
            }
            default -> {
                return -1;
            }
        }
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

    private void createAquarium(Player player, Location center) {
        for (int y = 0; y <= 2 * GLASS_RANGE; y++) {
            for (int x = -GLASS_RANGE; x <= GLASS_RANGE; x++) {
                for (int z = -GLASS_RANGE; z <= GLASS_RANGE; z++) {
                    Location blockLoc = center.clone().add(x, y, z);
                    player.getWorld().getBlockAt(blockLoc).setType(Material.GLASS);
                }
            }
        }
        int waterOffset = GLASS_RANGE - WATER_RANGE;
        for (int y = waterOffset; y <= waterOffset + 2 * WATER_RANGE; y++) {
            for (int x = -WATER_RANGE; x <= WATER_RANGE; x++) {
                for (int z = -WATER_RANGE; z <= WATER_RANGE; z++) {
                    Location waterLoc = center.clone().add(x, y, z);
                    player.getWorld().getBlockAt(waterLoc).setType(Material.WATER);
                }
            }
        }
    }

}