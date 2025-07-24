package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Thunder extends Ability {
    public static boolean ppon = false;

    public Thunder() {
        InitAbility("썬더볼트", Type.Active_Immediately, Rank.S, new String[]{
                "철괴 좌클릭으로 능력을 사용합니다.",
                "주변 5칸의 플레이어에게 데미지를 줍니다."});
        InitAbility(5, 0, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if ((isOwner(e.getPlayer())) && (isValidItem(Ability.DefaultItem)) && !ConfigManager.DamageGuard) {
                return 0;
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != e.getPlayer() && p.getLocation().distance(e.getPlayer().getLocation()) <= 5) {
                p.getWorld().strikeLightningEffect(p.getLocation());
                p.damage(10, e.getPlayer());
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Thunder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */