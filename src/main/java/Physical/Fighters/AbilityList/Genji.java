package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.EventData;
import Physical.Fighters.PhysicalFighters;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;

public class Genji
        extends AbilityBase {
    int aaa;
    boolean po;

    public Genji() {
        if (PhysicalFighters.SRankUsed) {
            InitAbility(
                    "겐지",
                    Type.Active_Immediately,
                    Rank.S,
                    new String[]{
                            "철괴나 칼을 들고 쉬프트시 앞으로 돌진하며 앞의 모든 적에게 큰 데미지를 줍니다.(질풍참)",
                            "질풍참을 사용시 5초간 칼의 사거리가 증가하며 5의 추가데미지를 입힙니다.",
                            "*낙하데미지를 받지않습니다."});
            InitAbility(20, 0, true);
            EventManager.onPlayerToggleSneakEvent.add(new EventData(this, 7));
            RegisterLeftClickEvent();
            RegisterRightClickEvent();
            EventManager.onEntityDamage.add(new EventData(this, 3));
            EventManager.onPlayerInteract.add(new EventData(this, 4));
            EventManager.onEntityDamageByEntity.add(new EventData(this, 5));
        }
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 7) {
            PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
            if ((PlayerCheck(e.getPlayer())) && (e.isSneaking())) {
                return 1;
            }
        }
        if (CustomData == 3) {
            EntityDamageEvent Event2 = (EntityDamageEvent) event;
            if ((PlayerCheck(Event2.getEntity())) &&
                    (Event2.getCause() == DamageCause.FALL)) {
                Event2.setCancelled(true);
                GetPlayer().sendMessage(
                        ChatColor.GREEN + "사뿐하게 떨어져 데미지를 받지 않았습니다.");
            }
        }
        if (CustomData == 4) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if ((PlayerCheck(e.getPlayer())) &&
                    (isSword(e.getPlayer().getItemInHand())) && (this.po)) {
                BlockIterator bi = new BlockIterator(e.getPlayer(), 6);
                while (bi.hasNext()) {
                    Block bb = bi.next();
                    ExplosionDMGPotion(e.getPlayer(), bb.getLocation(), 3, 5, PotionEffectType.WITHER, 20, 2);
                    ExplosionDMGPotion(e.getPlayer(), bb.getLocation(), 3, 0, PotionEffectType.SLOWNESS, 20, 2);
                }
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 1:
                PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
                Location s = e.getPlayer().getLocation();
                e.getPlayer().chat(ChatColor.GREEN + "류진노 켄오 쿠라에!");
                this.po = true;
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        Genji.this.po = false;
                        cancel();
                    }
                }, 5000L);
                s.setY(s.getY() + 1.6D);
                Block b = s.getBlock();
                BlockIterator bi = new BlockIterator(e.getPlayer(), 15);
                while (bi.hasNext()) {
                    Block bb = bi.next();
                    if ((bb.getType().isSolid()) && (bb.getType() != Material.AIR)) break;
                    b = bb;
                    ExplosionDMGL(e.getPlayer(), b.getLocation(), 2, 20);
                }
                Location l = b.getLocation();
                l.setPitch(s.getPitch());
                l.setYaw(s.getYaw());
                e.getPlayer().getWorld().strikeLightningEffect(e.getPlayer().getLocation());
                e.getPlayer().teleport(l);
                e.getPlayer().getWorld().strikeLightningEffect(l);
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Genji.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */