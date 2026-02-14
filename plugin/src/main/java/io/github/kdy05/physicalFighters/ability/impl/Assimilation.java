package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.command.CommandInterface;
import io.github.kdy05.physicalFighters.util.EventData;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import java.util.UUID;

public class Assimilation extends Ability implements CommandInterface {
    private boolean ActiveAss = false;

    public Assimilation(UUID playerUuid) {
        super(AbilitySpec.builder("흡수", Type.PassiveManual, Rank.S)
                .guide("자신이 죽인 플레이어의 능력을 흡수합니다.",
                        "\"/va a\" 명령으로 자신이 흡수한 능력들을 확인할 수 있습니다.",
                        "흡수 가능한 능력의 개수는 제한이 없지만 액티브 능력은 최대 1개만 가능합니다.")
                .build(), playerUuid);
    }

    @Override
    public void registerEvents() {
        EventManager.registerEntityDeath(new EventData(this, 0));
    }

    @Override
    public int checkCondition(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDeathEvent event0 = (EntityDeathEvent) event;
            if (event0.getEntity() instanceof Player && isOwner(event0.getEntity().getKiller()))
                return 0;
        }
        return -1;
    }

    @Override
    public void applyEffect(Event event, int CustomData) {
        if (CustomData == 0) {
            EntityDeathEvent event0 = (EntityDeathEvent) event;
            if (!(event0.getEntity() instanceof Player) || event0.getEntity().getKiller() == null) return;
            Player victim = (Player) event0.getEntity();
            Ability ability = AbilityUtils.findAbility(victim);
            Player player = event0.getEntity().getKiller();
            if (ability == null) return;
            String absorbedTypeName = ability.getAbilityName();
            if (ability.getAbilityType() == Type.PassiveAutoMatic || ability.getAbilityType() == Type.PassiveManual) {
                AbilityRegistry.deactivate(ability, false);
                AbilityRegistry.createAndActivate(absorbedTypeName, player, false);
                player.sendMessage(ChatColor.GREEN + "새로운 패시브 능력을 흡수하였습니다.");
                player.sendMessage(ChatColor.YELLOW + "새로운 능력 : " + ChatColor.WHITE + absorbedTypeName);
            } else if (!this.ActiveAss) {
                AbilityRegistry.deactivate(ability, false);
                AbilityRegistry.createAndActivate(absorbedTypeName, player, false);
                player.sendMessage(ChatColor.GREEN + "새로운 액티브 능력을 흡수하였습니다.");
                player.sendMessage(ChatColor.YELLOW + "새로운 능력 : " + ChatColor.WHITE + absorbedTypeName);
                player.sendMessage(ChatColor.RED + "더이상 액티브 흡수는 불가능합니다.");
                this.ActiveAss = true;
            } else {
                player.sendMessage(ChatColor.RED + "흡수할 수 없는 능력입니다.");
            }
        }
    }

    @Override
    public boolean isInfoPrimary() { return true; }

    @Override
    public void onActivate(Player p) {
        this.ActiveAss = false;
    }

    @Override
    public boolean onCommandEvent(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && isOwner((Player) sender)
                && args[0].equalsIgnoreCase("a") && args.length == 1) {
            sender.sendMessage(ChatColor.GREEN + "-- 당신이 소유한 능력 --");
            for (Ability ability : AbilityRegistry.findAbilities((Player) sender)) {
                sender.sendMessage(ability.getAbilityName());
            }
            return true;
        }
        return false;
    }

}
