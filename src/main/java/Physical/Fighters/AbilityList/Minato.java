package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.AbilityBase.Rank;
import Physical.Fighters.MainModule.AbilityBase.Type;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.EventData;
import Physical.Fighters.PhysicalFighters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Minato extends AbilityBase {
    public Minato() {
        if (PhysicalFighters.SRankUsed) {
            InitAbility("미나토", Type.Passive_Manual, Rank.S, new String[]{
                    "눈덩이를 던져, 맞은 적에게 텔레포트합니다."});
            InitAbility(1, 0, true);
            EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
            EventManager.onPlayerDropItem.add(new EventData(this, 1));
            EventManager.onPlayerRespawn.add(new EventData(this, 2));
            EventManager.onEntityDeath.add(new EventData(this, 3));
        }
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                if ((Event0.getDamager() instanceof Snowball)) {
                    Snowball a = (Snowball) Event0.getDamager();
                    if (PlayerCheck((Player) a.getShooter())) {
                        if (((Event0.getEntity() instanceof Player)) &&
                                ((Player) a.getShooter() ==
                                        (Player) Event0
                                                .getEntity()))
                            return 9999;
                        return 0;
                    }
                }
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                if ((PlayerCheck(Event1.getPlayer())) &&
                        (Event1.getItemDrop().getItemStack().getType() == Material.SNOWBALL)) {
                    PlayerInventory inv = Event1.getPlayer().getInventory();
                    if (!inv.contains(Material.SNOWBALL, 16)) {
                        return 1;
                    }
                }
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                if (PlayerCheck(Event2.getPlayer()))
                    return 2;
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                if (PlayerCheck(Event3.getEntity()))
                    return 3;
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                Player pe = (Player) Event0.getEntity();
                Location l1 = pe.getLocation();
                GetPlayer().teleport(l1);
                break;
            case 1:
                PlayerDropItemEvent Event1 = (PlayerDropItemEvent) event;
                Event1.getPlayer().sendMessage(
                        ChatColor.RED + "소유한 눈덩이가 16개 이하일시 못버립니다.");
                Event1.setCancelled(true);
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                Event2.getPlayer().sendMessage(ChatColor.GREEN + "눈덩이가 지급됩니다.");
                PlayerInventory inv = Event2.getPlayer().getInventory();
                inv.setItem(8, new ItemStack(Material.SNOWBALL, 64));
                inv.setItem(7, new ItemStack(Material.SNOWBALL, 64));
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                List<ItemStack> itemlist = Event3.getDrops();
                for (int l = 0; l < itemlist.size(); l++) {
                    if (((ItemStack) itemlist.get(l)).getType() == Material.SNOWBALL)
                        itemlist.remove(l);
                }
        }
    }

    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.SNOWBALL, 64));
        p.getInventory().setItem(7, new ItemStack(Material.SNOWBALL, 64));
    }

    public void A_ResetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.SNOWBALL, 64));
        p.getInventory().setItem(7, new ItemStack(Material.SNOWBALL, 64));
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Minato.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */