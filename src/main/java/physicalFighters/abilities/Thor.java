package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.AUC;
import physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Thor extends Ability {
    int Attack = 0;

    public Thor() {
        InitAbility("토르", Type.Active_Immediately, Rank.GOD, new String[]{
                "금도끼는 묠니르입니다. 묠니르의 기본데미지는 8입니다.",
                "묠니르를 들고 우클릭시 묠니르에 번개의 힘을 내리치며 주변의 플레이어에게 5의 데미지를 주고,",
                "다음 공격에 +3의 데미지를 농축시킵니다. (6번까지 중첩됩니다.)"});
        InitAbility(8, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        registerRightClickEvent();
    }

    public ItemStack m() {
        ItemStack is = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.YELLOW + "묠니르");
        is.setItemMeta(im);
        return is;
    }

    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 1) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            if ((isOwner(Event.getPlayer())) && (isValidItem(m().getType())) && (!EventManager.DamageGuard)) {
                Event.getPlayer().getInventory().getItemInMainHand().setDurability((short) 0);
                return 0;
            }
        }
        if (CustomData == 0) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if ((isOwner(e.getDamager())) && ((e.getEntity() instanceof LivingEntity))) {
                Player p = (Player) e.getDamager();
                LivingEntity t = (LivingEntity) e.getEntity();
                if ((p.getInventory().getItemInMainHand().getType() == m().getType()) &&
                        (p.getInventory().getItemInMainHand().hasItemMeta()) &&
                        (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "묠니르"))) {
                    e.setDamage(8);
                    if (this.Attack > 0) {
                        t.damage(3 * this.Attack);
                        t.getWorld().strikeLightning(t.getLocation());
                        p.sendMessage(ChatColor.YELLOW + "묠니르에 농축된 번개의 데미지를 추가로 입혔습니다.");
                        this.Attack = 0;
                    }
                    p.getInventory().getItemInMainHand().setDurability((short) 0);
                }
            }
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent Event = (PlayerInteractEvent) event;
            Player p = Event.getPlayer();
            World w = p.getWorld();
            org.bukkit.Location loc = p.getLocation();
            w.strikeLightningEffect(loc);
            w.strikeLightningEffect(loc);
            AUC.splashDamage(p, loc, 3, 5);
            if (this.Attack < 6) {
                this.Attack += 1;
                p.sendMessage(ChatColor.YELLOW + "묠니르에 농축된 번개 : (" + this.Attack + "/6)");
            }
        }
    }

    public void A_SetEvent(Player p) {
        p.getInventory().setItem(8, m());
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Thor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */