package physicalFighters.abilities;

import org.bukkit.ChatColor;
import physicalFighters.core.Ability;
import physicalFighters.core.EventManager;
import physicalFighters.utils.EventData;

import java.util.Random;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

// TODO: 리메이크, 데미지 계수로 조정(1.0~1.8배), 시간 지날 수록 0.1배수씩 자동 하락

public class Zoro extends Ability {
    private double dmg = 0;

    public Zoro() {
        InitAbility("조로", Type.Active_Immediately, Rank.S,
                "철괴 왼쪽클릭시 칼의 데미지가 랜덤으로 설정됩니다.");
        InitAbility(45, 0, true);
        registerLeftClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent Event1 = (PlayerInteractEvent) event;
            if (isOwner(Event1.getPlayer()) && isValidItem(Ability.DefaultItem)) {
                return 0;
            }
        } else if (CustomData == 1) {
            EntityDamageByEntityEvent Event = (EntityDamageByEntityEvent) event;
            if (isOwner(Event.getDamager()) && getPlayer().getInventory().getItemInMainHand().getType().name().endsWith("_SWORD")) {
                if (this.dmg != 0) {
                    Event.setDamage(this.dmg);
                } else {
                    this.dmg = (Event.getDamage());
                }
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Random random = new Random();
        this.dmg = random.nextInt(5) + 5;
        Event.getPlayer().sendMessage(ChatColor.RED + "데미지가 " + this.dmg + "로 설정되었습니다.");
    }
}
