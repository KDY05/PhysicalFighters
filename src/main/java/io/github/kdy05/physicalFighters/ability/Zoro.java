package io.github.kdy05.physicalFighters.ability;

import org.bukkit.ChatColor;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.EventData;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

// TODO: 리메이크, 데미지 계수로 조정(1.0~1.8배), 시간 지날 수록 0.1배수씩 자동 하락

public class Zoro extends Ability {
    private double dmg = 0;

    public Zoro() {
        InitAbility("조로", Type.Active_Immediately, Rank.S,
                Usage.IronLeft + "능력 사용 시 칼의 데미지가 랜덤으로 설정됩니다.");
        InitAbility(45, 0, true);
        registerLeftClickEvent();
        EventManager.onEntityDamageByEntity.add(new EventData(this, 1));
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0 -> {
                PlayerInteractEvent event0 = (PlayerInteractEvent) event;
                if (isOwner(event0.getPlayer()) && isValidItem(Ability.DefaultItem)) {
                    return 0;
                }
            }
            case 1 -> {
                EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) event;
                if (isOwner(event1.getDamager()) && ((Player) event1.getDamager()).getInventory().getItemInMainHand()
                        .getType().name().endsWith("_SWORD")) {
                    event1.setDamage(((Player) event1.getDamager()).getAttackCooldown() * this.dmg);
                }
            }
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Random random = new Random();
        this.dmg = random.nextInt(5) + 5;
        event0.getPlayer().sendMessage(ChatColor.RED + "데미지가 " + this.dmg + "로 설정되었습니다.");
    }
}
