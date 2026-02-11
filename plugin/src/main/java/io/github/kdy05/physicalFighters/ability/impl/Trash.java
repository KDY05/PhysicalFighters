package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.util.PotionEffectFactory;

public class Trash extends Ability {
    public Trash() {
        super(AbilitySpec.builder("쓰레기", Type.Active_Immediately, Rank.F)
                .cooldown(10)
                .guide(Usage.IronRight + "체력을 소비하여 1분간 허약해집니다.",
                        Usage.IronAttack + "3% 확률로 능력을 서로 바꿉니다.")
                .build());
        EventManager.registerEntityDamageByEntity(new EventData(this));
        registerRightClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
            if (!isOwner(event0.getDamager())) return -1;
            if (Math.random() > 0.03D) return -1;
            if (event0.getDamager() instanceof Player && event0.getEntity() instanceof Player) {
                Player caster = (Player) event0.getDamager();
                Player target = (Player) event0.getEntity();
                Ability casterAbility = AbilityUtils.findAbility(caster);
                Ability targetAbility = AbilityUtils.findAbility(target);
                if (casterAbility == null || targetAbility == null) return -1;
                targetAbility.setPlayer(caster, false);
                casterAbility.setPlayer(target, false);
                targetAbility.setRunAbility(true);
                casterAbility.setRunAbility(true);
                caster.sendMessage("당신은 쓰레기 능력을 사용해 상대방과 능력을 바꿨습니다.");
                target.sendMessage("당신은 쓰레기 능력에 의해 쓰레기가 되었습니다.");
            }
        } else if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            if (isOwner(event1.getPlayer()) && isValidItem(Ability.DefaultItem)) {
                return 1;
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        if (CustomData == 1) {
            PlayerInteractEvent event1 = (PlayerInteractEvent) event;
            Player p = event1.getPlayer();
            p.setHealth(p.getHealth() - 4);
            p.addPotionEffect(PotionEffectFactory.createWeakness(1200, 0));
        }
    }
}
