package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Medic extends Ability {
    public Medic() {
        InitAbility("메딕", Type.Active_Immediately, Rank.B, new String[]{
                "철괴 왼클릭시 맞은 사람의 체력이 6 회복됩니다.", "철괴 오른클릭시 자신의 체력을 6 회복합니다.",
                "두 기능은 쿨타임을 공유합니다."});
        InitAbility(5, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
        registerRightClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                if (((Event1.getEntity() instanceof Player)) &&
                        (isOwner(Event1.getDamager())) &&
                        (isValidItem(Ability.DefaultItem))) {
                    return 0;
                }
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                if ((isOwner(Event2.getPlayer())) &&
                        (isValidItem(Ability.DefaultItem))) {
                    return 1;
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                Player p1 = (Player) Event1.getEntity();
                if (((Damageable) p1).getHealth() <= 14.0D) {
                    p1.setHealth(((Damageable) p1).getHealth() + 6);
                } else {
                    p1.setHealth(20);
                }
                p1.sendMessage(String.format(ChatColor.GREEN +
                        "%s님의 메딕 능력으로 체력을 6 회복했습니다.", new Object[]{getPlayer().getName()}));
                getPlayer().sendMessage(
                        String.format(ChatColor.GREEN + "%s님의 체력을 6 회복시켰습니다.", new Object[]{
                                p1.getName()}));
                Event1.setCancelled(true);
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                Player p2 = Event2.getPlayer();
                if (((Damageable) p2).getHealth() <= 14.0D) {
                    p2.setHealth(((Damageable) p2).getHealth() + 6);
                } else
                    p2.setHealth(20);
                p2.sendMessage(ChatColor.GREEN + "자신의 체력을 6 회복했습니다.");
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Medic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */