package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.utils.ACC;
import physicalFighters.utils.EventData;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Teleporter extends AbilityBase {
    static String signName = null;
    static Location signLoc = null;

    public Teleporter() {
        InitAbility("소환술사", Type.Active_Immediately,
                Rank.A, new String[]{
                        "표지판을 설치하고, 플레이어의 이름을 적은 뒤, 철괴를 휘두르면",
                        "이름이 적힌 플레이어는 표지판으로 이동합니다.",
                        "자기 자신의 이름도 적을 수 있습니다. [표지판을 벽에 설치하면 적용되지 않습니다]"});
        InitAbility(100, 0, true);
        registerLeftClickEvent();
        EventManager.onSignChangeEvent.add(new EventData(this, 1));
        EventManager.onPlayerRespawn.add(new EventData(this, 2));
        EventManager.onEntityDeath.add(new EventData(this, 3));
        EventManager.onBlockBreakEvent.add(new EventData(this, 4));
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if ((isOwner(Event.getPlayer())) && !EventManager.DamageGuard &&
                        (isValidItem(ACC.DefaultItem))) {
                    if ((signName != null) && (signLoc != null)) {
                        return 0;
                    }
                    Event.getPlayer().sendMessage(
                            ChatColor.RED + "표지판을 설치하셔야 합니다.");
                }
                break;
            case 1:
                SignChangeEvent event2 = (SignChangeEvent) event;
                if (isOwner(event2.getPlayer()) && !EventManager.DamageGuard) {
                    if (event2.getBlock().getType() == Material.OAK_SIGN) {
                        if ((!event2.getLine(0).isEmpty()) &&
                                (Bukkit.getPlayer(event2.getLine(0)).isOnline())) {
                            signName =
                                    Bukkit.getPlayer(event2.getLine(0)).getName();
                            signLoc = event2.getBlock().getLocation();
                            event2.getPlayer().sendMessage(
                                    ChatColor.LIGHT_PURPLE + "철괴를 휘두르면 " +
                                            ChatColor.WHITE + signName +
                                            ChatColor.LIGHT_PURPLE +
                                            "님은 이곳으로 텔레포트됩니다.");
                        }
                    } else if ((event2.getBlock().getType() == Material.OAK_SIGN) &&
                            (isOwner(event2.getPlayer()))) {
                        event2.getPlayer().sendMessage(
                                ChatColor.RED + "표지판은 바닥에 세워야합니다.");
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
                break;
            case 4:
                BlockBreakEvent Event4 = (BlockBreakEvent) event;
                if ((signLoc != null) && (Event4.getBlock().getLocation() == signLoc)) {
                    signName = null;
                    signLoc = null;
                    getPlayer().sendMessage(ChatColor.RED + "표지판이 제거되었습니다.");
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                Player p = Event.getPlayer();
                Player t = Bukkit.getPlayer(signName);
                t.sendMessage(ChatColor.RED + "소환술사에 의해 당신은 표지판으로 이동합니다.");
                t.teleport(signLoc);
                p.sendMessage(ChatColor.GREEN +
                        "성공적으로 소환을 마쳤습니다. 100초간 능력을 사용하지 못합니다.");
                signLoc.getBlock().breakNaturally();
                signName = null;
                signLoc = null;
                break;
            case 2:
                PlayerRespawnEvent Event2 = (PlayerRespawnEvent) event;
                Event2.getPlayer().sendMessage(
                        ChatColor.GREEN + "이전에 소유했던 화살은 모두 소멸하며 다시 지급됩니다.");
                PlayerInventory inv = Event2.getPlayer().getInventory();
                inv.remove(new ItemStack(Material.OAK_SIGN, 64));
                inv.setItem(8, new ItemStack(Material.OAK_SIGN, 3));
                break;
            case 3:
                EntityDeathEvent Event3 = (EntityDeathEvent) event;
                List<ItemStack> itemlist = Event3.getDrops();
                for (int l = 0; l < itemlist.size(); l++) {
                    if (((ItemStack) itemlist.get(l)).getType() == Material.ARROW)
                        itemlist.remove(l);
                }
        }
    }

    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.OAK_SIGN, 3));
    }

    public void A_ResetEvent(Player p) {
        p.getInventory().removeItem(new ItemStack[]{new ItemStack(Material.OAK_SIGN)});
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Teleporter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */