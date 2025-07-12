package physicalFighters.core;

import physicalFighters.core.Ability.Type;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;
import physicalFighters.scripts.MainScripter;
import physicalFighters.scripts.MainScripter.ScriptStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

public class EventManager implements Listener {

    public static ArrayList<Ability> LeftHandEvent = new ArrayList<>();
    public static ArrayList<Ability> RightHandEvent = new ArrayList<>();
    public static boolean DamageGuard = false;

    @EventHandler
    public static void onEntityDamage(EntityDamageEvent event) {
        if ((event.getEntity() instanceof Player p)) {
            // 플레이어 무적
            if (DamageGuard) {
                event.setCancelled(true);
                event.getEntity().setFireTicks(0);
            }
            // 갑옷 내구도 무한
            if (PhysicalFighters.InfinityDur) {
                PlayerInventory inv = p.getInventory();
                for (ItemStack armor : inv.getArmorContents()) {
                    if (armor != null && armor.getItemMeta() instanceof Damageable damageable) {
                        damageable.setDamage(0);
                        armor.setItemMeta(damageable);
                    }
                }
            }
        }
        if (event instanceof EntityDamageByEntityEvent byEntityEvent) {
            // 몬스터애게 받는 대미지 50% 감소
            if (PhysicalFighters.HalfMonsterDamage && !(byEntityEvent.getDamager() instanceof Player))
                byEntityEvent.setDamage(byEntityEvent.getDamage() / 2);
            AbilityExcuter(onEntityDamageByEntity, event);
        }
        AbilityExcuter(onEntityDamage, event);
    }
    public static ArrayList<EventData> onEntityDamage = new ArrayList<>();
    public static ArrayList<EventData> onEntityDamageByEntity = new ArrayList<>();


    @EventHandler
    public static void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (PhysicalFighters.NoFoodMode) {
            // 배고픔 무한
            event.setFoodLevel(20);
            return;
        }
        AbilityExcuter(onFoodLevelChange, event);
    }
    public static ArrayList<EventData> onFoodLevelChange = new ArrayList<>();


    @EventHandler
    public static void onEntityDeath(EntityDeathEvent event) {
        if (MainScripter.Scenario == ScriptStatus.GameStart && event instanceof PlayerDeathEvent pde) {
            Player victim = (Player) event.getEntity();
            Player killer = victim.getKiller();
            victim.getInventory().clear();

            // 킥, 밴 처리
            if (PhysicalFighters.AutoKick && !AbilityList.phoenix.isOwner(victim)) {
                if (PhysicalFighters.AutoBan && !victim.isOp()) {
                    victim.ban("당신은 죽었습니다. 다시 들어오실 수 없습니다.", (Date) null, null, true);
                } else {
                    victim.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.");
                }
            }

            // 데스 메시지
            PhysicalFighters.getPlugin().getLogger().info(pde.getDeathMessage());
            if (killer != null) {
                // 타살인 경우
                if (PhysicalFighters.KillerOutput) {
                    pde.setDeathMessage(ChatColor.GREEN + killer.getName() + ChatColor.WHITE + "님이 "
                            + ChatColor.RED + victim.getName() + ChatColor.WHITE + "님의 살겠다는 의지를 꺾었습니다.");
                } else {
                    pde.setDeathMessage(ChatColor.RED + victim.getName() +
                            ChatColor.WHITE + "님이 누군가에게 살해당했습니다.");
                }
            } else {
                // 자연사인 경우
                pde.setDeathMessage(ChatColor.RED + victim.getName() +
                        ChatColor.WHITE + "님이 대자연에 의해 의지를 꺾였습니다.");
            }

        }
        AbilityExcuter(onEntityDeath, event);
    }
    public static ArrayList<EventData> onEntityDeath = new ArrayList<>();


    @EventHandler
    public static void onPlayerInteract(PlayerInteractEvent event) {
        _AbilityEventFilter(event);
        Player p = event.getPlayer();
        ItemStack handItem = p.getInventory().getItemInMainHand();
        // 도구 내구도 무한
        if (PhysicalFighters.InfinityDur) {
            if (handItem.getItemMeta() instanceof Damageable damageable) {
                damageable.setDamage(0);
                handItem.setItemMeta(damageable);
            }
        }
        // 능력서 사용
        if (handItem.getType() == Material.ENCHANTED_BOOK) {
            if (handItem.hasItemMeta() && Objects.requireNonNull(
                    handItem.getItemMeta()).getDisplayName().startsWith(ChatColor.GOLD + "[능력서]")) {
                String name = handItem.getItemMeta().getDisplayName();
                int n = Integer.parseInt(name.split("f")[1].split("\\.")[0]);
                usebook(p, n);
                p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }
        }

        AbilityExcuter(onPlayerInteract, event);
    }
    public static ArrayList<EventData> onPlayerInteract = new ArrayList<>();

    public static void usebook(Player p, int abicode) {
        if (p == null || abicode < 0 || abicode >= AbilityList.AbilityList.size())
            return;
        Ability ability = AbilityList.AbilityList.get(abicode);
        if (PhysicalFighters.AbilityOverLap) {
            if (ability.getAbilityType() == Ability.Type.Active_Continue ||
                ability.getAbilityType() == Ability.Type.Active_Immediately) {
                for (Ability ab : AbilityList.AbilityList) {
                    if (ab.isOwner(p) && (ab.getAbilityType() == Ability.Type.Active_Continue ||
                                            ab.getAbilityType() == Ability.Type.Active_Immediately)) {
                        ab.setPlayer(null, true);
                    }
                }
            }
        } else {
            for (Ability ab : AbilityList.AbilityList) {
                if (ab.isOwner(p)) {
                    ab.setPlayer(null, true);
                }
            }
        }
        ability.setPlayer(p, true);
        ability.setRunAbility(true);
        Bukkit.broadcastMessage(String.format(ChatColor.GOLD +
                "%s님이 능력을 부여받았습니다.", p.getName()));
    }


    private static void AbilityExcuter(ArrayList<EventData> ED, Event event) {
        for (EventData ed : ED) {
            if (ed.ab.getAbilityType() == Type.Active_Continue) {
                if (ed.ab.AbilityDuratinEffect(event, ed.parameter))
                    return;
            } else {
                if (ed.ab.AbilityExcute(event, ed.parameter))
                    return;
            }
        }
    }


    private static void _AbilityEventFilter(PlayerInteractEvent event) {
        int i = 0;
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) ||
                event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            while (i < LeftHandEvent.size() && !LeftHandEvent.isEmpty()) {
                if (LeftHandEvent.get(i).AbilityExcute(event, 0)) {
                    return;
                }
                ++i;
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_AIR) ||
                event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            while (i < RightHandEvent.size() && !RightHandEvent.isEmpty()) {
                if (RightHandEvent.get(i).AbilityExcute(event, 1)) {
                    return;
                }
                ++i;
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
