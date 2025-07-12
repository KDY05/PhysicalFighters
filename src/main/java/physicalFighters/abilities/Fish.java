package physicalFighters.abilities;

import physicalFighters.core.EventManager;
import physicalFighters.core.Ability;
import physicalFighters.utils.EventData;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Fish extends Ability {
    public Fish() {
        InitAbility("강태공", Type.Passive_Manual, Rank.A,
                "낚싯대로 상대를 타격 시 강한 데미지를 주고, 낮은 확률로 전용 물고기를 얻습니다.",
                "물고기를 들고 상대를 타격 시에, 더욱 강한 데미지를 줍니다.",
                "(!) 공격 속도에 영향을 받지 않습니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        EventManager.onPlayerDropItem.add(new EventData(this, 1));
        EventManager.onPlayerRespawn.add(new EventData(this, 2));
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
                if (isOwner(Event.getDamager()) && isValidItem(Material.FISHING_ROD))
                    return 0;
                if (isOwner(Event.getDamager()) && isValidItem(Material.COD)) {
                    return 3;
                }
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                if (isOwner(Event1.getPlayer()) &&
                        Event1.getItemDrop().getItemStack().getType() == Material.FISHING_ROD) {
                    PlayerInventory inv = Event1.getPlayer().getInventory();
                    if (!inv.contains(Material.FISHING_ROD, 1)) {
                        return 1;
                    }
                }
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                if (isOwner(Event2.getPlayer()))
                    return 2;
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                Event0.setDamage(7.0);
                if (Math.random() <= 0.03) {
                    Event0.getEntity().getWorld().dropItemNaturally(Event0.getEntity().getLocation(),
                            new ItemStack(Material.COD));
                }
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                Event1.getPlayer().sendMessage(org.bukkit.ChatColor.RED + "낚싯대는 버릴 수 없습니다.");
                Event1.setCancelled(true);
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                Event2.getPlayer().sendMessage(org.bukkit.ChatColor.GREEN + "낚싯대가 지급됩니다.");
                Event2.getPlayer().getInventory().addItem(new ItemStack(Material.FISHING_ROD, 1));
                break;
            case 3:
                EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
                Event.setDamage(10.5);
        }
    }

    public void A_SetEvent(Player p) {
        p.getInventory().addItem(new ItemStack(Material.FISHING_ROD, 1));
    }

    public void A_ResetEvent(Player p) {
        p.getInventory().addItem(new ItemStack(Material.FISHING_ROD, 1));
    }
}
