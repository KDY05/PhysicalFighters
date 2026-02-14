package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;

public class Gaara extends Ability {
    private Location targetLocation = null;

    public Gaara(UUID playerUuid) {
        super(AbilitySpec.builder("가아라", Type.ActiveImmediately, Rank.B)
                .guide(Usage.IronLeft + "바라보는 방향에 모래를 떨어뜨리고, 잠시 후 폭발시킵니다.")
                .cooldown(45)
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

        if (!isOwner(event0.getPlayer()) || !isValidItem(Ability.DefaultItem) || InvincibilityManager.isDamageGuard()) {
            return -1;
        }

        targetLocation = AbilityUtils.getTargetLocation(caster, 30);
        if (targetLocation == null) {
            caster.sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
            return -1;
        }

        return 0;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player caster = event0.getPlayer();

        if (targetLocation == null) {
            caster.sendMessage(ChatColor.RED + "능력을 사용할 수 없습니다.");
            return;
        }

        Location center = targetLocation.clone().add(0, 4, 0); // 4블록 위로 올림
        Block targetBlock = caster.getWorld().getBlockAt(targetLocation);
        new Exploder(targetBlock).runTaskLater(plugin, 80L);

        AbilityUtils.createBox(center, Material.SAND, 3, 5);
        targetLocation = null;
    }

    static class Exploder extends BukkitRunnable {
        World world;
        Location location;

        Exploder(Block block) {
            this.world = block.getWorld();
            this.location = block.getLocation().clone().add(0, 2, 0);
        }

        @Override
        public void run() {
            this.world.createExplosion(this.location, 5.0F);
            this.world.createExplosion(this.location, 5.0F);
            this.world.createExplosion(this.location, 5.0F);
        }
    }
}