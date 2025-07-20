package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.AbilityInitializer;
import io.github.kdy05.physicalFighters.utils.AUC;
import io.github.kdy05.physicalFighters.utils.CommandInterface;
import io.github.kdy05.physicalFighters.utils.EventData;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Objects;

public class Assimilation
        extends Ability implements CommandInterface {
    private boolean ActiveAss = false;

    public Assimilation() {
        InitAbility("흡수", Type.Passive_Manual, Rank.S,
                "자신이 죽인 사람의 능력을 흡수합니다. 액티브 능력은",
                "1개만 가능합니다. 미러링도 흡수가 가능하며 데스 노트의 경우",
                "이미 능력을 썼더라도 다시 쓸수 있습니다. 자신에게 타격받은",
                "사람은 배고픔이 빠르게 감소합니다. \"/va a\" 명령으로",
                "자신이 흡수한 능력을 볼수 있습니다.", "흡수가 가능한 능력의 갯수에는 제한이 없습니다.");
        InitAbility(0, 0, true);
        EventManager.onEntityDamageByEntity.add(new EventData(this, 0));
        EventManager.onEntityDeath.add(new EventData(this, 1));
        commandManager.registerCommand(this);
    }

    public int A_Condition(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                if (isOwner(Event0.getDamager()))
                    return 0;
                break;
            case 1:
                EntityDeathEvent Event1 = (EntityDeathEvent) event;
                if (Event1.getEntity() instanceof Player &&
                        isOwner(Event1.getEntity().getKiller()))
                    return 1;
                break;
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        switch (CustomData) {
            case 0:
                EntityDamageByEntityEvent Event0 = (EntityDamageByEntityEvent) event;
                if ((Event0.getEntity() instanceof Player p)) {
                    p.setSaturation(0.0F);
                }
                break;
            case 1:
                EntityDeathEvent Event1 = (EntityDeathEvent) event;
                if (Event1.getEntity() instanceof Player victim && Event1.getEntity().getKiller() != null) {
                    Ability ability = AUC.findAbility(victim);
                    Player player = Event1.getEntity().getKiller();
                    if (ability != null) {
                        ability.cancelCTimer();
                        ability.cancelDTimer();
                        if ((ability.getAbilityType() == Type.Passive_AutoMatic) ||
                                (ability.getAbilityType() == Type.Passive_Manual)) {
                            ability.setPlayer(player, false);
                            player.sendMessage(
                                    ChatColor.GREEN + "새로운 패시브 능력을 흡수하였습니다.");
                            player.sendMessage(
                                    ChatColor.YELLOW + "새로운 능력 : " +
                                            ChatColor.WHITE + ability.getAbilityName());
                        } else if (!this.ActiveAss) {
                            ability.setPlayer(player, false);
                            player.sendMessage(
                                    ChatColor.GREEN + "새로운 액티브 능력을 흡수하였습니다.");
                            player.sendMessage(
                                    ChatColor.YELLOW + "새로운 능력 : " +
                                            ChatColor.WHITE + ability.getAbilityName());
                            player.sendMessage(
                                    ChatColor.RED + "이제 액티브 흡수는 불가능합니다.");
                            this.ActiveAss = true;
                        } else {
                            player.sendMessage(
                                    ChatColor.RED + "흡수할수 없는 능력을 가지고 있었습니다.");
                        }
                    }
                }
                break;
        }
    }

    public void A_SetEvent(Player p) {
        this.ActiveAss = false;
    }

    public boolean onCommandEvent(CommandSender sender, Command command, String label, String[] args) {
        if (((sender instanceof Player)) && (isOwner((Player) sender)) &&
                (args[0].equalsIgnoreCase("a")) && (args.length == 1)) {
            sender.sendMessage(ChatColor.GREEN + "-- 당신이 소유한 능력 --");
            for (Ability a : AbilityInitializer.AbilityList) {
                if (a.isOwner(getPlayer())) {
                    Objects.requireNonNull(getPlayer()).sendMessage(a.getAbilityName());
                }
            }
            return true;
        }
        return false;
    }
}
