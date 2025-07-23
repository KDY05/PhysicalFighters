package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.BaseItem;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Kaiji extends Ability implements BaseItem {
    public Kaiji() {
        InitAbility("카이지", Type.Passive_Manual, Rank.S,
                "다이아몬드로 상대 타격 시 30% 확률로 상대를 즉사시키고, 70% 확률로 자신이 사망합니다.");
        InitAbility(20, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
                if (!ConfigManager.DamageGuard && isOwner(event0.getDamager())
                        && isValidItem(Material.DIAMOND) && event0.getEntity() instanceof Player)
                    return 0;
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
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            Player player = (Player) event0.getEntity();
            if (Math.random() <= 0.3D) {
                player.damage(5000);
                Bukkit.broadcastMessage(String.format(ChatColor.RED +
                        "%s님이  카'의지'에 능력에 의지가 꺾였습니다.", player.getName()));
            } else {
                if (getPlayer() == null) return;
                getPlayer().damage(5000);
                Bukkit.broadcastMessage(String.format(ChatColor.RED +
                        "%s님이 도박하다가 손목이 날라갔습니다.", getPlayer().getName()));
            }
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        giveBaseItem(p);
    }

    @Override
    public void A_ResetEvent(Player p) {
        removeBaseItem(p);
    }

    @Override
    public ItemStack[] getBaseItem() {
        return new ItemStack[] {
                new ItemStack(Material.DIAMOND, 1)
        };
    }

    @Override
    public String getItemName() {
        return "다이아몬드";
    }

}
