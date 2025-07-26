package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

public class Issac
        extends Ability {
    private int bullet = 0;
    private Material item;

    public Issac() {
        InitAbility("아이작", Type.Active_Immediately, Rank.S, new String[]{
                "금괴를 들고 우클릭시 눈물을 발사합니다.",
                "철괴 하나당 5개의 눈물을 발사할 수 있습니다.",
                "눈물의 대미지는 3입니다."});
        InitAbility(0, 0, true, ShowText.Custom_Text);
        registerRightClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 3));
        EventManager.onProjectileHitEvent.add(new EventData(this, 5));
        this.item = Material.GOLD_INGOT;
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 1:
                PlayerInteractEvent Event0 = (PlayerInteractEvent) event;
                if ((!ConfigManager.DamageGuard) &&
                        (isOwner(Event0.getPlayer())) && (isValidItem(this.item))) {
                    if (this.bullet != 0) {
                        return 10;
                    }
                    if (getPlayer().getInventory().contains(Material.IRON_INGOT)) {
                        return 20;
                    }
                    getPlayer().sendMessage(ChatColor.RED + "철괴가 없습니다.");
                }
                break;
            case 3:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                if ((Event1.getDamager() instanceof Snowball)) {
                    Snowball a = (Snowball) Event1.getDamager();
                    if (isOwner((Entity) a.getShooter())) {
                        if (((Event1.getEntity() instanceof Player)) &&
                                ((Player) a.getShooter() ==
                                        (Player) Event1
                                                .getEntity()))
                            return -1;
                        return 3;
                    }
                }
                break;
            case 5:
                ProjectileHitEvent Event2 = (ProjectileHitEvent) event;
                if ((Event2.getEntity() instanceof Snowball)) {
                    Snowball a = (Snowball) Event2.getEntity();
                    if (((a.getShooter() instanceof Player)) &&
                            (isOwner((Player) a.getShooter()))) {
                        a.remove();
                        return -2;
                    }
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 3:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                Event0.setDamage(6);
                break;
            case 10:
                PlayerInteractEvent Event1 = (PlayerInteractEvent) event;
                this.bullet -= 1;
                Event1.getPlayer().launchProjectile(Snowball.class);
                break;
            case 20:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                PlayerInventory inv = getPlayer().getInventory();
                int sell = inv.first(Material.IRON_INGOT);
                if (inv.getItem(sell).getAmount() == 1) {
                    inv.clear(sell);
                } else {
                    inv.getItem(sell).setAmount(inv.getItem(sell).getAmount() - 1);
                }
                getPlayer().updateInventory();
                this.bullet = 5;
                getPlayer().sendMessage(ChatColor.GREEN + "눈물을 충전했습니다.");
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Issac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */