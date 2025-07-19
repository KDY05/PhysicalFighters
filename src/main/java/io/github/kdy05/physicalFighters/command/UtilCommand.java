package io.github.kdy05.physicalFighters.command;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.utils.AbilityInitializer;
import io.github.kdy05.physicalFighters.utils.CommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UtilCommand implements CommandInterface {

    private final PhysicalFighters plugin;

    public UtilCommand(PhysicalFighters plugin) {
        this.plugin = plugin;
    }

    public boolean onCommandEvent(CommandSender sender, Command command, String label, String[] args) {
        // 운영자 권한 필터
        if (!sender.hasPermission("va.operate")) {
            return false;
        }

        if (args[0].equalsIgnoreCase("uti")) {
            vauti(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("inv")) {
            vainv();
            return true;
        } else if (args[0].equalsIgnoreCase("go")) {
            vago();
            return true;
        } else if (args[0].equalsIgnoreCase("hung")) {
            vahungry();
            return true;
        } else if (args[0].equalsIgnoreCase("dura")) {
            vadura();
            return true;
        } else if (args[0].equalsIgnoreCase("tc")) {
            vatc(sender);
            return true;
        }

        return false;
    }

    public final void vauti(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "유틸 명령어 목록");
        sender.sendMessage(ChatColor.RED + "alist : " + ChatColor.WHITE + "능력자 목록을 봅니다.");
        sender.sendMessage(ChatColor.RED + "elist : " + ChatColor.WHITE + "능력 확정이 안된 사람들을 보여줍니다.");
        sender.sendMessage(ChatColor.RED + "ablist [페이지] : " + ChatColor.WHITE + "능력 목록 및 능력 코드를 보여줍니다.");
        sender.sendMessage(ChatColor.RED + "abi [닉네임] [능력 코드] : " +
                ChatColor.WHITE + "특정 플레이어에게 능력을 강제로 할당합니다. 같은 능력을 " +
                "여럿이서 가질수는 없으며 이미 할당된 능력을 타인에게 " +
                "주면 기존에 갖고있던 사람의 능력은 사라지게 됩니다. " +
                "액티브 능력은 두 종류 이상 중복해서 줄 수 없습니다. " +
                "게임을 시작하지 않더라도 사용이 가능한 명령입니다. " +
                "닉네임칸에 null을 쓰면 해당 능력에 등록된 플레이어가 " +
                "해제되며 명령 코드에 -1을 넣으면 해당 플레이어가 가진" + "모든 능력이 해제됩니다.");
        sender.sendMessage(ChatColor.RED + "tc : " + ChatColor.WHITE + "모든 능력의 지속 효과 및 쿨타임을 초기화 합니다.");
        sender.sendMessage(ChatColor.RED + "skip : " + ChatColor.WHITE + "모든 능력을 강제로 확정시킵니다.");
    }

    public final void vago() {
        Bukkit.broadcastMessage(ChatColor.GREEN +
                "OP에 의해 초반 무적이 해제되었습니다. 이제 데미지를 입습니다.");
        PhysicalFighters.DamageGuard = false;
    }

    public final void vainv() {
        Bukkit.broadcastMessage(ChatColor.GREEN +
                "OP에 의해 초반 무적이 설정되었습니다. 이제 데미지를 입지않습니다.");
        PhysicalFighters.DamageGuard = true;
    }

    public final void vahungry() {
        if (!PhysicalFighters.NoFoodMode) {
            PhysicalFighters.NoFoodMode = true;
            Bukkit.broadcastMessage(ChatColor.GREEN +
                    "OP에 의해 배고픔무한이 설정되었습니다.");
        } else {
            PhysicalFighters.NoFoodMode = false;
            Bukkit.broadcastMessage(ChatColor.RED +
                    "OP에 의해 배고픔무한이 해제되었습니다.");
        }
    }

    public final void vadura() {
        if (!PhysicalFighters.InfinityDur) {
            PhysicalFighters.InfinityDur = true;
            Bukkit.broadcastMessage(ChatColor.GREEN +
                    "OP에 의해 내구도무한이 설정되었습니다.");
        } else {
            PhysicalFighters.InfinityDur = false;
            Bukkit.broadcastMessage(ChatColor.RED +
                    "OP에 의해 내구도무한이 해제되었습니다.");
        }
    }

    public final void vatc(CommandSender sender) {
        for (Ability a : AbilityInitializer.AbilityList) {
            a.cancelDTimer();
            a.cancelCTimer();
        }
        Bukkit.broadcastMessage(String.format(ChatColor.GRAY +
                "관리자 %s님이 쿨타임 및 지속시간을 초기화했습니다.", sender.getName()));
    }

}
