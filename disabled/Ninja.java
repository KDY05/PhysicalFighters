package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Ninja extends Ability {
    public Ninja() {
        InitAbility("닌자", Type.Active_Immediately, Rank.S, new String[]{
                "철괴 왼쪽클릭시 매우 빠르게 화살을 발사합니다. ",
                "이 화살에 플레이어가 맞을 경우 10%확률로 플레이어를 폭발시키고, ",
                "30%의 확률로 플레이어에게 불을 붙히고,", "65%의 확률로 쿨타임이 초기화됩니다."});
        InitAbility(10, 0, true);
        registerLeftClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 1));
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if ((!ConfigManager.DamageGuard) && (isOwner(Event.getPlayer())) &&
                        (isValidItem(Ability.DefaultItem))) {
                    return 0;
                }
                break;
            case 1:
                EntityDamageByEntityEvent E = (EntityDamageByEntityEvent) event;
                if ((E.getDamager() instanceof Arrow)) {
                    Arrow a = (Arrow) E.getDamager();
                    if (isOwner((Player) a.getShooter())) {
                        Player p = (Player) a.getShooter();
                        E.setDamage(E.getDamage() + 13);
                        if (Math.random() <= 0.65D) {
                            cancelCTimer();
                            p.sendMessage(ChatColor.YELLOW + "플레이어를 맞춰 쿨타임이 초기화되었습니다.");
                        }
                        if (Math.random() <= 0.1D) {
                            World w = E.getEntity().getWorld();
                            w.createExplosion(E.getEntity().getLocation(), 4.0F);
                        }
                    }
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Arrow a = p.launchProjectile(Arrow.class);
        a.setVelocity(a.getVelocity().multiply(8));
        if (Math.random() <= 0.3D) {
            a.setFireTicks(20);
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Ninja.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */