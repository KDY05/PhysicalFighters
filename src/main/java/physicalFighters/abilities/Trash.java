package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

public class Trash extends Ability {
    public Trash() {
        if ((!PhysicalFighters.Toner) && (PhysicalFighters.SRankUsed) && (!PhysicalFighters.Specialability)) {
            InitAbility("쓰레기", Type.Active_Immediately, Rank.F,
                    "능력 사용시 체력을 소비하여 1분간 허약해집니다.",
                    "철괴로 상대를 타격시 1%확률로 능력을 서로 바꿉니다.");
            InitAbility(10, 0, true);
            EventManager.onEntityDamageByEntity.add(new EventData(this));
            registerRightClickEvent();
        }
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                if (isOwner(Event0.getDamager())) {
                    if (Math.random() <= 0.01D) {
                        Player p1 = (Player) Event0.getDamager();
                        Player p2 = (Player) Event0.getEntity();
                        Ability a = Ability.FindAbility(p1);
                        Ability a2 = Ability.FindAbility(p2);
                        if (a == null || a2 == null) break;
                        a2.setPlayer(p1, false);
                        a.setPlayer(p2, false);
                        a2.setRunAbility(true);
                        a.setRunAbility(true);
                        p1.sendMessage("당신은 쓰레기 능력을 사용해 상대방과 능력을 바꿨습니다.");
                        p2.sendMessage("당신은 쓰레기 능력에 의해 쓰레기가 되었습니다.");
                    }
                }
                break;
            case 1:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem))) {
                    return 0;
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        p.setHealth(p.getHealth() - 4);
        p.addPotionEffect(new PotionEffect(org.bukkit.potion.PotionEffectType.WEAKNESS, 1200, 0));
    }
}
