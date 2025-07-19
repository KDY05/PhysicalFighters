package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.AUC;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

public class Trash extends Ability {
    public Trash() {
        InitAbility("쓰레기", Type.Active_Immediately, Rank.F,
                "철괴 우클릭 시 체력을 소비하여 1분간 허약해집니다.",
                "철괴로 상대를 타격시 3% 확률로 능력을 서로 바꿉니다.");
        InitAbility(10, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this));
        registerRightClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                if (!isOwner(Event0.getDamager())) break;
                if (Math.random() > 0.03D) break;
                if (Event0.getDamager() instanceof Player caster && Event0.getEntity() instanceof Player target) {
                    Ability casterAbility = AUC.findAbility(caster);
                    Ability targetAbility = AUC.findAbility(target);
                    if (casterAbility == null || targetAbility == null) break;
                    targetAbility.setPlayer(caster, false);
                    casterAbility.setPlayer(target, false);
                    targetAbility.setRunAbility(true);
                    casterAbility.setRunAbility(true);
                    caster.sendMessage("당신은 쓰레기 능력을 사용해 상대방과 능력을 바꿨습니다.");
                    target.sendMessage("당신은 쓰레기 능력에 의해 쓰레기가 되었습니다.");
                }
                break;
            case 1:
                PlayerInteractEvent Event = (PlayerInteractEvent) event;
                if (isOwner(Event.getPlayer()) && isValidItem(Ability.DefaultItem)) {
                    return 0;
                }
                break;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        p.setHealth(p.getHealth() - 4);
        p.addPotionEffect(new PotionEffect(org.bukkit.potion.PotionEffectType.WEAKNESS, 1200, 0));
    }
}
