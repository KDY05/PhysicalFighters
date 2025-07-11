package physicalFighters.utils;

import physicalFighters.core.Ability;
import physicalFighters.core.AbilityList;
import physicalFighters.PhysicalFighters;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class AUC {

    public static void showInfoText(Player p) {
        Ability ability;
        if (AbilityList.assimilation.getPlayer() == p) {
            ability = AbilityList.assimilation;
        } else {
            ability = Ability.FindAbility(p);
        }
        if (ability == null) {
            p.sendMessage(ChatColor.RED + "능력이 없거나 옵저버입니다.");
            return;
        }
        p.sendMessage(ChatColor.GREEN + "---------------");
        p.sendMessage(ChatColor.GOLD + "- 능력 정보 -");
        p.sendMessage(ChatColor.DARK_AQUA + "참고 : 능력 리스트중 가장 상단의 능력만 보여줍니다.");
        p.sendMessage(ChatColor.AQUA + ability.getAbilityName() + ChatColor.WHITE
                + " [" + showTypeText(ability) + "] " + ability.getRank().getText());
        for (int l = 0; l < ability.getGuide().length; l++) {
            p.sendMessage(ability.getGuide()[l]);
        }
        p.sendMessage(showTimerText(ability));
        p.sendMessage(ChatColor.GREEN + "---------------");
    }

    public static String showTypeText(Ability ability) {
        Ability.Type type = ability.getAbilityType();
        if (!ability.getRunAbility()) return ChatColor.RED + "능력 비활성화됨" + ChatColor.WHITE;
        if (type == Ability.Type.Active_Continue)
            return ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "지속" + ChatColor.WHITE;
        if (type == Ability.Type.Active_Immediately)
            return ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "즉발" + ChatColor.WHITE;
        if (type == Ability.Type.Passive_AutoMatic)
            return ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "자동" + ChatColor.WHITE;
        if (type == Ability.Type.Passive_Manual)
            return ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "수동" + ChatColor.WHITE;
        return "Unknown";
    }

    public static String showTimerText(Ability ability) {
        if (ability.getAbilityType() == Ability.Type.Active_Continue)
            return String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "%d초", ability.getCoolDown(), ability.getDuration());
        if (ability.getAbilityType() == Ability.Type.Active_Immediately)
            return String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음", ability.getCoolDown());
        if (ability.getAbilityType() == Ability.Type.Passive_AutoMatic)
            return ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "없음 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음";
        if (ability.getAbilityType() == Ability.Type.Passive_Manual)
            return ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "없음 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음";
        return "None";
    }

}
