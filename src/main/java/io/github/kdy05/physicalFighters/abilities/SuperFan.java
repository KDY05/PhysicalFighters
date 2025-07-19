package io.github.kdy05.physicalFighters.abilities;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SuperFan extends Ability {
    public SuperFan() {
        InitAbility("선풍기", Type.Active_Immediately, Rank.C,
                "철괴를 들고 좌클릭하여 바라보는 방향의 플레이어들을 날려버립니다.",
                "플레이어들은 무더위에 시원함을 느껴 체력이 회복됩니다.",
                "하지만 강한 바람에 의해 눈을 뜨기가 힘들고 허약해집니다.");
        InitAbility(20, 0, true);
        registerLeftClickEvent();
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if (isOwner(Event.getPlayer()) && isValidItem(Ability.DefaultItem) && !PhysicalFighters.DamageGuard) {
            return 0;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        Location l = getPlayer().getLocation();
        Location l2 = getPlayer().getLocation();
        Location l3 = getPlayer().getLocation();
        l3.setY(l.getY() - 1.0D);
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));
        for (int i = 1; i < 10; i++) {
            l2.setX(l.getX() + i + 2.0D * (Math.sin(degrees) * Math.cos(ydeg)));
            l2.setY(l.getY() + i + 2.0D * Math.sin(ydeg));
            l2.setZ(l.getZ() + i + 2.0D * (Math.cos(degrees) * Math.cos(ydeg)));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PhysicalFighters.DamageGuard) continue;
                if (player == getPlayer()) continue;
                if (l2.distance(player.getLocation()) > 3.0D) continue;
                player.setVelocity(player.getVelocity().add(l3.toVector()
                                .subtract(player.getLocation().toVector()).normalize()
                                .multiply(-2.2D)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 2));
                player.sendMessage(org.bukkit.ChatColor.LIGHT_PURPLE + "선풍기의 강력한 바람에 힘을 잃었습니다!");
            }
        }
    }
}
