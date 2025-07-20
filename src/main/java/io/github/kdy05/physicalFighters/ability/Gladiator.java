package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.utils.AUC;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Gladiator extends Ability {
    private static final int ARENA_SIZE = 5;
    private static final int ARENA_HEIGHT = 8;
    private static final int ARENA_Y_OFFSET = 50;
    private static final int DURATION_TICKS = 300;

    private LivingEntity target = null;

    public Gladiator() {
        InitAbility("글레디에이터", Type.Active_Continue, Rank.SSS,
                Usage.IronAttack + "천공의 투기장으로 이동하여 15초간 1:1 대결을 펼칩니다.",
                "이때 상대는 디버프, 당신은 버프를 받습니다.");
        InitAbility(60, DURATION_TICKS / 20, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
        if (!ConfigManager.DamageGuard && isOwner(event0.getDamager())
                && event0.getEntity() instanceof LivingEntity && isValidItem(Ability.DefaultItem)) {
            target = (LivingEntity) event0.getEntity();
            return 0;
        }
        return -1;
    }

    @Override
    public void A_DurationStart() {
        if (target == null) return;
        Player attacker = getPlayer();

        Location originalTargetLoc = target.getLocation().clone();
        Location originalAttackerLoc = attacker.getLocation().clone();

        Location arenaBase = originalTargetLoc.clone();
        arenaBase.setY(arenaBase.getY() + ARENA_Y_OFFSET);

        // 아레나 생성 후 마주 보도록 텔레포트
        createArena(arenaBase);

        Location targetArenaLoc = arenaBase.clone().add(2, 2, 2);
        Location attackerArenaLoc = arenaBase.clone().add(-2, 2, -2);

        Vector targetDirection = attackerArenaLoc.toVector().subtract(targetArenaLoc.toVector()).normalize();
        targetArenaLoc.setDirection(targetDirection);
        target.teleport(targetArenaLoc);

        Vector attackerDirection = targetArenaLoc.toVector().subtract(attackerArenaLoc.toVector()).normalize();
        attackerArenaLoc.setDirection(attackerDirection);
        attacker.teleport(attackerArenaLoc);

        // 포션 효과
        target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, DURATION_TICKS, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, DURATION_TICKS, 0));
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, DURATION_TICKS, 0));
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, DURATION_TICKS, 0));

        // 원상 복구
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            target.teleport(originalTargetLoc);
            attacker.teleport(originalAttackerLoc);
            removeArena(arenaBase);
        }, DURATION_TICKS);
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
    }

    private void createArena(Location base) {
        AUC.createBox(base, Material.BEDROCK, ARENA_SIZE, ARENA_HEIGHT);
        AUC.createBox(base.clone().add(0, 1, 0), Material.AIR,
                ARENA_SIZE - 1, ARENA_HEIGHT - 2, true);
        int torchY = ARENA_HEIGHT - 2;
        base.clone().add(ARENA_SIZE - 1, torchY, ARENA_SIZE - 1).getBlock().setType(Material.TORCH);
        base.clone().add(ARENA_SIZE - 1, torchY, -(ARENA_SIZE - 1)).getBlock().setType(Material.TORCH);
        base.clone().add(-(ARENA_SIZE - 1), torchY, ARENA_SIZE - 1).getBlock().setType(Material.TORCH);
        base.clone().add(-(ARENA_SIZE - 1), torchY, -(ARENA_SIZE - 1)).getBlock().setType(Material.TORCH);
    }

    private void removeArena(Location base) {
        AUC.createBox(base, Material.AIR, ARENA_SIZE, ARENA_HEIGHT, true);
    }

}
