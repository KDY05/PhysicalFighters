package io.github.kdy05.physicalFighters.core;

import io.github.kdy05.physicalFighters.core.Ability.Type;
import io.github.kdy05.physicalFighters.utils.AbilityInitializer;
import io.github.kdy05.physicalFighters.utils.EventData;
import io.github.kdy05.physicalFighters.PhysicalFighters;

import java.util.ArrayList;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;

public class EventManager implements Listener {

    public static ArrayList<Ability> LeftHandEvent = new ArrayList<>();
    public static ArrayList<Ability> RightHandEvent = new ArrayList<>();

    @EventHandler
    public static void onPlayerItemDamage(PlayerItemDamageEvent event) {
        // 내구도 무한 모드
        if (ConfigManager.InfinityDur) {
            event.setCancelled(true);
        }
        AbilityExcuter(onPlayerItemDamage, event);
    }

    public static ArrayList<EventData> onPlayerItemDamage = new ArrayList<>();

    @EventHandler
    public static void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            // 플레이어 무적
            if (ConfigManager.DamageGuard) {
                event.setCancelled(true);
                event.getEntity().setFireTicks(0);
            }
        }
        if (event instanceof EntityDamageByEntityEvent) {
            AbilityExcuter(onEntityDamageByEntity, event);
        }
        AbilityExcuter(onEntityDamage, event);
    }

    public static ArrayList<EventData> onEntityDamage = new ArrayList<>();
    public static ArrayList<EventData> onEntityDamageByEntity = new ArrayList<>();

    @EventHandler
    public static void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (ConfigManager.NoFoodMode) {
            // 배고픔 무한
            event.setFoodLevel(20);
            return;
        }
        AbilityExcuter(onFoodLevelChange, event);
    }

    public static ArrayList<EventData> onFoodLevelChange = new ArrayList<>();

    @EventHandler
    public static void onEntityDeath(EntityDeathEvent event) {
        if (GameManager.getScenario() == GameManager.ScriptStatus.GameStart && event instanceof PlayerDeathEvent pde) {
            Player victim = (Player) event.getEntity();
            Player killer = victim.getKiller();
            victim.getInventory().clear();

            // 사망 시 처리 (OnKill: 0=아무것도 안함, 1=관전자 모드, 2=킥, 3=밴)
            if (ConfigManager.OnKill > 0 && !AbilityInitializer.phoenix.isOwner(victim)) {
                switch (ConfigManager.OnKill) {
                    case 1 -> {
                        // 죽은 위치 저장
                        org.bukkit.Location deathLocation = victim.getLocation().clone();
                        victim.setGameMode(org.bukkit.GameMode.SPECTATOR);
                        victim.teleport(deathLocation);
                        victim.sendMessage(ChatColor.YELLOW + "관전자 모드로 전환되었습니다.");
                    }
                    case 2 ->
                        victim.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.");
                    case 3 -> {
                        if (!victim.isOp()) {
                            victim.ban("당신은 죽었습니다. 다시 들어오실 수 없습니다.", (Date) null, null, true);
                        } else {
                            victim.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.");
                        }
                    }
                }
            }

            // 데스 메시지
            PhysicalFighters.getPlugin().getLogger().info(pde.getDeathMessage());
            if (killer != null) {
                // 타살인 경우
                if (ConfigManager.KillerOutput) {
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
        AbilityExcuter(onEntityDeath, event);
    }

    public static ArrayList<EventData> onEntityDeath = new ArrayList<>();

    @EventHandler
    public static void onPlayerInteract(PlayerInteractEvent event) {
        AbilityEventFilter(event);

        // 능력서 사용
        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() == Material.ENCHANTED_BOOK && handItem.getItemMeta() != null
                && handItem.getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "[능력서]")) {
            String name = handItem.getItemMeta().getDisplayName();
            int n = Integer.parseInt(name.split("f")[1].split("\\.")[0]);
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            PhysicalFighters.getPlugin().getGameCommand().assignAbility(player, n, player);
        }

        AbilityExcuter(onPlayerInteract, event);
    }

    public static ArrayList<EventData> onPlayerInteract = new ArrayList<>();

    private static void AbilityExcuter(ArrayList<EventData> ED, Event event) {
        for (EventData eventData : ED) {
            Ability ability = eventData.ability;
            if (ability.getAbilityType() == Type.Active_Continue
                    && ability.getPlayer() != null && ability.getDurationState()) {
                ability.A_Effect(event, eventData.parameter);
            }
            ability.execute(event, eventData.parameter);
        }
    }

    private static void AbilityEventFilter(PlayerInteractEvent event) {
        int i = 0;
        switch (event.getAction()) {
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                while (i < LeftHandEvent.size() && !LeftHandEvent.isEmpty()) {
                    LeftHandEvent.get(i).execute(event, 0);
                    ++i;
                }
            }
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                while (i < RightHandEvent.size() && !RightHandEvent.isEmpty()) {
                    RightHandEvent.get(i).execute(event, 1);
                    ++i;
                }
            }
        }
    }

    // 이하는 이벤트 구독 용도만 수행

    @EventHandler
    public static void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player)
            AbilityExcuter(onPlayerPickupItem, event);
    }

    public static ArrayList<EventData> onPlayerPickupItem = new ArrayList<>();

    @EventHandler
    public static void onDisable(PluginDisableEvent event) {
        AbilityExcuter(onPluginDisable, event);
    }

    public static ArrayList<EventData> onPluginDisable = new ArrayList<>();

    @EventHandler
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        AbilityExcuter(onPlayerRespawn, event);
    }

    public static ArrayList<EventData> onPlayerRespawn = new ArrayList<>();

    @EventHandler
    public static void onEntityTarget(EntityTargetEvent event) {
        AbilityExcuter(onEntityTarget, event);
    }

    public static ArrayList<EventData> onEntityTarget = new ArrayList<>();

    @EventHandler
    public static void onEntityRegainHealth(EntityRegainHealthEvent event) {
        AbilityExcuter(onEntityRegainHealth, event);
    }

    public static ArrayList<EventData> onEntityRegainHealth = new ArrayList<>();

    @EventHandler
    public static void onBlockPlaceEvent(BlockPlaceEvent event) {
        AbilityExcuter(onBlockPlaceEvent, event);
    }

    public static ArrayList<EventData> onBlockPlaceEvent = new ArrayList<>();

    @EventHandler
    public static void onBlockBreakEvent(BlockBreakEvent event) {
        AbilityExcuter(onBlockBreakEvent, event);
    }

    public static ArrayList<EventData> onBlockBreakEvent = new ArrayList<>();

    @EventHandler
    public static void onSignChangeEvent(SignChangeEvent event) {
        AbilityExcuter(onSignChangeEvent, event);
    }

    public static ArrayList<EventData> onSignChangeEvent = new ArrayList<>();

    @EventHandler
    public static void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        AbilityExcuter(onPlayerToggleSneakEvent, event);
    }

    public static ArrayList<EventData> onPlayerToggleSneakEvent = new ArrayList<>();

    @EventHandler
    public static void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
        AbilityExcuter(onProjectileLaunchEvent, event);
    }

    public static ArrayList<EventData> onProjectileLaunchEvent = new ArrayList<>();

    @EventHandler
    public static void onPlayerDropItem(PlayerDropItemEvent event) {
        AbilityExcuter(onPlayerDropItem, event);
    }

    public static ArrayList<EventData> onPlayerDropItem = new ArrayList<>();

    @EventHandler
    public static void onPlayerMove(PlayerMoveEvent event) {
        AbilityExcuter(onPlayerMoveEvent, event);
    }

    public static ArrayList<EventData> onPlayerMoveEvent = new ArrayList<>();

    @EventHandler
    public static void onProjectileHit(ProjectileHitEvent event) {
        AbilityExcuter(onProjectileHitEvent, event);
    }

    public static ArrayList<EventData> onProjectileHitEvent = new ArrayList<>();

}
