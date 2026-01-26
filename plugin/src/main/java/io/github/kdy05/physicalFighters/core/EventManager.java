package io.github.kdy05.physicalFighters.core;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability.Type;
import io.github.kdy05.physicalFighters.module.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.AbilityInitializer;
import io.github.kdy05.physicalFighters.util.AbilityUtils;
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
    private final ConfigManager configManager;

    public static ArrayList<Ability> LeftHandEvent = new ArrayList<>();
    public static ArrayList<Ability> RightHandEvent = new ArrayList<>();

    public static ArrayList<EventData> onPlayerItemDamage = new ArrayList<>();
    public static ArrayList<EventData> onFoodLevelChange = new ArrayList<>();
    public static ArrayList<EventData> onEntityTarget = new ArrayList<>();
    public static ArrayList<EventData> onEntityDamage = new ArrayList<>();
    public static ArrayList<EventData> onEntityDamageByEntity = new ArrayList<>();
    public static ArrayList<EventData> onEntityDeath = new ArrayList<>();
    public static ArrayList<EventData> onPlayerInteract = new ArrayList<>();
    public static ArrayList<EventData> onPlayerRespawn = new ArrayList<>();
    public static ArrayList<EventData> onBlockBreakEvent = new ArrayList<>();
    public static ArrayList<EventData> onSignChangeEvent = new ArrayList<>();
    public static ArrayList<EventData> onProjectileLaunchEvent = new ArrayList<>();
    public static ArrayList<EventData> onPlayerDropItem = new ArrayList<>();
    public static ArrayList<EventData> onPlayerMoveEvent = new ArrayList<>();
    public static ArrayList<EventData> onProjectileHitEvent = new ArrayList<>();

    public EventManager(PhysicalFighters plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        // 내구도 무한 모드
        if (configManager.isInfinityDur()) {
            event.setCancelled(true);
        }
        executeAbility(onPlayerItemDamage, event);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        // 배고픔 무한 모드
        if (configManager.isNoFoodMode()) {
            event.setFoodLevel(20);
            return;
        }
        executeAbility(onFoodLevelChange, event);
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
        if (event instanceof EntityDamageByEntityEvent) {
            executeAbility(onEntityDamageByEntity, event);
        }
        executeAbility(onEntityDamage, event);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (GameManager.getScenario() == GameManager.ScriptStatus.GameStart && event instanceof PlayerDeathEvent) {
            PlayerDeathEvent pde = (PlayerDeathEvent) event;
            Player victim = (Player) event.getEntity();
            Player killer = victim.getKiller();

            handleVictim(victim);
            printDeathMessage(pde, killer, victim);
        }
        executeAbility(onEntityDeath, event);
    }

    private void handleVictim(Player victim) {
        // 사망 시 처리 (OnKill: 0=아무것도 안함, 1=관전자 모드, 2=킥, 3=밴)
        int onKill = configManager.getOnKill();
        if (onKill <= 0 || AbilityInitializer.phoenix.isOwner(victim)) {
            return;
        }
        if (onKill == 1) {
            // 죽은 위치 저장
            Location deathLocation = victim.getLocation().clone();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                victim.setGameMode(GameMode.SPECTATOR);
                victim.spigot().respawn();
                victim.teleport(deathLocation);
                victim.sendTitle(ChatColor.RED + "사망하였습니다!",
                        ChatColor.YELLOW + "관전자 모드로 전환합니다.", 10, 100 ,10);
            }, 1L);
        } else if (onKill == 2) {
            victim.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.");
        } else if (onKill == 3) {
            if (!victim.isOp()) {
                Bukkit.getBanList(BanList.Type.NAME).addBan(victim.getName(),
                        "당신은 죽었습니다. 다시 들어오실 수 없습니다.", null, null);
                victim.kickPlayer("당신은 죽었습니다. 다시 들어오실 수 없습니다.");
            } else {
                victim.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.");
            }
        }
    }

    private void printDeathMessage(PlayerDeathEvent pde, Player killer, Player victim) {
        plugin.getLogger().info(pde.getDeathMessage());
        if (killer != null) {
            // 타살인 경우
            if (configManager.isKillerOutput()) {
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
        if (handItem.getType() == Material.ENCHANTED_BOOK && handItem.getItemMeta() != null
                && handItem.getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "[능력서]")) {
            String name = handItem.getItemMeta().getDisplayName();
            int n = Integer.parseInt(name.split("f")[1].split("\\.")[0]);
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            AbilityUtils.assignAbility(player, n, player, configManager.isAbilityOverLap());
        }

        executeAbility(onPlayerInteract, event);
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
            for (Ability ability : LeftHandEvent) {
                ability.execute(event, 0);
            }
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            for (Ability ability : RightHandEvent) {
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
