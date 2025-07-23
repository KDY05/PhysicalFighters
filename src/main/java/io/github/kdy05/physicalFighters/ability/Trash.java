package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

public class Trash extends Ability {
    public Trash() {
        InitAbility("쓰레기", Type.Active_Immediately, Rank.F,
               Usage.IronRight + "체력을 소비하여 1분간 허약해집니다.",
              Usage.IronAttack + "3% 확률로 능력을 서로 바꿉니다.");
        InitAbility(10, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
        registerRightClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent) event;
                if (!isOwner(event0.getDamager())) break;
                if (Math.random() > 0.03D) break;
                if (event0.getDamager() instanceof Player caster && event0.getEntity() instanceof Player target) {
                    Ability casterAbility = AbilityUtils.findAbility(caster);
                    Ability targetAbility = AbilityUtils.findAbility(target);
                    if (casterAbility == null || targetAbility == null) break;
                    targetAbility.setPlayer(caster, false);
                    casterAbility.setPlayer(target, false);
                    targetAbility.setRunAbility(true);
                    casterAbility.setRunAbility(true);
                    caster.sendMessage("당신은 쓰레기 능력을 사용해 상대방과 능력을 바꿨습니다.");
                    target.sendMessage("당신은 쓰레기 능력에 의해 쓰레기가 되었습니다.");
                }
            }
            case 1 -> {
                PlayerInteractEvent event1 = (PlayerInteractEvent) event;
                if (isOwner(event1.getPlayer()) && isValidItem(Ability.DefaultItem)) {
                    return 1;
                }
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
            p.addPotionEffect(new PotionEffect(org.bukkit.potion.PotionEffectType.WEAKNESS, 1200, 0));
        }
    }
}
