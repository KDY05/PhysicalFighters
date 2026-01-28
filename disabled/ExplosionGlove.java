package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.game.Ability;

import io.github.kdy05.physicalFighters.game.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExplosionGlove extends Ability {
    public ExplosionGlove() {
        InitAbility("폭파장갑", Type.Active_Immediately, Rank.A,
                "능력 사용시 주변의 플레이어에게 폭발을 일으키며", "공중으로 띄웁니다.");
        InitAbility(50, 0, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if ((isOwner(e.getPlayer())) && (isValidItem(Material.IRON_INGOT)) && !ConfigManager.DamageGuard) {
                return 0;
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            Player[] pl = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            e.getPlayer().getWorld().createExplosion(e.getPlayer().getLocation(), 5.0F);
            for (Player p : pl) {
                if ((p != getPlayer()) &&
                        (p.getLocation().distance(getPlayer().getLocation()) <= 15.0D)) {
                    p.getWorld().createExplosion(p.getLocation(), 3.0F);
                    p.damage(14, e.getPlayer());
                    p.setVelocity(new org.bukkit.util.Vector(0.0D, 1.5D, 0.0D));
                }
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\ExplosionGlove.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */