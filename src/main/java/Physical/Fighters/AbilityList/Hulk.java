package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.ACC;
import Physical.Fighters.MinerModule.EventData;
import Physical.Fighters.PhysicalFighters;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Hulk extends AbilityBase {
    boolean playerhulk = false;
    int playerhealth = 20;

    public Hulk() {
        if (PhysicalFighters.SRankUsed) {
            InitAbility("헐크", Type.Active_Immediately, Rank.SSS, new String[]{
                    "철괴 오른쪽클릭시에 30초간 매우 강해집니다.",
                    "버프를 받으며, 모든 데미지를 반으로 줄여받으며, 일부 액티브능력을 무시합니다.",
                    "당신의 데미지는 1.5배가 되며, 당신의 공격 범위가 넓어집니다."});
            InitAbility(180, 0, true);
            EventManager.onEntityDamageByEntity.add(new EventData(this));
            RegisterRightClickEvent();
        }
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                if ((Event1.getEntity() instanceof Player)) {
                    if (PlayerCheck(Event1.getDamager()))
                        if (this.playerhulk) {
                            Event1.setDamage((int) (Event1.getDamage() * 1.5D));
                            ((Player) Event1.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0), true);
                        } else {
                            Event1.setDamage(Event1.getDamage());
                        }
                    if (PlayerCheck(Event1.getEntity())) {
                        if (this.playerhulk) {
                            Event1.setDamage(Event1.getDamage() / 2);
                        } else
                            Event1.setDamage(Event1.getDamage());
                    }
                }
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                if ((PlayerCheck(Event2.getPlayer())) &&
                        (ItemCheck(ACC.DefaultItem)) && !EventManager.DamageGuard) {
                    return 0;
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
        Player p = Event2.getPlayer();
        this.playerhealth = ((int) ((Damageable) p).getHealth());
        p.getWorld().createExplosion(p.getLocation(), 0.0F);
        p.setHealth(20);
        p.sendMessage(ChatColor.RED + "당신은 헐크로 변신했으며, 30초간 무척 강해집니다. 30초가 지나면 당신은 원래대로 돌아옵니다.");
        this.playerhulk = true;
        GetPlayer().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 0), true);
        GetPlayer().addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 0), true);
        GetPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 0), true);
        GetPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0), true);
        GetPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 600, 0), true);
        GetPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 600, 0), true);
        Timer timer = new Timer();
        timer.schedule(new Pauck(GetPlayer()), 30000L);
    }

    class Pauck extends TimerTask {
        Player p1;

        Pauck(Player pp1) {
            this.p1 = pp1;
        }

        public void run() {
            this.p1.setHealth(Hulk.this.playerhealth);
            Hulk.this.playerhulk = false;
            this.p1.sendMessage(ChatColor.GREEN + "원래대로 돌아왔습니다.");
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Hulk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */