package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.BaseItem;
import io.github.kdy05.physicalFighters.utils.EventData;

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

public class Tranceball extends Ability implements BaseItem {

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
        switch (this.mode) {
            case Mode.Swap -> this.mode = Mode.Grab;
            case Mode.Grab -> this.mode = Mode.Chase;
            case Mode.Chase -> this.mode = Mode.Swap;
        }
    }

    public Tranceball() {
        InitAbility("트랜스볼", Type.Passive_Manual, Rank.SS,
                "웅크리고 눈덩이를 우클릭하여 모드를 전환합니다.",
                "스왑 모드 - 눈덩이를 맞은 적과 위치를 교환합니다.",
                "그랩 모드 - 눈덩이를 맞은 적을 자신의 위치로 당겨옵니다.",
                "추격 모드 - 눈덩이를 맞은 적의 위치로 즉시 이동합니다.");
        InitAbility(0, 0, true);
        EventManager.onProjectileHitEvent.add(new EventData(this, 0));
        registerRightClickEvent();
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                ProjectileHitEvent event0 = (ProjectileHitEvent) event;
                if (event0.getEntity() instanceof Snowball s && s.getShooter() instanceof Player p
                        && isOwner(p) && s.getShooter() != event0.getHitEntity()
                        && event0.getHitEntity() instanceof LivingEntity) {
                    return 0;
                }
            }
            case 1 -> {
                PlayerInteractEvent event1 = (PlayerInteractEvent) event;
                if (isValidItem(Material.SNOWBALL) && isOwner(event1.getPlayer()) && event1.getPlayer().isSneaking()) {
                    switchMode();
                    sendMessage(ChatColor.AQUA + mode.toString() + " 모드");
                    event1.setCancelled(true);
                }
            }
            case ITEM_DROP_EVENT -> {
                return handleItemDropCondition(event);
            }
            case ITEM_RESPAWN_EVENT -> {
                return handleItemRespawnCondition(event);
            }
            case ITEM_DEATH_EVENT -> {
                return handleItemDeathCondition(event);
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 0) {
            ProjectileHitEvent event0 = (ProjectileHitEvent) event;
            LivingEntity target = (LivingEntity) event0.getHitEntity();
            if (getPlayer() == null) return;
            if (target == null) return;
            Location casterLoc = getPlayer().getLocation();
            Location targetLoc = target.getLocation();
            switch (this.mode) {
                case Mode.Swap -> {
                    target.teleport(casterLoc);
                    getPlayer().teleport(targetLoc);
                }
                case Mode.Grab -> target.teleport(casterLoc);
                case Mode.Chase -> getPlayer().teleport(targetLoc);
            }
            getPlayer().getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        giveBaseItem(p);
        this.mode = Mode.Swap;
    }

    @Override
    public void A_ResetEvent(Player p) {
        removeBaseItem(p);
    }

    @Override
    public ItemStack[] getBaseItem() {
        return new ItemStack[] {
                new ItemStack(Material.SNOWBALL, 64)
        };
    }

    @Override
    public String getItemName() {
        return "눈덩이";
    }

}
