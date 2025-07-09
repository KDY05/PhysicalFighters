package physicalFighters.utils;

import physicalFighters.core.AbilityBase;
import physicalFighters.core.AbilityList;
import physicalFighters.PhysicalFighters;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class AUC {
    public static void InfoTextOut(Player p) {
        AbilityBase a;
        if (AbilityList.assimilation.getPlayer() == p) {
            a = AbilityList.assimilation;
        } else {
            a = AbilityBase.FindAbility(p);
        }
        if (a != null) {
            p.sendMessage(ChatColor.GREEN + "---------------");
            p.sendMessage(ChatColor.GOLD + "- 능력 정보 -");
            p.sendMessage(ChatColor.DARK_AQUA + "참고 : 능력 리스트중 가장 상단의 능력만 보여줍니다.");
            if (PhysicalFighters.ReverseMode) {
                p.sendMessage(ChatColor.AQUA + a.getAbilityName() + ChatColor.WHITE + " [" + a.getRank().getText() + ChatColor.WHITE + "] ");
            } else
                p.sendMessage(ChatColor.AQUA + a.getAbilityName() + ChatColor.WHITE + " [" + TypeTextOut(a) + "] " + a.getRank().getText());
            for (int l = 0; l < a.getGuide().length; l++) {
                p.sendMessage(a.getGuide()[l]);
            }
            if (!PhysicalFighters.ReverseMode)
                p.sendMessage(TimerTextOut(a));
            p.sendMessage(ChatColor.GREEN + "---------------");
            return;
        }
        p.sendMessage(ChatColor.RED + "능력이 없거나 옵저버입니다.");
    }

    public static String TypeTextOut(AbilityBase ab) {
        AbilityBase.Type type = ab.getAbilityType();
        if (!ab.getRunAbility()) return ChatColor.RED + "능력 비활성화됨" + ChatColor.WHITE;
        if (type == AbilityBase.Type.Active_Continue)
            return ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "지속" + ChatColor.WHITE;
        if (type == AbilityBase.Type.Active_Immediately)
            return ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "즉발" + ChatColor.WHITE;
        if (type == AbilityBase.Type.Passive_AutoMatic)
            return ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "자동" + ChatColor.WHITE;
        if (type == AbilityBase.Type.Passive_Manual)
            return ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "수동" + ChatColor.WHITE;
        return "Unknown";
    }

    public static String TimerTextOut(AbilityBase data) {
        if (data.getAbilityType() == AbilityBase.Type.Active_Continue)
            return String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / " + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "%d초", data.getCoolDown(), data.getDuration());
        if (data.getAbilityType() == AbilityBase.Type.Active_Immediately)
            return String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / " + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음", data.getCoolDown());
        if (data.getAbilityType() == AbilityBase.Type.Passive_AutoMatic)
            return ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "없음 / " + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음";
        if (data.getAbilityType() == AbilityBase.Type.Passive_Manual)
            return ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "없음 / " + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음";
        return "None";
    }
}
