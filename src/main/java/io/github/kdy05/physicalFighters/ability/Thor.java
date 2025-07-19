package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.AUC;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class Thor extends Ability {
    private int charge = 0;

    private ItemStack getMjolnir() {
        ItemStack item = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) meta.setDisplayName(ChatColor.YELLOW + "묠니르");
        item.setItemMeta(meta);
        return item;
    }

    public Thor() {
        InitAbility("토르", Type.Active_Immediately, Rank.GOD,
                "금도끼는 묠니르입니다. 묠니르의 기본데미지는 8입니다.",
                "묠니르를 들고 우클릭시 묠니르에 번개의 힘을 내리치며 주변의 플레이어에게 5의 데미지를 주고,",
                "다음 공격에 +3의 데미지를 농축시킵니다. (6번까지 중첩됩니다.)");
        InitAbility(8, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerRightClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            // 데미지 이벤트
            case 0 -> {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                if (isOwner(e.getDamager()) && e.getEntity() instanceof LivingEntity entity) {
                    Player caster = (Player) e.getDamager();
                    ItemStack item = caster.getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) break;
                    if (isValidItem(getMjolnir().getType()) && meta.getDisplayName()
                            .equalsIgnoreCase(ChatColor.YELLOW + "묠니르")) {
                        e.setDamage(8);
                        if (this.charge > 0) {
                            entity.damage(3 * this.charge);
                            entity.getWorld().strikeLightning(entity.getLocation());
                            caster.sendMessage(ChatColor.YELLOW + "묠니르에 농축된 번개의 데미지를 추가로 입혔습니다.");
                            this.charge = 0;
                        }
                        if (meta instanceof Damageable damageable) {
                            damageable.setDamage(0);
                            item.setItemMeta(damageable);
                        }
                    }
                }
            }
            // 우클릭 이벤트
            case 1 -> {
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if (isOwner(Event.getPlayer()) && isValidItem(getMjolnir().getType()) && !PhysicalFighters.DamageGuard) {
                    ItemStack item = Event.getPlayer().getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    if (meta instanceof Damageable damageable) {
                        damageable.setDamage(0);
                        item.setItemMeta(damageable);
                    }
                    return 0;
                }
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            Player caster = Event.getPlayer();
            Location loc = caster.getLocation();
            caster.getWorld().strikeLightningEffect(loc);
            caster.getWorld().strikeLightningEffect(loc);
            AUC.splashDamage(caster, loc, 3, 5);
            if (this.charge < 6) {
                this.charge += 1;
                caster.sendMessage(ChatColor.YELLOW + "묠니르에 농축된 번개 : (" + this.charge + "/6)");
            }
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, getMjolnir());
    }
}
