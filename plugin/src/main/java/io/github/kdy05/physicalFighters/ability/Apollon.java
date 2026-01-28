package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.module.InvincibilityManager;
import org.bukkit.ChatColor;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.AbilitySpec;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.kdy05.physicalFighters.util.AbilityUtils;

public class Apollon extends Ability {
    // 타겟 위치 저장
    private Location targetLocation = null;

    public Apollon() {
        super(AbilitySpec.builder("아폴론", Type.Active_Immediately, Rank.S)
                .cooldown(45)
                .guide(Usage.IronLeft + "바라보는 방향에 불구덩이를 만듭니다.")
                .build());
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent event0 = (PlayerInteractEvent) event;
        Player p = event0.getPlayer();

        if (!isOwner(p) || !isValidItem(Ability.DefaultItem)) {
            return -1;
        }

        if (InvincibilityManager.isDamageGuard()) {
            p.sendMessage(ChatColor.RED + "현재 사용할 수 없습니다.");
            return -1;
        }

        targetLocation = AbilityUtils.getTargetLocation(p, 40);
        if (targetLocation == null) {
            p.sendMessage(ChatColor.RED + "거리가 너무 멉니다.");
            return -1;
        }

        return 0;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();

        if (targetLocation == null) {
            p.sendMessage(ChatColor.RED + "능력을 사용할 수 없습니다.");
            return;
        }

        Location center = targetLocation.clone();
        AbilityUtils.createBox(center.clone().add(0, -8, 0), Material.NETHERRACK, 4, 8);
        AbilityUtils.createBox(center.clone().add(0, -7, 0), Material.AIR, 3, 7);
        AbilityUtils.createBox(center.clone().add(0, -7, 0), Material.FIRE, 3, 1);

        targetLocation = null;
    }

}
