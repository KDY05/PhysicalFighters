package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

// TODO: 인벤세이브 오류

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
        switch (CustomData) {
            case 0 -> {
                PlayerDeathEvent event0 = (PlayerDeathEvent) event;
                Player killed = event0.getEntity();

                invsave.put(killed, killed.getInventory().getContents());
                event0.getDrops().clear();

                if (this.AbilityUse) {
                    Bukkit.broadcastMessage(ChatColor.RED + "불사조가 죽었습니다. 더 이상 부활할수 없습니다.");
                    switch (ConfigManager.OnKill) {
                        case 1 -> {
                            killed.setGameMode(GameMode.SPECTATOR);
                            killed.sendMessage(ChatColor.YELLOW + "관전자 모드로 전환되었습니다.");
                        }
                        case 2 ->
                                killed.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.");
                        case 3 -> {
                            if (!killed.isOp()) {
                                killed.ban("당신은 죽었습니다. 다시 들어오실 수 없습니다.", (Date) null, null, true);
                            } else {
                                killed.kickPlayer("당신은 죽었습니다. 게임에서 퇴장합니다.");
                            }
                        }
                    }
                } else {
                    Bukkit.broadcastMessage(ChatColor.RED + "불사조가 죽었습니다. 다시 부활할 수 있습니다.");
                }

                if (killed.getKiller() != null) {
                    this.AbilityUse = true;
                }
                this.ReviveCounter += 1;
            }
            case 1 -> {
                PlayerRespawnEvent event1 = (PlayerRespawnEvent) event;
                Player player = event1.getPlayer();
                ItemStack[] inv = invsave.get(player);

                if (inv != null)
                    player.getInventory().setContents(inv);
                invsave.remove(player);

                if (!this.AbilityUse) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "불사조가 부활하였습니다. 부활 횟수 : " +
                            this.ReviveCounter + "회");
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 600, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 600, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 600, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 0));
            }
        }
    }

    @Override
    public void A_SetEvent(Player p) {
        this.ReviveCounter = 0;
        this.AbilityUse = false;
    }
}
