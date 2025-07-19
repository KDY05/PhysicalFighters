package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Time2 extends Ability {
    public Time2() {
        InitAbility("시간을 지배하는 자", Type.Active_Immediately, Rank.A,
                "자신을 제외한 모든 플레이어들의 속도를 15초간 느리게 만듭니다.");
        InitAbility(40, 0, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem)) && !PhysicalFighters.DamageGuard) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        Player[] List = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        Player[] arrayOfPlayer1;
        int j = (arrayOfPlayer1 = List).length;
        for (int i = 0; i < j; i++) {
            Player p = arrayOfPlayer1[i];
            if (p != getPlayer()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
            }
            p.sendMessage(org.bukkit.ChatColor.GREEN +
                    "시간을 지배하는 자에의해 15초간 당신의 시간이 느리게 흘러갑니다.");
        }
    }
}
