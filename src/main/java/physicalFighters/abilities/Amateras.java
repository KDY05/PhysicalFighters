package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

// TODO: 리메이크 필요, 이건 그냥 원거리 라이터잖아요;;

public class Amateras extends Ability {
    public Amateras() {
        InitAbility("아마테라스", Type.Active_Immediately, Rank.S,
                "철괴 우클릭 시 체력을 소비해서 보고있는 사물을 불태웁니다.",
                "(!) 불에 내성이 있는 적에게는 통하지 않습니다.");
        InitAbility(0, 0, true, ShowText.Custom_Text);
        registerRightClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (isOwner(Event.getPlayer()) && isValidItem(Ability.DefaultItem) && !EventManager.DamageGuard) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        Block block = getPlayer().getTargetBlock(null, 0); // TODO: 크래시 가능성 있음
        Location ll = block.getLocation();
        Location l2 = block.getLocation();
        l2.setY(ll.getY() + 1.0D);
        if (block.getWorld().getBlockAt(l2).getType() == Material.AIR) {
            block.getWorld().getBlockAt(l2).setType(Material.FIRE);
            getPlayer().setHealth((int) (getPlayer().getHealth() - 0.0D));
        } else {
            block.getWorld().getHighestBlockAt(ll).setType(Material.FIRE);
            getPlayer().setHealth((int) (getPlayer().getHealth() - 0.0D));
        }
    }
}
