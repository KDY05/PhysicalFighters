package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.game.Ability;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import io.github.kdy05.physicalFighters.game.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Pressure extends Ability {
    public Pressure() {
        InitAbility("압력", Type.Active_Immediately, Rank.S, new String[]{
                "철괴로 왼쪽클릭시 20칸 이내의 모든 적을 강한 압력으로 압축시킵니다.",
                "대상플레이어는 대미지와 디버프를 받습니다."});
        InitAbility(40, 0, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem)) && !ConfigManager.DamageGuard) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        LinkedList<Player> ts = new LinkedList();
        for (int i = 0; i < (Bukkit.getOnlinePlayers()).size(); i++) {
            if ((p.getLocation().distance((Bukkit.getOnlinePlayers().toArray(new Player[0]))[i].getLocation()) < 20.0D) &&
                    ((Bukkit.getOnlinePlayers().toArray(new Player[0]))[i] != p)) {
                ts.add((Bukkit.getOnlinePlayers().toArray(new Player[0]))[i]);
            }
        }
        if (!ts.isEmpty()) {
            for (int i = 0; i < ts.size(); i++) {
                Player t = (Player) ts.get(i);
                Timer timer = new Timer();
                timer.schedule(new DTim(p, t), 100L, 100L);
                t.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2), true);
                t.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 2), true);
                t.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 2), true);
                t.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2), true);
                t.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 2), true);
            }
            ts.clear();
        }
    }

    public class DTim extends TimerTask {
        Player pp;
        Player tt;
        private int a = 15;

        DTim(Player p, Player t) {
            this.pp = p;
            this.tt = t;
        }

        public void run() {
            this.tt.damage(8, this.pp);
            this.tt.playEffect(this.tt.getLocation(), org.bukkit.Effect.SMOKE, 20);
            this.a -= 1;
            if (this.a <= 0) {
                cancel();
            }
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Pressure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */