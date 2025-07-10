package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Hulk extends Ability {
    boolean playerhulk = false;
    int playerhealth = 20;

    public Hulk() {
        if (PhysicalFighters.SRankUsed) {
            InitAbility("헐크", Type.Active_Immediately, Rank.SSS,
                    "철괴 오른쪽클릭시에 30초간 매우 강해집니다.",
                    "버프를 받으며, 모든 데미지를 반으로 줄여받으며, 일부 액티브능력을 무시합니다.",
                    "당신의 데미지는 1.5배가 되며, 당신의 공격 범위가 넓어집니다.");
            InitAbility(180, 0, true);
            EventManager.onEntityDamageByEntity.add(new EventData(this));
            registerRightClickEvent();
        }
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                if ((Event1.getEntity() instanceof Player)) {
                    // 공격력 1.5배
                    if (isOwner(Event1.getDamager()))
                        if (this.playerhulk) {
                            Event1.setDamage((int) (Event1.getDamage() * 1.5D));
                            ((Player) Event1.getEntity()).addPotionEffect(
                                    new PotionEffect(PotionEffectType.NAUSEA, 100, 0));
                        } else {
                            Event1.setDamage(Event1.getDamage());
                        }
                    // 대미지 반감
                    if (isOwner(Event1.getEntity())) {
                        if (this.playerhulk) {
                            Event1.setDamage(Event1.getDamage() / 2);
                        } else
                            Event1.setDamage(Event1.getDamage());
                    }
                }
                break;
            case 1:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                if ((isOwner(Event2.getPlayer())) &&
                        (isValidItem(Ability.DefaultItem)) && !EventManager.DamageGuard) {
                    return 0;
                }
                break;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
        Player p = Event2.getPlayer();
        this.playerhealth = ((int) p.getHealth());
        p.getWorld().createExplosion(p.getLocation(), 0.0F);
        p.setHealth(20);
        p.sendMessage(ChatColor.RED + "당신은 헐크로 변신했으며, 30초간 무척 강해집니다. 30초가 지나면 당신은 원래대로 돌아옵니다.");
        this.playerhulk = true;
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 0));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 0));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 0));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 600, 0));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 600, 0));
        
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = getPlayer();
                if (player != null && player.isOnline()) {
                    player.setHealth(playerhealth);
                    playerhulk = false;
                    player.sendMessage(ChatColor.GREEN + "원래대로 돌아왔습니다.");
                }
            }
        }.runTaskLater(plugin, 600L); // 30초 = 600틱 (20틱 = 1초)
    }
}
