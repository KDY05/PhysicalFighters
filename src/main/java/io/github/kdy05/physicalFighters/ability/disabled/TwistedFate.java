package io.github.kdy05.physicalFighters.ability.disabled;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TwistedFate extends Ability {
    int Card = 0;
    int RCard = 0;
    int DCard = 0;
    Timer t = new Timer();
    Timer tt = new Timer();

    public TwistedFate() {
        InitAbility("트위스티드 페이트", Type.Active_Immediately, Rank.S, new String[]{
                "철괴를 들고 우클릭시 카드를 뽑으며, 좌클릭시 바라보는 방향으로 카드를 날립니다.",
                "카드는 순서대로 빨강색, 파랑색, 황금색이 있습니다.",
                ChatColor.RED + "빨강색 카드" + ChatColor.WHITE + "는 15의 데미지를 주며, 상대와 주변 플레이어의 이동속도를 느리게합니다.",
                ChatColor.BLUE + "파랑색 카드" + ChatColor.WHITE + "는 20의 데미지를 주며, 본 스킬의 재사용 대기시간을 초기화 시켜줍니다.",
                ChatColor.GOLD + "황금색 카드" + ChatColor.WHITE + "는 10의 데미지를 주며, 상대를 2초간 못움직이게합니다."});
        InitAbility(20, 0, true);
        registerLeftClickEvent();
        registerRightClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 2));
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if ((isOwner(e.getPlayer())) &&
                    ((e.getAction() == Action.LEFT_CLICK_AIR) || (e.getAction() == Action.LEFT_CLICK_BLOCK)) &&
                    (isValidItem(Material.IRON_INGOT))) {
                if (this.RCard != 0) {
                    Arrow a = e.getPlayer().launchProjectile(Arrow.class);
                    a.setVelocity(a.getVelocity().multiply(5));
                    a.setShooter(e.getPlayer());
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_SLIME_ATTACK, 3.0F, 3.0F);
                    int i = this.RCard;
                    this.DCard = i;
                    this.RCard = 0;
                    Bukkit.broadcastMessage(this.DCard + "," + this.RCard);
                } else {
                    e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "카드를 뽑아주세요 (철괴우클릭)");
                }
            }
        }
        if (CustomData == 1) {
            final PlayerInteractEvent e = (PlayerInteractEvent) event;
            if ((isOwner(e.getPlayer())) &&
                    ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) &&
                    (isValidItem(Material.IRON_INGOT))) {
                if (this.Card != 0) {
                    if (this.Card == 1) {
                        this.Card = 0;
                        this.RCard = 1;
                        e.getPlayer().sendMessage(ChatColor.RED + "빨강색 카드를 선택했습니다.");
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                    }
                    if (this.Card == 2) {
                        this.Card = 0;
                        this.RCard = 2;
                        e.getPlayer().sendMessage(ChatColor.BLUE + "파랑색 카드를 선택했습니다.");
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 3.0F);
                    }
                    if (this.Card == 3) {
                        this.Card = 0;
                        this.RCard = 3;
                        e.getPlayer().sendMessage(ChatColor.GOLD + "황금색 카드를 선택했습니다.");
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 3.0F, 1.0F);
                    }
                    this.tt = new Timer();
                    this.tt.schedule(new TimerTask() {
                        public void run() {
                            TwistedFate.this.RCard = 0;
                            e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "카드를 던지지 못했습니다.");
                        }
                    }, 5000L);
                    this.t.cancel();
                    return -1;
                }
                return 1;
            }
        }
        if (CustomData == 2) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if ((this.DCard != 0) &&
                    ((e.getDamager() instanceof Arrow)) && ((e.getEntity() instanceof LivingEntity))) {
                Player p = (Player) ((Arrow) e.getDamager()).getShooter();
                LivingEntity t = (LivingEntity) e.getEntity();
                if (isOwner(p)) {
                    if (this.DCard == 1) {
                        p.sendMessage(ChatColor.RED + "빨강색 카드를 맞춰 주변 플레이어의 속도를 늦춥니다..");
                        t.getWorld().playEffect(t.getLocation(), org.bukkit.Effect.ENDER_SIGNAL, 1);
                        p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 3.0F, -1.0F);
                        for (Player l : Bukkit.getOnlinePlayers()) {
                            if ((l != p) && (l.getLocation().distance(t.getLocation()) <= 3.0D)) {
                                l.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 2));
                                l.playSound(l.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 3.0F, -1.0F);
                                l.damage(15);
                            }
                        }
                        this.DCard = 0;
                    }
                    if (this.DCard == 2) {
                        if (getCool() - 10 > 0) {
                            setCool(getCool() - 10);
                        } else
                            cancelCTimer();
                        p.sendMessage(ChatColor.BLUE + "파랑색 카드를 맞춰 쿨타임이 10초 감소되었습니다.");
                        p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 3.0F, 3.0F);
                        t.damage(20);
                        this.DCard = 0;
                    }
                    if (this.DCard == 3) {
                        p.sendMessage(ChatColor.GOLD + "황금색 카드를 맞춰 상대를 2초간 못움직이게합니다.");
                        p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 3.0F, 3.0F);
                        t.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 10));
                        t.damage(10);
                        this.DCard = 0;
                    }
                }
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 1) {
            final PlayerInteractEvent e = (PlayerInteractEvent) event;
            this.t = new Timer();
            this.t.schedule(new TimerTask() {
                int i;

                public void run() {
                    if (this.i == 6) {
                        e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "카드를 뽑지 못했습니다.");
                        TwistedFate.this.Card = 0;
                        cancel();
                    }
                    if (TwistedFate.this.Card == 0) {
                        e.getPlayer().sendMessage(ChatColor.RED + "빨강색 카드");
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 3.0F, 1.0F);
                        TwistedFate.this.Card = 1;
                    } else if (TwistedFate.this.Card == 1) {
                        e.getPlayer().sendMessage(ChatColor.BLUE + "파랑색 카드");
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 3.0F, 1.0F);
                        TwistedFate.this.Card = 2;
                    } else if (TwistedFate.this.Card == 2) {
                        e.getPlayer().sendMessage(ChatColor.GOLD + "황금색 카드");
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 3.0F, 1.0F);
                        TwistedFate.this.Card = 3;
                    } else if (TwistedFate.this.Card == 3) {
                        e.getPlayer().sendMessage(ChatColor.RED + "빨강색 카드");
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 3.0F, 1.0F);
                        TwistedFate.this.Card = 1;
                    }
                    this.i += 1;
                }
            }, 0L, 700L);
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\TwistedFate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */