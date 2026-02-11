package io.github.kdy05.physicalFighters.game;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.Ability.Type;
import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
import io.github.kdy05.physicalFighters.util.AbilityBook;
import io.github.kdy05.physicalFighters.util.EventData;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public final class EventManager implements Listener {

    private final PhysicalFighters plugin;

    private static final ArrayList<Ability> leftClickHandlers = new ArrayList<>();
    private static final ArrayList<Ability> rightClickHandlers = new ArrayList<>();

    private static final ArrayList<EventData> onEntityTarget = new ArrayList<>();
    private static final ArrayList<EventData> onEntityDamage = new ArrayList<>();
    private static final ArrayList<EventData> onEntityDamageByEntity = new ArrayList<>();
    private static final ArrayList<EventData> onEntityDeath = new ArrayList<>();
    private static final ArrayList<EventData> onPlayerRespawn = new ArrayList<>();
    private static final ArrayList<EventData> onBlockBreakEvent = new ArrayList<>();
    private static final ArrayList<EventData> onSignChangeEvent = new ArrayList<>();
    private static final ArrayList<EventData> onProjectileLaunchEvent = new ArrayList<>();
    private static final ArrayList<EventData> onPlayerDropItem = new ArrayList<>();
    private static final ArrayList<EventData> onPlayerMoveEvent = new ArrayList<>();
    private static final ArrayList<EventData> onProjectileHitEvent = new ArrayList<>();

    // --- 이벤트 등록 API ---

    public static void registerLeftClick(Ability ability) { leftClickHandlers.add(ability); }
    public static void registerRightClick(Ability ability) { rightClickHandlers.add(ability); }

    public static void registerEntityTarget(EventData data) { onEntityTarget.add(data); }
    public static void registerEntityDamage(EventData data) { onEntityDamage.add(data); }
    public static void registerEntityDamageByEntity(EventData data) { onEntityDamageByEntity.add(data); }
    public static void registerEntityDeath(EventData data) { onEntityDeath.add(data); }
    public static void registerPlayerRespawn(EventData data) { onPlayerRespawn.add(data); }
    public static void registerBlockBreak(EventData data) { onBlockBreakEvent.add(data); }
    public static void registerSignChange(EventData data) { onSignChangeEvent.add(data); }
    public static void registerProjectileLaunch(EventData data) { onProjectileLaunchEvent.add(data); }
    public static void registerPlayerDropItem(EventData data) { onPlayerDropItem.add(data); }
    public static void registerPlayerMove(EventData data) { onPlayerMoveEvent.add(data); }
    public static void registerProjectileHit(EventData data) { onProjectileHitEvent.add(data); }

    public EventManager(PhysicalFighters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        // 내구도 무한 모드
        if (plugin.getConfigManager().isInfinityDur()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        // 배고픔 무한 모드
        if (plugin.getConfigManager().isNoFoodMode()) {
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            if (InvincibilityManager.isDamageGuard()) {
                event.setTarget(null);
                event.setCancelled(true);
            }
        }
        executeAbility(onEntityTarget, event);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            // 플레이어 무적
            if (InvincibilityManager.isDamageGuard()) {
                event.setCancelled(true);
                event.getEntity().setFireTicks(0);
            }
        }
        // 주의: EntityDamageByEntityEvent는 EntityDamageEvent의 하위 클래스이므로
        // 양쪽 리스트에 같은 능력을 등록하면 이중 실행됩니다.
        // 양쪽 등록이 필요한 경우, A_Condition에서 조건이 겹치지 않도록 해야 합니다.
        if (event instanceof EntityDamageByEntityEvent) {
            executeAbility(onEntityDamageByEntity, event);
        }
        executeAbility(onEntityDamage, event);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (plugin.getGameManager().getScenario() == GameManager.ScriptStatus.GameStart
                && event instanceof PlayerDeathEvent) {
            PlayerDeathEvent pde = (PlayerDeathEvent) event;
            Player victim = (Player) event.getEntity();
            Player killer = victim.getKiller();

            handleVictim(victim);
            printDeathMessage(pde, killer, victim);
        }
        executeAbility(onEntityDeath, event);
    }

    private void handleVictim(Player victim) {
        for (Ability ability : AbilityRegistry.AbilityList) {
            if (ability.isOwner(victim) && ability.isDeathExempt()) {
                return;
            }
        }
        GameUtils.applyDeathPenalty(victim);
    }

    private void printDeathMessage(PlayerDeathEvent pde, Player killer, Player victim) {
        plugin.getLogger().info(pde.getDeathMessage());
        if (killer != null) {
            // 타살인 경우
            if (plugin.getConfigManager().isKillerOutput()) {
                pde.setDeathMessage(ChatColor.GREEN + killer.getName() + ChatColor.WHITE + "님이 "
                        + ChatColor.RED + victim.getName() + ChatColor.WHITE + "님의 살겠다는 의지를 꺾었습니다.");
            } else {
                pde.setDeathMessage(ChatColor.RED + victim.getName() +
                        ChatColor.WHITE + "님이 누군가에게 살해당했습니다.");
            }
        } else {
            // 자연사인 경우
            pde.setDeathMessage(ChatColor.RED + victim.getName() +
                    ChatColor.WHITE + "님이 대자연에 의해 의지가 꺾였습니다.");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        filterAbilityExecution(event);
        // 능력서 사용
        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItemInMainHand();
        int bookCode = AbilityBook.parseAbilityCode(handItem);
        if (bookCode >= 0) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            GameUtils.assignAbility(player, bookCode, player, plugin.getConfigManager().isAbilityOverLap());
        }
    }

    private void executeAbility(ArrayList<EventData> dataList, Event event) {
        for (EventData data : dataList) {
            Ability ability = data.ability;
            if (ability.getAbilityType() == Type.Active_Continue
                    && ability.getPlayer() != null && ability.getDurationState()) {
                ability.A_Effect(event, data.parameter);
            }
            ability.execute(event, data.parameter);
        }
    }

    private void filterAbilityExecution(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            for (Ability ability : leftClickHandlers) {
                ability.execute(event, 0);
            }
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            for (Ability ability : rightClickHandlers) {
                ability.execute(event, 1);
            }
        }
    }

    // 이하는 이벤트 구독 용도만 수행

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        executeAbility(onPlayerRespawn, event);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        executeAbility(onBlockBreakEvent, event);
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {
        executeAbility(onSignChangeEvent, event);
    }

    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
        executeAbility(onProjectileLaunchEvent, event);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        executeAbility(onPlayerDropItem, event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        executeAbility(onPlayerMoveEvent, event);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        executeAbility(onProjectileHitEvent, event);
    }

}
