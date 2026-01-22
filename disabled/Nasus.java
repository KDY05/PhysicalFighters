package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import io.github.kdy05.physicalFighters.utils.EventData;

public class Nasus extends Ability {
    int stack = 0;

    public Nasus() {
        InitAbility("나서스", Type.Active_Immediately, Rank.B, new String[]{"시작시 괭이를 지급합니다.", "괭이로 흙을 경작할때마다 스택이 1씩 쌓입니다.", "10스택당 괭이에 1의 추가대미지가 생깁니다."});
        InitAbility(3, 0, true, ShowText.Custom_Text);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerRightClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if ((e.getDamager() instanceof Player)) {
                Player p = (Player) e.getDamager();
                if ((isOwner(p)) &&
                        (p.getInventory().getItemInMainHand().getType() == Material.WOODEN_HOE)) {
                    int dmg = this.stack / 10;
                    e.setDamage(dmg);
                    p.getInventory().getItemInMainHand().setDurability((short) 0);
                }
            }
        }
        if (CustomData == 1) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if ((e.getPlayer().getInventory().getItemInMainHand().getType() == Material.WOODEN_HOE) &&
                    (isOwner(e.getPlayer())) &&
                    (e.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) && (
                    (e.getClickedBlock().getType() == Material.SHORT_GRASS) || (e.getClickedBlock().getType() == Material.DIRT))) {
                if (this.stack >= 300) {
                    e.getPlayer().sendMessage("300스택 이상 쌓을 수 없습니다.");
                }
                return 0;
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 0) {
            this.stack += 1;
            getPlayer().sendMessage(org.bukkit.ChatColor.YELLOW + "스택을 쌓았습니다. (" + this.stack + ")");
        }
    }

    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, new ItemStack(Material.ARROW, 64));
        p.getInventory().setItem(7, new ItemStack(Material.BOW, 1));
    }

    public void A_ResetEvent(Player p) {
        p.getInventory().removeItem(new ItemStack[]{new ItemStack(Material.ARROW, 64)});
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Nasus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */