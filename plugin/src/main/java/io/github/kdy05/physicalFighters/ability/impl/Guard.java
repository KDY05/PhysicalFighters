package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import org.bukkit.Bukkit;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import java.util.UUID;

public class Guard extends Ability {
    // 박스 크기 관련 상수
    private static final int BOX_RADIUS = 5;
    private static final int BOX_HEIGHT = 9;
    private static final int INNER_RADIUS = 3;
    private static final int INNER_HEIGHT = 6;
    private static final int TELEPORT_HEIGHT = 2;

    private Location targetLocation = null;

    public Guard(UUID playerUuid) {
        super(AbilitySpec.builder("목둔", Type.ActiveImmediately, Rank.A)
                .cooldown(30)
                .guide(Usage.IronLeft + "바라보는 위치에 나무벽을 설치합니다. 주위에 플레이어가 있으면 가둡니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player caster = event0.getPlayer();

        if (!isOwner(event0.getPlayer()) || !isValidItem(Ability.DefaultItem))
            return -1;

        targetLocation = AbilityUtils.getTargetLocation(caster, 40);
        if (targetLocation == null) {
            caster.sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
            return -1;
        }

        if (InvincibilityManager.isDamageGuard()) {
            caster.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
            return -1;
        }

        return 0;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player p = event0.getPlayer();

        if (targetLocation == null) {
            p.sendMessage(ChatColor.RED + "능력을 사용할 수 없습니다.");
            return;
        }

        Location center = targetLocation.clone();
        teleportPlayersInRange(p, center);
        buildWalls(center);

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
                player.addPotionEffect(PotionEffectFactory.createMiningFatigue(15 * 20, 1));
            }
        }
    }

    private boolean isPlayerInBoxRange(Location playerLoc, Location center) {
        double dx = Math.abs(playerLoc.getX() - center.getX());
        double dy = playerLoc.getY() - center.getY();
        double dz = Math.abs(playerLoc.getZ() - center.getZ());

        return dx <= BOX_RADIUS &&
                dy >= 0 && dy <= BOX_HEIGHT &&
                dz <= BOX_RADIUS;
    }

    private void buildWalls(Location center) {
        AbilityUtils.createBox(center, Material.OAK_PLANKS, BOX_RADIUS, BOX_HEIGHT);
        AbilityUtils.createBox(center.clone().add(0,1,0), Material.AIR, INNER_RADIUS, INNER_HEIGHT);
    }

}