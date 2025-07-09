package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import physicalFighters.utils.ACC;

public class ReverseAlchemy extends AbilityBase {
    public ReverseAlchemy() {
        InitAbility("반 연금술", Type.Active_Immediately, Rank.C, new String[]{
                "제련된 금을 철괴와 1:1, 다이아몬드와 3:1 비율로 교환합니다.",
                "철은 왼클릭, 다이아몬드는 오른클릭으로 사용합니다.",
                "비록 철괴로 능력을 작동시키지만 금괴는 갖고 있어야 합니다.",
                "금괴를 들고 오른클릭시 금괴를 하나 소모하여 자신의 체력과",
                "포만감을 모두 채워줍니다."});
        InitAbility(2, 0, true, ShowText.No_Text);
        registerLeftClickEvent();
        registerRightClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (isOwner(Event.getPlayer())) {
            PlayerInventory inv = Event.getPlayer().getInventory();
            if (isValidItem(ACC.DefaultItem)) {
                switch (CustomData) {
                    case 0:
                        if (inv.contains(Material.GOLD_INGOT, 1)) {
                            return CustomData;
                        }
                        break;
                    case 1:
                        if (inv.contains(Material.GOLD_INGOT, 3))
                            return CustomData;
                        break;
                }
                Event.getPlayer().sendMessage(ChatColor.RED + "금괴가 부족합니다.");
            } else if ((isValidItem(Material.GOLD_INGOT)) &&
                    (inv.contains(Material.GOLD_INGOT, 1)) && (CustomData == 1)) {
                return 2;
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        switch (CustomData) {
            case 0:
                Event.getPlayer().getInventory().removeItem(new ItemStack[]{new ItemStack(Material.GOLD_INGOT, 1)});
                GiveItem(Event.getPlayer(), Material.IRON_INGOT, 1);
                break;
            case 1:
                Event.getPlayer().getInventory().removeItem(new ItemStack[]{new ItemStack(Material.GOLD_INGOT, 3)});
                GiveItem(Event.getPlayer(), Material.DIAMOND, 1);
                break;
            case 2:
                Event.getPlayer().getInventory().removeItem(new ItemStack[]{new ItemStack(Material.GOLD_INGOT, 1)});
                Event.getPlayer().setHealth(20);
                Event.getPlayer().setFoodLevel(20);
                Event.getPlayer().setSaturation(5.0F);
        }
        Event.getPlayer().updateInventory();
    }

    private void GiveItem(Player p, Material item, int amount) {
        PlayerInventory inv = p.getInventory();
        Map<Integer, ItemStack> overflow = inv.addItem(new ItemStack[]{new ItemStack(item, amount)});
        for (ItemStack is : overflow.values()) {
            p.getWorld().dropItemNaturally(p.getLocation(), is);
            p.sendMessage(ChatColor.RED + "경고, 인벤토리 공간이 부족합니다.");
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\ReverseAlchemy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */