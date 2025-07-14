package io.github.kdy05.physicalFighters.abilities;

import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonArrow extends Ability {
    public PoisonArrow() {
        InitAbility("독화살", Type.Passive_Manual, Rank.B,
                "죽거나 게임 시작시 화살 한묶음이 고정적으로 주어집니다.",
                "화살에 맞은 적은 6초간 독에걸립니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        EventManager.onPlayerRespawn.add(new EventData(this, 1));
        EventManager.onEntityDeath.add(new EventData(this, 2));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                if (Event0.getDamager() instanceof Arrow a && isOwner((Player) a.getShooter())
                    && a.getShooter() != Event0.getEntity()) {
                        return 0;
                }
                break;
            case 1:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                if (isOwner(Event2.getPlayer())) return 1;
                break;
            case 2:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                if (isOwner(Event3.getEntity())) return 2;
                break;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                LivingEntity target = (LivingEntity) Event0.getEntity();
                target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 60, 0));
                target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 0));
                break;
            case 1:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                Event2.getPlayer().sendMessage(
                        ChatColor.GREEN + "이전에 소유했던 화살은 모두 소멸하며 다시 지급됩니다.");
                PlayerInventory inv = Event2.getPlayer().getInventory();
                inv.remove(Material.ARROW);
                inv.setItem(8, new ItemStack(Material.ARROW, 64));
                inv.setItem(7, new ItemStack(Material.BOW, 1));
                break;
            case 2:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                List<ItemStack> itemlist = Event3.getDrops();
                itemlist.removeIf(item -> item.getType() == Material.ARROW);
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.ARROW, 64));
        p.getInventory().setItem(7, new ItemStack(Material.BOW, 1));
    }

    @Override
    public void A_ResetEvent(Player p) {
        p.getInventory().removeItem(new ItemStack(Material.ARROW, 64));
    }
}
