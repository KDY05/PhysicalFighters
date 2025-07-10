package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Flower extends Ability {
    public Flower() {
        if (PhysicalFighters.SRankUsed) {
            InitAbility("흡혈초", Type.Active_Immediately, Rank.SS, new String[]{
                    "철괴 왼클릭시 맞은 사람의 체력을 흡수합니다.",
                    "철괴 오른클릭시 자신의 체력을 소비해 레벨을 얻습니다."});
            InitAbility(5, 0, true);
            EventManager.onEntityDamageByEntity.add(new EventData(this));
            registerRightClickEvent();
        }
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                if (((Event1.getEntity() instanceof Player)) &&
                        (isOwner(Event1.getDamager())) &&
                        (isValidItem(Ability.DefaultItem)) &&
                        (!EventManager.DamageGuard)) {
                    return 1;
                }
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                if ((isOwner(Event2.getPlayer())) &&
                        (((Damageable) getPlayer()).getHealth() >= 16.0D) &&
                        (isValidItem(Ability.DefaultItem))) {
                    return 2;
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 1:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                Player p1 = (Player) Event1.getEntity();
                if (((Damageable) getPlayer()).getHealth() <= 14.0D) {
                    getPlayer().setHealth(((Damageable) getPlayer()).getHealth() + 6);
                    p1.setHealth(((Damageable) p1).getHealth() - 6);
                } else {
                    getPlayer().setHealth(20);
                    p1.setHealth(((Damageable) p1).getHealth() - 6);
                }
                p1.sendMessage(String.format(
                        ChatColor.RED + "%s님이 당신의 체력을 흡수했습니다.", new Object[]{getPlayer()
                                .getName()}));
                getPlayer().sendMessage(
                        String.format(ChatColor.RED + "%s님의 체력을 흡수했습니다.", new Object[]{
                                p1.getName()}));
                break;
            case 2:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                Player p2 = Event2.getPlayer();
                p2.setLevel(p2.getLevel() + 10);
                p2.sendMessage(ChatColor.GREEN + "레벨을 얻었습니다. +2");
                p2.setHealth(((Damageable) p2).getHealth() - 15);
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Flower.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */