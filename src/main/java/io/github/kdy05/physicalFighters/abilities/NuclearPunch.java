package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NuclearPunch extends Ability {
    public NuclearPunch() {
        InitAbility("핵 펀치", Type.Active_Immediately, Rank.A, new String[]{
                "철괴로 타격을 당한 상대가 매우 멀리 넉백당합니다.", "동시에 데미지 20을 받습니다."});
        InitAbility(45, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if ((isOwner(Event.getDamager())) && (isValidItem(Ability.DefaultItem))) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        Event.setDamage(8);
        Event.getEntity().getWorld()
                .createExplosion(Event.getEntity().getLocation(), 0.0F);
        int knockback = -24;
        if ((Event.getEntity() instanceof Player)) {
            Player p = (Player) Event.getEntity();
            if (p.isBlocking()) {
                knockback = -12;
            }
        }
        Event.getEntity().setVelocity(
                Event.getEntity()
                        .getVelocity()
                        .add(Event
                                .getDamager()
                                .getLocation()
                                .toVector()
                                .subtract(
                                        Event.getEntity().getLocation()
                                                .toVector()).normalize()
                                .multiply(knockback)));
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\NuclearPunch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */