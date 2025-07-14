package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.AUC;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Infighter extends Ability {
    public Infighter() {
        InitAbility("인파이터", Type.Passive_AutoMatic, Rank.A, new String[]{
                "주먹으로 모든것을 해결하는 능력입니다.",
                "주먹으로 공격하면 대상에게 큰 충격을 받습니다.",
                "10%확률로 폭발이 일어나며 대상이 넉백됩니다.",
                "20%의 확률로 대상을 5초간 그로기상태로 만듭니다."});
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if ((!EventManager.DamageGuard) &&
                (isOwner(Event.getDamager()))) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Player p = (Player) e.getDamager();
        Player t = (Player) e.getEntity();
        if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
            Random r = new Random();
            int dmg = 5 + r.nextInt(12);
            e.setDamage(dmg);
            if (Math.random() <= 0.1D) {
                t.getWorld().createExplosion(t.getLocation(), 0.0F);
                Location l1 = p.getLocation();
                AUC.goVelocity(t, l1, -3);
            }
            if (Math.random() <= 0.2D) {
                t.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0), true);
                t.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0), true);
                t.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0), true);
                t.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0), true);
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Infighter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */