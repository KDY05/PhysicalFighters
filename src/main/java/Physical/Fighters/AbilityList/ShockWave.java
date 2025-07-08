package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.AbilityBase.Rank;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ShockWave extends AbilityBase {
    public ShockWave() {
        InitAbility("쇼크웨이브", Type.Active_Immediately, Rank.A, new String[]{
                "자신이 보고있는 방향으로 막강한 직선 충격포를 쏩니다.",
                "충격파로 인해 물과 벽 건너편까지 폭발력이 통과합니다.",
                "어떤 방향으로도 발사할수 있으며 발사 제약이 없습니다.",
                "공격 범위는 25칸정도이며 자신의 주변 4칸은 보호됩니다.",
                "금괴 왼클릭으로 능력을 발동시킵니다. 철괴 1개 소모."});
        InitAbility(53, 0, true);
        RegisterLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((PlayerCheck(Event.getPlayer())) &&
                (ItemCheck(Material.GOLD_INGOT))) {
            PlayerInventory inv = Event.getPlayer().getInventory();
            if (inv.contains(Material.IRON_INGOT, 1)) {
                return 0;
            }
            Event.getPlayer().sendMessage(org.bukkit.ChatColor.RED + "철괴가 부족합니다.");
            return -2;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        PlayerInventory inv = Event.getPlayer().getInventory();
        Location l = Event.getPlayer().getLocation();
        Location l2 = Event.getPlayer().getLocation();
        int sell = inv.first(Material.IRON_INGOT);
        if (inv.getItem(sell).getAmount() == 1) {
            inv.clear(sell);
        } else {
            inv.getItem(sell).setAmount(inv.getItem(sell).getAmount() - 1);
        }
        Event.getPlayer().updateInventory();
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));
        for (int i = 1; i < 7; i++) {
            l2.setX(l.getX() + (3 * i + 3) * (
                    Math.sin(degrees) * Math.cos(ydeg)));
            l2.setY(l.getY() + (3 * i + 3) * Math.sin(ydeg));
            l2.setZ(l.getZ() + (3 * i + 3) * (
                    Math.cos(degrees) * Math.cos(ydeg)));
            l2.getWorld().createExplosion(l2, 4.0F);
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\ShockWave.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */