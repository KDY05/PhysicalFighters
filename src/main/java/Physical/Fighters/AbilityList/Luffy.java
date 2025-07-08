package Physical.Fighters.AbilityList;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.EventManager;

import java.util.Objects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Luffy extends AbilityBase {

    private final Material item;

    public Luffy() {
        InitAbility("루피", Type.Active_Immediately, Rank.S,
                "철괴를 들고 왼쪽클릭을 하면 주먹질을 합니다 [쿨타임 없음]",
                "금괴를 들고 왼쪽클릭을 하면 30초간 속도,점프력,공격력,방어력이 높아집니다. [체력 5 소모, 쿨타임없음]",
                "버프스킬을 사용시에  부작용이 있습니다.",
                "*주의* 금괴를 들고 왼쪽클릭을 난타하시다가 사망하실 수 있습니다.");
        InitAbility(0, 0, true, ShowText.Custom_Text);
        RegisterLeftClickEvent();
        this.item = Material.IRON_INGOT;
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((PlayerCheck(Event.getPlayer())) && (ItemCheck(this.item)) && !EventManager.DamageGuard) {
            return 1;
        }
        if ((PlayerCheck(Event.getPlayer())) && (Event.getPlayer().getHealth() >= 6.0D) && (ItemCheck(Material.GOLD_INGOT))) {
            return 2;
        }
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Location l = Event.getPlayer().getLocation();
        Location l2 = Event.getPlayer().getLocation();
        double degrees = Math.toRadians(-(l.getYaw() % 360.0F));
        double ydeg = Math.toRadians(-(l.getPitch() % 360.0F));

        switch (CustomData) {
            case 1:
                for (int i = 1; i < 5; i++) {
                    l2.setX(l.getX() + (i + 1) * (Math.sin(degrees) * Math.cos(ydeg)));
                    l2.setY(l.getY() + (i + 1) * Math.sin(ydeg));
                    l2.setZ(l.getZ() + (i + 1) * (Math.cos(degrees) * Math.cos(ydeg)));

                    Block targetBlock = Objects.requireNonNull(l2.getWorld()).getBlockAt(l2);
                    if (targetBlock.getType() != Material.SANDSTONE) {
                        Material originalType = targetBlock.getType();

                        // 블록을 SANDSTONE으로 변경
                        targetBlock.setType(Material.SANDSTONE);

                        // 원래 블록으로 복원 (BukkitScheduler 사용)
                        Bukkit.getScheduler().runTaskLater(plugin,
                                () -> targetBlock.setType(originalType), 5L);
                    }

                    for (Player pp : Bukkit.getOnlinePlayers()) {
                        if (pp != GetPlayer()) {
                            Location loc = pp.getLocation();
                            if (l2.getWorld().getBlockAt(l2).getLocation().distance(loc) <= 3.0D) {
                                pp.damage(1, p);
                            }
                        }
                    }
                }
                break;

            case 2:
                p.setHealth(p.getHealth() - 5);
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 600, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 0));
                p.sendMessage(ChatColor.GREEN + "기어세컨드를 사용하였습니다.");
        }
    }

}
