package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.BaseItem;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class Killtolevelup extends Ability implements BaseItem {

    private int dama = 4;

    public Killtolevelup(UUID playerUuid) {
        super(AbilitySpec.builder("폭주", Type.Passive_Manual, Rank.SS)
                .guide("깃털의 처음 대미지는 4입니다.",
                        "깃털로 적을 처치할 때마다 대미지가 2만큼 늘어납니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDamageByEntity(new EventData(this, 0));
        EventManager.registerEntityDeath(new EventData(this, 1));
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (isOwner(event0.getDamager()) && isValidItem(Material.FEATHER))
                return 0;
        } else if (CustomData == 1) {
            EntityDeathEvent event1 = (EntityDeathEvent) event;
            if (event1.getEntity().getKiller() != null && isOwner(event1.getEntity().getKiller())
                    && isValidItem(Material.FEATHER) && event1.getEntity() instanceof Player)
                return 1;
        } else if (CustomData == ITEM_DROP_EVENT) {
            return handleItemDropCondition(event);
        } else if (CustomData == ITEM_RESPAWN_EVENT) {
            return handleItemRespawnCondition(event);
        } else if (CustomData == ITEM_DEATH_EVENT) {
            return handleItemDeathCondition(event);
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            event0.setDamage(event0.getDamage() * this.dama);
        } else if (CustomData == 1) {
            EntityDeathEvent event1 = (EntityDeathEvent) event;
            Player player = event1.getEntity().getKiller();
            if (player == null) return;
            this.dama += 2;
            Bukkit.broadcastMessage(String.format(ChatColor.RED + "%s님을 죽이고 %s님이 폭주했습니다.",
                    event1.getEntity().getName(), player.getName()));
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
                new ItemStack(Material.FEATHER, 1)
        };
    }

    @Override
    public String getItemName() {
        return "깃털";
    }

}
