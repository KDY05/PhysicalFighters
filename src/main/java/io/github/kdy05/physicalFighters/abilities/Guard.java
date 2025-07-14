package io.github.kdy05.physicalFighters.abilities;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.utils.AUC;

public class Guard extends Ability {
    // 박스 크기 관련 상수
    private static final int BOX_RADIUS = 5;
    private static final int BOX_HEIGHT = 8;
    private static final int INNER_RADIUS = 3;
    private static final int INNER_HEIGHT = 6;
    private static final int TELEPORT_HEIGHT = 2;

    private Location targetLocation = null;

    public Guard() {
        InitAbility("목둔", Type.Active_Immediately, Rank.A,
                "바라보는 위치에 나무벽을 설치합니다. 주위에 플레이어가 있으면 가둡니다.");
        InitAbility(30, 0, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player caster = Event.getPlayer();

        if (!isOwner(Event.getPlayer()) || !isValidItem(Ability.DefaultItem))
            return -1;

        targetLocation = AUC.getTargetLocation(caster, 40);
        if (targetLocation == null) {
            caster.sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
            return -1;
        }

        if (EventManager.DamageGuard) {
            caster.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
            return -1;
        }

        return 0;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();

        if (targetLocation == null) {
            p.sendMessage(ChatColor.RED + "능력을 사용할 수 없습니다.");
            return;
        }

        Location center = targetLocation.clone();
        teleportPlayersInRange(p, center);
        buildOuterWalls(p, center);
        buildInnerSpace(p, center);

        targetLocation = null;
    }

    private void teleportPlayersInRange(Player caster, Location center) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == caster) continue; // 시전자 제외

            Location playerLoc = player.getLocation();
            if (isPlayerInBoxRange(playerLoc, center)) {
                Location teleportLoc = center.clone().add(0, TELEPORT_HEIGHT, 0);
                teleportLoc.setYaw(playerLoc.getYaw());
                teleportLoc.setPitch(playerLoc.getPitch());
                player.teleport(teleportLoc);
                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 15 * 20, 1));
            }
        }
    }

    private boolean isPlayerInBoxRange(Location playerLoc, Location center) {
        double dx = Math.abs(playerLoc.getX() - center.getX());
        double dy = playerLoc.getY() - center.getY();
        double dz = Math.abs(playerLoc.getZ() - center.getZ());

        return dx <= Guard.BOX_RADIUS &&
                dy >= 0 && dy <= Guard.BOX_HEIGHT &&
                dz <= Guard.BOX_RADIUS;
    }

    private void buildOuterWalls(Player player, Location center) {
        for (int y = 0; y <= BOX_HEIGHT; y++) {
            for (int x = -BOX_RADIUS; x <= BOX_RADIUS; x++) {
                for (int z = -BOX_RADIUS; z <= BOX_RADIUS; z++) {
                    Location blockLoc = center.clone().add(x, y, z);
                    Block block = player.getWorld().getBlockAt(blockLoc);

                    if (block.getType() != Material.BEDROCK) {
                        block.setType(Material.OAK_PLANKS);
                    }
                }
            }
        }
    }

    private void buildInnerSpace(Player player, Location center) {
        for (int y = 1; y <= INNER_HEIGHT; y++) {
            for (int x = -INNER_RADIUS; x <= INNER_RADIUS; x++) {
                for (int z = -INNER_RADIUS; z <= INNER_RADIUS; z++) {
                    Location blockLoc = center.clone().add(x, y, z);
                    Block block = player.getWorld().getBlockAt(blockLoc);

                    if (block.getType() != Material.BEDROCK) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
}