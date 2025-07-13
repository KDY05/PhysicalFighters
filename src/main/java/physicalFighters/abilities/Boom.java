package physicalFighters.abilities;

import org.bukkit.entity.LivingEntity;
import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;

import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Boom extends Ability {
    public Boom() {
        InitAbility("붐포인트", Type.Active_Immediately, Rank.S,
                "철괴 좌클릭시 20초간 10m 안에 있는 적을 폭발시킵니다.");
        InitAbility(60, 0, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (!EventManager.DamageGuard &&
                isOwner(Event.getPlayer()) && isValidItem(Ability.DefaultItem)) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        new Exploder(getPlayer()).runTaskTimer(plugin, 0L, 30L);
    }

    static class Exploder extends BukkitRunnable {
        private int count = 0;
        private final Player caster;
        public Exploder(Player caster) {
            this.caster = caster;
        }
        public void run() {
            Player caster = this.caster;
            if (caster == null) return;
            caster.getWorld().getNearbyEntities(caster.getLocation(), 10, 10, 10).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .map(entity -> (LivingEntity) entity)
                    .filter(entity -> entity != caster)
                    .forEach(entity -> entity.getWorld().createExplosion(entity.getLocation(), 0.3f));
            count++;
            if (count >= 14) cancel();
        }
    }

}
