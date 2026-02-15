package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.EventData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Random;
import java.util.UUID;

// TODO: 리메이크, 대미지 계수로 조정(1.0~1.8배), 시간 지날 수록 0.1배수씩 자동 하락

public class Zoro extends Ability {
    private double dmg = 0;

    public Zoro(UUID playerUuid) {
        super(AbilitySpec.builder("조로", Type.ActiveImmediately, Rank.S)
                .cooldown(45)
                .guide(Usage.IronLeft + "능력 사용 시 칼의 대미지가 랜덤으로 설정됩니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        registerLeftClickEvent();
        EventManager.registerEntityDamageByEntity(new EventData(this, 1));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        if (CustomData == 0) {
            PlayerInteractEvent event0 = (PlayerInteractEvent) event;
            if (isOwner(event0.getPlayer()) && isValidItem(Ability.DefaultItem)) {
                return 0;
            }
        } else if (CustomData == 1) {
            EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) event;
            if (isOwner(event1.getDamager()) && ((Player) event1.getDamager()).getInventory().getItemInMainHand()
                    .getType().name().endsWith("_SWORD")) {
                event1.setDamage(((Player) event1.getDamager()).getAttackCooldown() * this.dmg);
            }
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Random random = new Random();
        this.dmg = random.nextInt(5) + 5;
        event0.getPlayer().sendMessage(ChatColor.RED + "대미지가 " + this.dmg + "로 설정되었습니다.");
    }
}
