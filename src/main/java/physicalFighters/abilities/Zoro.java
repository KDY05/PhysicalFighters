package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.utils.ACC;
import physicalFighters.utils.EventData;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Zoro extends AbilityBase {
    int dama = 0;

    public Zoro() {
        InitAbility("조로", Type.Active_Immediately, Rank.S, new String[]{
                "철괴 왼쪽클릭시 칼의 데미지가 랜덤으로 설정됩니다."});
        InitAbility(45, 0, true);
        registerLeftClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 1));
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 1) {
            EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
            if ((isOwner(Event.getDamager())) && ((isValidItem(Material.DIAMOND_SWORD)) || (isValidItem(Material.WOODEN_SWORD))
                    || (isValidItem(Material.IRON_SWORD)) || (isValidItem(Material.GOLDEN_SWORD)))) {
                if (this.dama != 0) {
                    Event.setDamage(this.dama);
                } else {
                    this.dama = ((int) Event.getDamage());
                }
            }
        } else if (CustomData == 0) {
            PlayerInteractEvent Event1 = (PlayerInteractEvent) event;
            if ((isOwner(Event1.getPlayer())) && (isValidItem(ACC.DefaultItem))) {
                return 0;
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        int rand = 0;
        Random random = new Random();
        rand = random.nextInt(5) + 5;
        this.dama = rand;
        Event.getPlayer().sendMessage(org.bukkit.ChatColor.RED + "데미지가 " + this.dama + "로 설정되었습니다.");
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Zoro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */