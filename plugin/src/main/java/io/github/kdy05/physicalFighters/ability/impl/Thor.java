package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.util.BaseItem;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class Thor extends Ability implements BaseItem {

    private int charge = 0;
    private final ItemStack mjolnir = createMjolnir();

    private ItemStack createMjolnir() {
        ItemStack mjolnir = new ItemStack(Material.GOLDEN_HOE);
        ItemMeta meta = mjolnir.getItemMeta();
        assert meta != null;
        meta.setDisplayName("묠니르");
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "토르 전용"));
        mjolnir.setItemMeta(meta);
        return mjolnir;
    }

    private boolean isMjolnir(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || meta.getLore() == null) return false;
        return stack.getType().equals(Material.GOLDEN_HOE) && meta.getDisplayName().equals("묠니르")
                && meta.getLore().get(0).equals(ChatColor.GRAY + "토르 전용");
    }

    public Thor() {
        super(AbilitySpec.builder("토르", Type.Active_Immediately, Rank.GOD)
                .cooldown(8)
                .guide("묠니르(철퇴) 우클릭 시 주변의 플레이어에게 5의 대미지를 주고,",
                        "다음 공격에 +3의 대미지를 농축시킵니다. [최대 6회 중첩]")
                .build());
        EventManager.registerEntityDamageByEntity(new EventData(this, 0));
        registerRightClickEvent();
        registerBaseItemEvents();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (isOwner(event0.getDamager()) && event0.getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) event0.getEntity();
                Player caster = (Player) event0.getDamager();
                ItemStack item = caster.getInventory().getItemInMainHand();
                if (!isMjolnir(item)) return -1;
                if (this.charge > 0) {
                    event0.setDamage(event0.getDamage() + 3 * this.charge);
                    this.charge = 0;
                    entity.getWorld().strikeLightning(entity.getLocation());
                    caster.sendMessage(ChatColor.YELLOW + "묠니르에 농축된 번개의 대미지를 추가로 입혔습니다.");
                }
            }
        } else if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            if (isOwner(event1.getPlayer()) && !InvincibilityManager.isDamageGuard()
                    && isMjolnir(event1.getPlayer().getInventory().getItemInMainHand())) {
                return 1;
            }
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
        if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            Player caster = event1.getPlayer();
            Location loc = caster.getLocation();
            caster.getWorld().strikeLightningEffect(loc);
            AbilityUtils.splashTask(caster, loc, 3, entity -> entity.damage(5));
            if (this.charge < 6) {
                this.charge += 1;
                caster.sendMessage(ChatColor.YELLOW + "묠니르에 농축된 번개 : (" + this.charge + "/6)");
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
        return new ItemStack[] { mjolnir };
    }

    @Override
    public String getItemName() {
        return "묠니르";
    }

}
