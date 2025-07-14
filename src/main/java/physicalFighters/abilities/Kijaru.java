package physicalFighters.abilities;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.AUC;
import physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Kijaru extends Ability {
    public Kijaru() {
        InitAbility("키자루", Type.Active_Immediately, Rank.SS,
                "철괴로 타격한 상대를 빛의 속도로 타격합니다.",
                "상대는 엄청난 속도로 멀리 날라갑니다. 당신도 상대를 따라 근접하게 날라갑니다.",
                "낙하데미지를 받지 않습니다.");
        InitAbility(45, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
        EventManager.onEntityDamage.add(new EventData(this, 3));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
                if (isOwner(Event.getDamager()) && isValidItem(Ability.DefaultItem)
                        && !EventManager.DamageGuard && Event.getEntity() instanceof LivingEntity)
                    return 0;
            }
            case 3 -> {
                EntityDamageEvent Event2 = (EntityDamageEvent) event;
                if (isOwner(Event2.getEntity()) && Event2.getCause() == DamageCause.FALL) {
                    getPlayer().sendMessage(ChatColor.GREEN + "사뿐하게 떨어져 데미지를 받지 않았습니다.");
                    Event2.setCancelled(true);
                }
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) Event.getEntity();

        Location loc = entity.getLocation();
        loc.setY(loc.getY() + 2.0D);
        entity.teleport(loc);

        entity.getWorld().createExplosion(entity.getLocation(), 0.0F);
        AUC.goVelocity(entity, getPlayer().getLocation(), -10);
        new Kizaru(getPlayer(), entity).runTaskLater(plugin, 20L);
        Event.setDamage(8);
    }

    static class Kizaru extends BukkitRunnable {
        Player caster;
        LivingEntity target;

        Kizaru(Player caster, LivingEntity target) {
            this.caster = caster;
            this.target = target;
        }

        @Override
        public void run() {
            Location loc2 = target.getLocation();
            loc2.setY(loc2.getY() + 2.0D);
            this.caster.teleport(loc2);
        }
    }
}
