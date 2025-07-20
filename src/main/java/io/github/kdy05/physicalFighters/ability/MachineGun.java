package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.ConfigManager;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MachineGun extends Ability {
    // 능력 설정 필드
    private static final int MAGAZINE_SIZE = 30;
    private static final int RELOAD_TIME_TICKS = 60;
    private static final double CRITICAL_CHANCE = 0.2;
    private static final int CRITICAL_DAMAGE = 2;
    private static final int BULLET_DAMAGE = 2;
    private static final Material WEAPON_ITEM = Material.GOLD_INGOT;
    private static final Material AMMO_ITEM = Material.IRON_INGOT;

    private int currentBullets = 0;
    private boolean isReloading = false;

    private static final int EVENT_RIGHT_CLICK = 1;
    private static final int EVENT_DAMAGE = 2;
    private static final int EVENT_PROJECTILE_HIT = 3;
    private static final int ACTION_SHOOT = 10;
    private static final int ACTION_RELOAD = 20;

    public MachineGun() {
        InitAbility("기관총", Type.Active_Immediately, Rank.S,
                Usage.GoldRight + "화살을 연사합니다. 철괴를 탄창으로 사용하며 한 탄창은 30발입니다.",
                "크리티컬 - 20% 확률로 화살이 고정 데미지를 입힙니다.");
        InitAbility(0, 0, true, ShowText.Custom_Text);
        registerRightClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, EVENT_DAMAGE));
        EventManager.onProjectileHitEvent.add(new EventData(this, EVENT_PROJECTILE_HIT));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        return switch (CustomData) {
            case EVENT_RIGHT_CLICK -> handleRightClickCondition(event);
            case EVENT_DAMAGE -> handleDamageCondition(event);
            case EVENT_PROJECTILE_HIT -> handleProjectileHitCondition(event);
            default -> -1;
        };
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case EVENT_DAMAGE -> handleDamageEffect(event);
            case ACTION_SHOOT -> handleShootEffect(event);
            case ACTION_RELOAD -> handleReloadEffect(event);
        }
    }

    private int handleRightClickCondition(Event event) {
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (!ConfigManager.DamageGuard && isOwner(e.getPlayer()) && isValidItem(WEAPON_ITEM)) {
            Player player = e.getPlayer();
            if (currentBullets > 0)
                return ACTION_SHOOT;
            if (player.getInventory().contains(AMMO_ITEM))
                return ACTION_RELOAD;
            player.sendMessage(ChatColor.RED + "탄창이 없습니다.");
            if (isReloading)
                player.sendMessage(ChatColor.RED + "장전중입니다.");
        }
        return -1;
    }

    private int handleDamageCondition(Event event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        if (e.getDamager() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player shooter && isOwner(shooter)) {
                // 자기 자신을 맞추는 경우 방지
                if (e.getEntity() instanceof Player && arrow.getShooter() == e.getEntity()) {
                    return -1;
                }
                return EVENT_DAMAGE;
            }
        }
        return -1;
    }

    private int handleProjectileHitCondition(Event event) {
        ProjectileHitEvent e = (ProjectileHitEvent) event;
        if (e.getEntity() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player shooter && isOwner(shooter)) {
                arrow.remove();
            }
        }
        return -1;
    }

    private void handleDamageEffect(Event event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        e.setDamage(BULLET_DAMAGE);
        if (e.getEntity() instanceof LivingEntity target && Math.random() <= CRITICAL_CHANCE) {
            target.getWorld().createExplosion(target.getLocation(), 0.0F);
            AbilityUtils.piercingDamage(target, CRITICAL_DAMAGE);
            sendMessage(ChatColor.GREEN + "크리티컬");
        }
    }

    private void handleShootEffect(Event event) {
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        Player player = e.getPlayer();

        if (currentBullets % 5 == 0) {
            player.sendMessage(ChatColor.AQUA + "남은 탄환 : " +
                    ChatColor.WHITE + currentBullets + "개");
        }

        currentBullets--;
        player.launchProjectile(Arrow.class);
    }

    private void handleReloadEffect(Event event) {
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        Player player = e.getPlayer();
        if (isReloading) return;
        isReloading = true;
        player.sendMessage(ChatColor.AQUA + "장전 중... [3초 소요]");
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.getInventory().contains(AMMO_ITEM, 1)){
                player.getInventory().removeItem(new ItemStack(AMMO_ITEM, 1));
                currentBullets = MAGAZINE_SIZE;
                player.sendMessage(ChatColor.GREEN + "재장전 완료");
            } else {
                player.sendMessage(ChatColor.RED + "재장전 실패 - 탄창이 없습니다.");
            }
            isReloading = false;
        }, RELOAD_TIME_TICKS);
    }
}