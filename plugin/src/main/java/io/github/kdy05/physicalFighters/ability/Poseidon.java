package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import io.github.kdy05.physicalFighters.utils.PotionEffectFactory;

public class Poseidon extends Ability {
    // 능력 설정 상수
    private static final int GLASS_RANGE = 4;
    private static final int WATER_RANGE = 2;
    private static final int TARGET_RANGE = 40;
    private static final double SLOW_RANGE = 10.0;
    private static final int TELEPORT_HEIGHT = 3;

    private Location targetLocation = null;

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
        if (CustomData == 0) {
            PlayerInteractEvent event0 = (PlayerInteractEvent) event;
            Player player = event0.getPlayer();
            if (!isOwner(player) || !isValidItem(Ability.DefaultItem)) {
                return -1;
            }

            targetLocation = AbilityUtils.getTargetLocation(player, TARGET_RANGE);
            if (targetLocation == null) {
                player.sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
                return -1;
            }

            if (ConfigManager.DamageGuard) {
                player.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
                return -1;
            }
            return 0;
        } else if (CustomData == 1) {
            PlayerMoveEvent event1 = (PlayerMoveEvent) event;
            Player caster = event1.getPlayer();
            if (!isOwner(caster)) return -1;
            if (!caster.getLocation().getBlock().getType().equals(Material.WATER)) return -1;
            caster.addPotionEffect(PotionEffectFactory.createWaterBreathing(60, 0));
            caster.addPotionEffect(PotionEffectFactory.createSpeed(60, 0));
            caster.addPotionEffect(PotionEffectFactory.createResistance(60, 0));
            AbilityUtils.splashTask(caster, caster.getLocation(), SLOW_RANGE,
                    entity -> entity.getLocation().getBlock().getType().equals(Material.WATER),
                    entity -> entity.addPotionEffect(PotionEffectFactory.createSlowness(60, 0)));
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player player = event0.getPlayer();

        if (targetLocation == null) {
            player.sendMessage(ChatColor.RED + "능력을 사용할 수 없습니다.");
            return;
        }

        createAquarium(player, targetLocation);
        targetLocation = null;
    }

    private void createAquarium(Player caster, Location center) {
        teleportPlayersInRange(caster, center);
        
        AbilityUtils.createBox(center, Material.GLASS, GLASS_RANGE, 2 * GLASS_RANGE + 1);
        AbilityUtils.createBox(center.clone().add(0, GLASS_RANGE - WATER_RANGE, 0),
                Material.WATER, WATER_RANGE, 2 * WATER_RANGE + 1);
    }

    private void teleportPlayersInRange(Player caster, Location center) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == caster) continue;
            Location playerLoc = player.getLocation();
            if (isPlayerInAquariumRange(playerLoc, center)) {
                Location teleportLoc = center.clone().add(0, TELEPORT_HEIGHT, 0);
                teleportLoc.setYaw(playerLoc.getYaw());
                teleportLoc.setPitch(playerLoc.getPitch());
                player.teleport(teleportLoc);
            }
        }
    }

    private boolean isPlayerInAquariumRange(Location playerLoc, Location center) {
        double dx = Math.abs(playerLoc.getX() - center.getX());
        double dy = playerLoc.getY() - center.getY();
        double dz = Math.abs(playerLoc.getZ() - center.getZ());

        return dx <= GLASS_RANGE &&
                dy >= 0 && dy <= 2 * GLASS_RANGE + 1 &&
                dz <= GLASS_RANGE;
    }

}