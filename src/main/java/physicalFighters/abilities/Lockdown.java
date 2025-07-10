package physicalFighters.abilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import physicalFighters.PhysicalFighters;
import physicalFighters.core.Ability;
import physicalFighters.utils.CommandInterface;
import physicalFighters.utils.Vector;


public class Lockdown extends Ability implements CommandInterface {
    private Ability victim;
    private CommandSender sender;
    private String[] data;

    public Lockdown() {
        InitAbility("봉인", Type.Active_Continue, Rank.B,
                "특정 플레이어의 능력을 캔슬시키고 1분간 봉인하며 상대의",
                "배고픔 수치를 0으로 만들어 도망치기 어렵게 합니다.",
                "명령어로 작동하며 사용법은 \"/va s 대상닉네임\" 입니다.",
                "자신과 60칸 이내 거리에 있어야 사용할수 있으며 게임 시작후",
                "능력 제한 시간동안은 이 능력을 사용할 수 없습니다.");
        InitAbility(80, 60, true);
        commandManager.RegisterCommand(this);
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (sender instanceof Player p && isOwner(p)) {
            if (!Ability.restrictionTimer.isRunning()) {
                if (Bukkit.getServer().getPlayerExact(data[1]) != null) {
                    Player pn = Bukkit.getServer().getPlayerExact(data[1]);
                    if (pn != null && p.getName().equals(pn.getName())) {
                        p.sendMessage(ChatColor.RED + "자기 자신에게 능력을 사용할수 없습니다.");
                        data = null;
                        return -1;
                    }
                    victim = Ability.FindAbility(pn);
                    if (victim != null) {
                        Vector vec = new Vector(p.getLocation());
                        if (pn != null && vec.distance(pn.getLocation()) <= 60) {
                            data = null;
                            return 0;
                        }
                    } else
                        p.sendMessage(ChatColor.RED + "옵저버입니다.");
                } else
                    p.sendMessage(ChatColor.RED + "존재하지 않는 플레이어입니다.");
            } else
                p.sendMessage(ChatColor.RED + "아직 능력을 사용할 수 없습니다.");
        } else
            sender.sendMessage(ChatColor.RED + "이 명령은 사용할 수 없습니다.");
        data = null;
        return -1;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
    }

    @Override
    public void A_DurationStart() {
        Player p = (Player) sender;
        Player pn = victim.getPlayer();
        p.sendMessage(String.format("%s님의 능력을 1분간 봉인합니다.", pn.getName()));
        pn.sendMessage(String.format(ChatColor.RED + "경고, %s님이 당신에게 Lockdown 능력을 사용했습니다.", p.getName()));
        pn.sendMessage(ChatColor.RED + "지속 효과가 모두 해제되고 1분간 능력 효과가 봉인됩니다.");
        victim.cancelDTimer();
        victim.cancelCTimer();
        victim.setRunAbility(false);
        if (!PhysicalFighters.NoFoodMode)
            victim.getPlayer().setFoodLevel(0);
    }


    @Override
    public void A_DurationEnd() {
        Player pn = victim.getPlayer();
        pn.sendMessage(ChatColor.GREEN + "봉인이 해제되었습니다.");
        victim.setRunAbility(true);
    }


    @Override
    public boolean onCommandEvent(CommandSender sender, Command command,
                                  String label, String[] data) {
        this.sender = sender;
        this.data = data;
        if (data[0].equalsIgnoreCase("s") && data.length == 2) {
            this.AbilityExcute(null, 0);
            return true;
        }
        return false;
    }
}
