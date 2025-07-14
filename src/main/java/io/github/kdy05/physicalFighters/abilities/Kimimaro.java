package io.github.kdy05.physicalFighters.abilities;

import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Kimimaro extends Ability {
    public Kimimaro() {
        InitAbility("키미마로", Type.Passive_Manual, Rank.SS,
                "뼈다귀로 상대를 공격할 시에 강한 데미지를 주고,",
                "40% 확률로 상대에게 10초간 독 효과를 겁니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        EventManager.onPlayerDropItem.add(new EventData(this, 1));
        EventManager.onPlayerRespawn.add(new EventData(this, 2));
        EventManager.onEntityDeath.add(new EventData(this, 3));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
                if (isOwner(Event.getDamager()) && isValidItem(Material.BONE)
                        && Event.getEntity() instanceof LivingEntity)
                    return 0;
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                if (isOwner(Event1.getPlayer()) &&
                        Event1.getItemDrop().getItemStack().getType() == Material.BONE) {
                    PlayerInventory inv = Event1.getPlayer().getInventory();
                    if (!inv.contains(Material.BONE, 1)) return 1;
                }
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                if (isOwner(Event2.getPlayer())) return 2;
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                if (isOwner(Event3.getEntity())) return 3;
                break;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
                Event.setDamage(Event.getDamage() * 8);
                LivingEntity entity = (LivingEntity) Event.getEntity();
                if (Math.random() < 0.4D)
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 0));
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                Event1.getPlayer().sendMessage(ChatColor.RED + "뼈는 버릴 수 없습니다.");
                Event1.setCancelled(true);
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                PlayerInventory inv = Event2.getPlayer().getInventory();
                inv.setItem(8, new ItemStack(Material.BONE, 1));
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                List<ItemStack> itemlist = Event3.getDrops();
                itemlist.removeIf(item -> item.getType() == Material.BONE);
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.BONE, 1));
    }

    @Override
    public void A_ResetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.BONE, 1));
    }
}
