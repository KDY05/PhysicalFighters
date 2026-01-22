package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.HashMap;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
    private final HashMap<Player, ItemStack[]> invsave = new HashMap<>();

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
                EntityDeathEvent event0 = (EntityDeathEvent) event;
                if (isOwner(event0.getEntity()))
                    return 0;
                break;
            case 1:
                PlayerRespawnEvent event1 = (PlayerRespawnEvent) event;
                if (isOwner(event1.getPlayer()))
                    return 1;
                break;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerDeathEvent event0 = (PlayerDeathEvent) event;
            Player killed = event0.getEntity();

            invsave.put(killed, killed.getInventory().getContents());
            event0.getDrops().clear();

            if (this.AbilityUse) {
                Bukkit.broadcastMessage(ChatColor.RED + "불사조가 죽었습니다. 더 이상 부활할수 없습니다.");
                if (ConfigManager.OnKill == 1) {
                    Location deathLocation = killed.getLocation().clone();
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        killed.setGameMode(GameMode.SPECTATOR);
                        killed.spigot().respawn();
                        killed.teleport(deathLocation);
                        killed.sendTitle(ChatColor.RED + "사망하였습니다!",
                                ChatColor.YELLOW + "관전자 모드로 전환합니다.",
                                10, 100 ,10);
                    }, 1L);
                } else if (ConfigManager.OnKill == 2) {
                    killed.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.");
                } else if (ConfigManager.OnKill == 3) {
                    if (!killed.isOp()) {
                        Bukkit.getBanList(BanList.Type.NAME).addBan(killed.getName(),
                                "당신은 죽었습니다. 다시 들어오실 수 없습니다.", null, null);
                        killed.kickPlayer("당신은 죽었습니다. 다시 들어오실 수 없습니다.");
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
        } else if (CustomData == 1) {
            PlayerRespawnEvent event1 = (PlayerRespawnEvent) event;
            Player player = event1.getPlayer();
            ItemStack[] inv = invsave.get(player);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (inv != null && player.isOnline()) {
                    player.getInventory().setContents(inv);
                }
                invsave.remove(player);
            }, 1L);

            if (!this.AbilityUse) {
                Bukkit.broadcastMessage(ChatColor.GREEN + "불사조가 부활하였습니다. 부활 횟수 : " +
                        this.ReviveCounter + "회");
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 600, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 600, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 0));
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        this.ReviveCounter = 0;
        this.AbilityUse = false;
    }
}
