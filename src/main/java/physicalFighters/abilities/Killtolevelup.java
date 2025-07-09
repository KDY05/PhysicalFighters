package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import java.util.List;

import org.bukkit.Bukkit;
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

public class Killtolevelup extends AbilityBase {
    int dama = 5;

    public Killtolevelup() {
        InitAbility("폭주", Type.Passive_Manual, Rank.SS, new String[]{
                "깃털의 처음 데미지는 5입니다.",
                "깃털로 1킬을 할때마다 데미지가 2씩 늘어납니다."});
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        EventManager.onPlayerDropItem.add(new EventData(this, 1));
        EventManager.onPlayerRespawn.add(new EventData(this, 2));
        EventManager.onEntityDeath.add(new EventData(this, 3));
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
                if ((isOwner(Event.getDamager())) && (isValidItem(Material.FEATHER)))
                    return 0;
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                if ((isOwner(Event1.getPlayer())) &&
                        (Event1.getItemDrop().getItemStack().getType() == Material.FEATHER)) {
                    PlayerInventory inv = Event1.getPlayer().getInventory();
                    if (!inv.contains(Material.FEATHER, 1)) {
                        return 1;
                    }
                }
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                if (isOwner(Event2.getPlayer()))
                    return 2;
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                if (isOwner(Event3.getEntity()))
                    return 3;
                if ((Event3.getEntity().getKiller() != null) &&
                        (isOwner(Event3.getEntity().getKiller())) && (isValidItem(Material.FEATHER)) && ((Event3.getEntity() instanceof Player)))
                    return 4;
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
                Event.setDamage(Event.getDamage() + this.dama);
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                Event1.getPlayer().sendMessage(ChatColor.RED + "깃털은 버릴 수 없습니다.");
                Event1.setCancelled(true);
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                PlayerInventory inv = Event2.getPlayer().getInventory();
                inv.setItem(8, new ItemStack(Material.FEATHER, 1));
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                List<ItemStack> itemlist = Event3.getDrops();
                for (int l = 0; l < itemlist.size(); l++) {
                    if (((ItemStack) itemlist.get(l)).getType() == Material.FEATHER) {
                        itemlist.remove(l);
                    }
                }
                break;
            case 4:
                EntityDeathEvent Event4 = (EntityDeathEvent) event;
//if (this.dama < 12)
//{
                this.dama += 2;
                Bukkit.broadcastMessage(String.format(ChatColor.RED + "%s님을 죽이고 %s님이  폭주했습니다.", new Object[]{
                        ((Player) Event4.getEntity()).getName(), Event4.getEntity().getKiller().getName()}));
                Event4.getEntity().getKiller().sendMessage(ChatColor.RED + "붉은 피를보니... 내가 더 강해진 것 같군.. 큭..");
//}
//else
//{
//this.dama = 12;
//Event4.getEntity().getKiller().sendMessage(ChatColor.RED + "당신은 이미 충분히 성장했습니다.");
//}
                break;
        }
    }

    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.FEATHER, 1));
    }

    public void A_ResetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.FEATHER, 1));
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Killtolevelup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */