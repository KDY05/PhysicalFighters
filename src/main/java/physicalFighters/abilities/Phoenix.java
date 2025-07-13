package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;
import physicalFighters.PhysicalFighters;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Phoenix extends Ability {
    private int ReviveCounter = 0;
    private boolean AbilityUse = false;
    private static final HashMap<Player, ItemStack[]> invsave = new HashMap<>();

    public Phoenix() {
        InitAbility("불사조", Type.Passive_Manual, Rank.A,
                "자연사할 시 무제한으로 인벤토리를 잃지 않고 부활합니다.",
                "타인에게 사망할 경우 1회에 한하여 자연사 판정으로 부활합니다.",
                "부활시 자신의 능력이 모두에게 알려지게 됩니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDeath.add(new EventData(this, 0));
        EventManager.onPlayerRespawn.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDeathEvent Event0 = (EntityDeathEvent) event;
                if (isOwner(Event0.getEntity()))
                    return 0;
                break;
            case 1:
                PlayerRespawnEvent Event1 = (PlayerRespawnEvent) event;
                if (isOwner(Event1.getPlayer()))
                    return 1;
                break;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                PlayerDeathEvent Event0 = (PlayerDeathEvent) event;
                Player killed = Event0.getEntity();
                invsave.put(killed, killed.getInventory().getContents());
                Event0.getDrops().clear();
                if (this.AbilityUse) {
                    Bukkit.broadcastMessage(ChatColor.RED + "불사조가 죽었습니다. 더 이상 부활할수 없습니다.");
                    if (PhysicalFighters.AutoKick) {
                        if (PhysicalFighters.AutoBan) {
                            killed.ban("당신은 죽었습니다. 다시 들어오실 수 없습니다.", (Date) null, null, true);
                        } else {
                            killed.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.");
                        }
                    }
                } else {
                    Bukkit.broadcastMessage(ChatColor.RED + "불사조가 죽었습니다. 다시 부활할 수 있습니다.");
                }
                if (killed.getKiller() != null) {
                    this.AbilityUse = true;
                }
                this.ReviveCounter += 1;
                break;
            case 1:
                PlayerRespawnEvent Event1 = (PlayerRespawnEvent) event;
                ItemStack[] inv = invsave.get(Event1.getPlayer());
                if (inv != null)
                    Event1.getPlayer().getInventory().setContents(inv);
                invsave.remove(Event1.getPlayer());
                if (!this.AbilityUse) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "불사조가 부활하였습니다. 부활 횟수 : " +
                            this.ReviveCounter + "회");
                }
                Event1.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 600, 0));
                Event1.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 0));
                Event1.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 600, 0));
                Event1.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0));
                Event1.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 600, 0));
                Event1.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 0));
                Event1.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 0));
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        this.ReviveCounter = 0;
        this.AbilityUse = false;
    }
}
