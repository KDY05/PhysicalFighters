package physicalFighters.abilities;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.EventManager;
import physicalFighters.core.AbilityList;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

public class Mirroring extends AbilityBase {
    public Mirroring() {
        if ((!PhysicalFighters.Toner) &&
                (!PhysicalFighters.Specialability)) {
            InitAbility("미러링", Type.Passive_Manual, Rank.SSS, new String[]{
                    "당신을 죽인 사람을 함께 저승으로 끌고갑니다.", "자신이 죽을경우 죽인 사람 역시 죽게됩니다.",
                    "데스노트는 이 능력에 죽지 않습니다."});
            InitAbility(0, 0, true);
            EventManager.onEntityDeath.add(new EventData(this));
        }
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDeathEvent Event = (EntityDeathEvent) event;
        if (((Event.getEntity().getKiller() instanceof Player)) &&
                (isOwner(Event.getEntity())))
            return 0;
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDeathEvent Event = (EntityDeathEvent) event;
        Player p = (Player) Event.getEntity();
        Bukkit.broadcastMessage(String.format(ChatColor.RED +
                "%s님의 미러링 능력이 발동되었습니다.", new Object[]{
                p.getName()}));
        if (AbilityList.assimilation.getPlayer() == p.getKiller()) {
            AbilityList.assimilation.A_Effect(Event, 1);
            Bukkit.broadcastMessage(ChatColor.GREEN + "미러링 능력이 무력화 되었습니다.");
            return;
        }
        if ((AbilityList.aegis.getPlayer() == p.getKiller()) &&
                (AbilityList.aegis.getDurationState())) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "미러링 능력이 무력화 되었습니다.");
            return;
        }
        Bukkit.broadcastMessage(String.format(ChatColor.RED +
                "%s님의 능력에 의해 %s님이 죽었습니다.", new Object[]{
                p.getName(), p.getKiller()
                .getName()}));
        p.getKiller().damage(5000);
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Mirroring.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */