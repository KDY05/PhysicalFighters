package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;

import java.util.Timer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Amateras
        extends Ability {
    Timer timer = new Timer();

    public Amateras() {
        InitAbility("아마테라스", Type.Active_Immediately, Rank.S, new String[]{
                "능력 사용시 체력을 소비해서 보고있는 사물을 태워버립니다.",
                "*아카이누와 블레이즈 등 불에 내성이 있는 적에게는 통하지 않습니다."});
        InitAbility(0, 0, true, ShowText.Custom_Text);
        registerRightClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem)) &&
                (!EventManager.DamageGuard)) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        Block block = getPlayer().getTargetBlock(null, 0);
        Location ll = block.getLocation();
        Location l2 = block.getLocation();
        l2.setY(ll.getY() + 1.0D);
        if (block.getWorld().getBlockAt(l2).getType() == Material.AIR) {
            block.getWorld().getBlockAt(l2).setType(Material.FIRE);
            getPlayer().setHealth((int) (((Damageable) getPlayer()).getHealth() - 0.0D));
        } else {
            block.getWorld().getHighestBlockAt(ll).setType(Material.FIRE);
            getPlayer().setHealth((int) (((Damageable) getPlayer()).getHealth() - 0.0D));
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Amateras.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */