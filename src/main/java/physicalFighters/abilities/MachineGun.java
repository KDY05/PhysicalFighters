package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

public class MachineGun extends Ability {
    private int bullet = 0;
    private Material item;
    private HashMap<Player, Boolean> relo = new HashMap();

    public MachineGun() {
        InitAbility("기관총", Type.Active_Immediately, Rank.S, new String[]{
                "고속으로 화살을 발사합니다. 금괴를 들고 오른클릭을 누르면 연사가",
                "가능합니다. 철괴를 탄창으로 사용하며 한 탄창은 30발입니다.",
                "피격시 10% 확률로 방어력을 무시하고 체력을 2 감소시키는",
                "크리티컬이 발생합니다. 기본 탄환 데미지는 1입니다."});
        InitAbility(0, 0, true, ShowText.Custom_Text);
        registerRightClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 3));
        EventManager.onProjectileHitEvent.add(new EventData(this, 5));
        this.item = Material.GOLD_INGOT;
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 1:
                PlayerInteractEvent Event0 = (PlayerInteractEvent) event;
                if ((!EventManager.DamageGuard) &&
                        (isOwner(Event0.getPlayer())) && (isValidItem(this.item))) {
                    if (this.bullet != 0) {
                        return 10;
                    }
                    if (getPlayer().getInventory().contains(Material.IRON_INGOT)) {
                        return 20;
                    }
                    getPlayer().sendMessage(ChatColor.RED + "탄창이 없습니다.");
                    if (this.relo.containsKey(getPlayer())) {
                        getPlayer().sendMessage(ChatColor.RED + "장전중입니다.");
                    }
                }
                break;
            case 3:
                EntityDamageByEntityEvent Event1 = (EntityDamageByEntityEvent) event;
                if ((Event1.getDamager() instanceof Arrow)) {
                    Arrow a = (Arrow) Event1.getDamager();
                    if (isOwner((Player) a.getShooter())) {
                        if (((Event1.getEntity() instanceof Player)) &&
                                ((Player) a.getShooter() ==
                                        (Player) Event1
                                                .getEntity()))
                            return -1;
                        return 3;
                    }
                }
                break;
            case 5:
                ProjectileHitEvent Event2 = (ProjectileHitEvent) event;
                if ((Event2.getEntity() instanceof Arrow)) {
                    Arrow a = (Arrow) Event2.getEntity();
                    if (((a.getShooter() instanceof Player)) &&
                            (isOwner((Player) a.getShooter()))) {
                        a.remove();
                        return -2;
                    }
                }
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 3:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                Event0.setDamage(1);
                if (((Event0.getEntity() instanceof Player)) && (Math.random() <= 0.1D)) {
                    Player p = (Player) Event0.getEntity();
                    p.getWorld().createExplosion(Event0.getEntity().getLocation(),
                            0.0F);
                    if (((Damageable) p).getHealth() > 2.0D) {
                        p.setHealth(((Damageable) p).getHealth() - 2);
                    }
                }
                break;
            case 10:
                PlayerInteractEvent Event1 = (PlayerInteractEvent) event;
                if (this.bullet % 5 == 0) {
                    Event1.getPlayer().sendMessage(
                            String.format(ChatColor.AQUA + "남은 탄환 : " +
                                    ChatColor.WHITE + "%d개", new Object[]{
                                    Integer.valueOf(this.bullet)}));
                }
                this.bullet -= 1;
                Event1.getPlayer().launchProjectile(Arrow.class);
                break;
            case 20:
                PlayerInteractEvent Event2 = (PlayerInteractEvent) event;
                if (!this.relo.containsKey(getPlayer())) {
                    Event2.getPlayer().sendMessage("탄환이 다 떨어졌습니다. 장전합니다 [3초소요]");
                    this.relo.put(Event2.getPlayer(), Boolean.valueOf(true));
                    Timer timer = new Timer();
                    timer.schedule(new onReload(), 3000L);
                }
                break;
        }
    }

    class onReload extends TimerTask {
        onReload() {
        }

        public void run() {
            PlayerInventory inv = MachineGun.this.getPlayer().getInventory();
            int sell = inv.first(Material.IRON_INGOT);
            if (inv.getItem(sell).getAmount() == 1) {
                inv.clear(sell);
            } else {
                inv.getItem(sell).setAmount(inv.getItem(sell).getAmount() - 1);
            }
            MachineGun.this.getPlayer().updateInventory();
            MachineGun.this.bullet = 30;
            MachineGun.this.getPlayer().sendMessage(ChatColor.GREEN + "재장전 완료");
            MachineGun.this.relo.remove(MachineGun.this.getPlayer());
        }
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\MachineGun.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */