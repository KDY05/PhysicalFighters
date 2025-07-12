package physicalFighters.abilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Gladiator extends Ability {
    private static final int ARENA_SIZE = 5;
    private static final int ARENA_HEIGHT = 8;
    private static final int ARENA_Y_OFFSET = 50;
    private static final int DURATION_TICKS = 300;

    public Gladiator() {
        InitAbility("글레디에이터", Type.Active_Immediately, Rank.SSS,
                "철괴로 상태 타격 시 천공의 투기장으로 이동하여 15초간 1:1 대결을 펼칩니다.",
                "이때 상대에게는 디버프를 걸고, 당신은 버프를 받습니다.");
        InitAbility(60, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
    }

    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        if (!EventManager.DamageGuard && isOwner(Event.getDamager())
                && Event.getEntity() instanceof LivingEntity && isValidItem(Ability.DefaultItem)) {
            return 0;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
        LivingEntity target = (LivingEntity) Event.getEntity();
        Player attacker = getPlayer();

        Location originalTargetLoc = target.getLocation().clone();
        Location originalAttackerLoc = attacker.getLocation().clone();

        Location arenaBase = originalTargetLoc.clone();
        arenaBase.setY(arenaBase.getY() + ARENA_Y_OFFSET);

        createArena(arenaBase);

        Location targetArenaLoc = arenaBase.clone().add(3, 1, 3);
        Location attackerArenaLoc = arenaBase.clone().add(-3, 1, -3);
        target.teleport(targetArenaLoc);
        attacker.teleport(attackerArenaLoc);

        target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, DURATION_TICKS, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, DURATION_TICKS, 0));
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, DURATION_TICKS, 0));
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, DURATION_TICKS, 0));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            target.teleport(originalTargetLoc);
            attacker.teleport(originalAttackerLoc);
            removeArena(arenaBase);
        }, DURATION_TICKS);
    }

    private void createArena(Location base) {
        for (int y = 0; y <= ARENA_HEIGHT; y++) {
            for (int x = -ARENA_SIZE; x <= ARENA_SIZE; x++) {
                for (int z = -ARENA_SIZE; z <= ARENA_SIZE; z++) {
                    Location loc = base.clone().add(x, y, z);
                    if (y == 0 || y == ARENA_HEIGHT) {
                        loc.getBlock().setType(Material.BEDROCK);
                    } else if (x == -ARENA_SIZE || x == ARENA_SIZE || z == -ARENA_SIZE || z == ARENA_SIZE) {
                        loc.getBlock().setType(Material.BEDROCK);
                    } else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
        int torchY = ARENA_HEIGHT - 1;
        base.clone().add(ARENA_SIZE - 1, torchY, ARENA_SIZE - 1).getBlock().setType(Material.TORCH);
        base.clone().add(ARENA_SIZE - 1, torchY, -(ARENA_SIZE - 1)).getBlock().setType(Material.TORCH);
        base.clone().add(-(ARENA_SIZE - 1), torchY, ARENA_SIZE - 1).getBlock().setType(Material.TORCH);
        base.clone().add(-(ARENA_SIZE - 1), torchY, -(ARENA_SIZE - 1)).getBlock().setType(Material.TORCH);
    }

    private void removeArena(Location base) {
        for (int y = 0; y <= ARENA_HEIGHT; y++) {
            for (int x = -ARENA_SIZE; x <= ARENA_SIZE; x++) {
                for (int z = -ARENA_SIZE; z <= ARENA_SIZE; z++) {
                    Location loc = base.clone().add(x, y, z);
                    loc.getBlock().setType(Material.AIR);
                }
            }
        }
    }

}
