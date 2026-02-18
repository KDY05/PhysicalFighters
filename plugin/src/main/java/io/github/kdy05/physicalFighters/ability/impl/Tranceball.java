package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.BaseItem;
import io.github.kdy05.physicalFighters.util.EventData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class Tranceball extends Ability implements BaseItem {

    private enum Mode {
        Swap("스왑"), Grab("그랩"), Chase("추격");

        private final String s;

        Mode(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return this.s;
        }
    }

    private Mode mode = Mode.Swap;

    private void switchMode() {
        if (this.mode == Mode.Swap) {
            this.mode = Mode.Grab;
        } else if (this.mode == Mode.Grab) {
            this.mode = Mode.Chase;
        } else if (this.mode == Mode.Chase) {
            this.mode = Mode.Swap;
        }
    }

    public Tranceball(UUID playerUuid) {
        super(AbilitySpec.builder("트랜스볼", Type.PassiveManual, Rank.SS)
                .guide("웅크리고 눈덩이를 우클릭하여 모드를 전환합니다.",
                        "스왑 모드 - 눈덩이를 맞은 적과 위치를 교환합니다.",
                        "그랩 모드 - 눈덩이를 맞은 적을 자신의 위치로 당겨옵니다.",
                        "추격 모드 - 눈덩이를 맞은 적의 위치로 즉시 이동합니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerProjectileHit(new EventData(this, 0));
        registerRightClickEvent();
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        if (CustomData == 0) {
            ProjectileHitEvent event0 = (ProjectileHitEvent) event;
            if (!(event0.getEntity() instanceof Snowball)) return -1;

            Snowball snowball = (Snowball) event0.getEntity();
            if (!(snowball.getShooter() instanceof Player)) return -1;

            Player shooter = (Player) snowball.getShooter();
            if (isOwner(shooter) && snowball.getShooter() != event0.getHitEntity()
                    && event0.getHitEntity() instanceof LivingEntity) {
                return 0;
            }
        }
        else if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            if (isValidItem(Material.SNOWBALL) && isOwner(event1.getPlayer()) && event1.getPlayer().isSneaking()) {
                switchMode();
                sendMessage(ChatColor.AQUA + mode.toString() + " 모드");
                event1.setCancelled(true);
            }
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        if (CustomData == 0) {
            ProjectileHitEvent event0 = (ProjectileHitEvent) event;
            LivingEntity target = (LivingEntity) event0.getHitEntity();
            if (getPlayer() == null) return;
            if (target == null) return;

            Location casterLoc = getPlayer().getLocation();
            Location targetLoc = target.getLocation();

            if (this.mode == Mode.Swap) {
                target.teleport(casterLoc);
                getPlayer().teleport(targetLoc);
            } else if (this.mode == Mode.Grab) {
                target.teleport(casterLoc);
            } else if (this.mode == Mode.Chase) {
                getPlayer().teleport(targetLoc);
            }

            getPlayer().getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));
        }
    }

    @Override
    public void onActivate(@NotNull Player p) {
        this.mode = Mode.Swap;
    }

    @NotNull
    @Override
    public ItemStack[] getBaseItem() {
        return new ItemStack[] {
                new ItemStack(Material.SNOWBALL, 64)
        };
    }

    @NotNull
    @Override
    public String getItemName() {
        return "눈덩이";
    }

}
