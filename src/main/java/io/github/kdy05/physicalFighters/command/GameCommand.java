package io.github.kdy05.physicalFighters.command;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.utils.AbilityInitializer;
import io.github.kdy05.physicalFighters.core.GameManager;
import io.github.kdy05.physicalFighters.utils.AbilityUtils;
import io.github.kdy05.physicalFighters.PhysicalFighters;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.kdy05.physicalFighters.utils.CommandInterface;

public class GameCommand implements CommandInterface {

    private final PhysicalFighters plugin;
    private final GameManager gameManager;

    public GameCommand(PhysicalFighters plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public boolean onCommandEvent(CommandSender sender, Command command, String label, String[] args) {
        // 도움말 커맨드
        if (args[0].equalsIgnoreCase("help")) {
            handleHelp(sender);
            return true;
        }
        
        // 유저 커맨드
        if (args[0].equalsIgnoreCase("check")) {
            handleCheck(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("yes")) {
            if (sender instanceof Player p) {
                this.gameManager.handleYes(p);
                return true;
            }
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.");
        } else if (args[0].equalsIgnoreCase("no")) {
            if (sender instanceof Player p) {
                this.gameManager.handleNo(p);
                return true;
            }
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.");
        }

        // 운영자 권한 필터
        if (!sender.hasPermission("va.operate")) {
            return false;
        }

        // 운영자 커맨드
        if (args[0].equalsIgnoreCase("start")) {
            if (sender instanceof Player p) {
                this.gameManager.gameReady(p);
                return true;
            }
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.");
        } else if (args[0].equalsIgnoreCase("stop")) {
            handleStop(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("skip")) {
            handleSkip(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("ob")) {
            if (sender instanceof Player p) {
                this.gameManager.handleObserve(p);
                return true;
            }
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.");
        } else if (args[0].equalsIgnoreCase("ablist")) {
            handleAblist(sender, args);
            return true;
        } else if (args[0].equalsIgnoreCase("abi")) {
            handleAbi(sender, args);
            return true;
        }

        return false;
    }

    private void handleHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "=== PhysicalFighters 명령어 목록 ===");
        sender.sendMessage("");
        
        // 기본 명령어들
        sender.sendMessage(ChatColor.YELLOW + "■ 기본 명령어");
        sender.sendMessage(ChatColor.GOLD + "/va help" + ChatColor.WHITE + " - 이 도움말을 표시합니다.");
        sender.sendMessage(ChatColor.GOLD + "/va check" + ChatColor.WHITE + " - 자신의 능력 정보를 확인합니다.");
        sender.sendMessage(ChatColor.GOLD + "/va yes" + ChatColor.WHITE + " - 능력 선택을 확정합니다.");
        sender.sendMessage(ChatColor.GOLD + "/va no" + ChatColor.WHITE + " - 능력 선택을 거부합니다.");
        sender.sendMessage("");
        
        // 운영자 명령어들 (권한이 있을 때만 표시)
        if (sender.hasPermission("va.operate")) {
            sender.sendMessage(ChatColor.YELLOW + "■ 게임 관리 명령어");
            sender.sendMessage(ChatColor.GOLD + "/va start" + ChatColor.WHITE + " - 게임을 시작합니다.");
            sender.sendMessage(ChatColor.GOLD + "/va stop" + ChatColor.WHITE + " - 게임을 중지합니다.");
            sender.sendMessage(ChatColor.GOLD + "/va skip" + ChatColor.WHITE + " - 능력 선택을 강제로 확정시킵니다.");
            sender.sendMessage(ChatColor.GOLD + "/va ob" + ChatColor.WHITE + " - 옵저버 설정을 합니다.");
            sender.sendMessage("");
            
            sender.sendMessage(ChatColor.YELLOW + "■ 능력 관리 명령어");
            sender.sendMessage(ChatColor.GOLD + "/va ablist [페이지]" + ChatColor.WHITE + " - 능력 목록과 코드를 표시합니다.");
            sender.sendMessage(ChatColor.GOLD + "/va abi [플레이어] [코드]" + ChatColor.WHITE + " - 플레이어에게 능력을 할당합니다.");
            sender.sendMessage("");
            
            sender.sendMessage(ChatColor.YELLOW + "■ 유틸리티 명령어");
            sender.sendMessage(ChatColor.GOLD + "/va util" + ChatColor.WHITE + " - 유틸리티 명령어 목록을 표시합니다.");
        }
        
        sender.sendMessage(ChatColor.GREEN + "================================");
    }

    public void handleCheck(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.");
            return;
        }
        Ability ability;
        if (AbilityInitializer.assimilation.getPlayer() == player) {
            ability = AbilityInitializer.assimilation;
        } else {
            ability = AbilityUtils.findAbility(player);
        }
        if (ability == null) {
            player.sendMessage(ChatColor.RED + "능력이 없거나 옵저버입니다.");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "---------------");
        player.sendMessage(ChatColor.GOLD + "- 능력 정보 -");
        if (ConfigManager.AbilityOverLap)
            player.sendMessage(ChatColor.DARK_AQUA + "참고 : 능력 리스트중 가장 상단의 능력만 보여줍니다.");
        player.sendMessage(ChatColor.AQUA + ability.getAbilityName() + ChatColor.WHITE
                + " [" + getTypeText(ability) + "] " + ability.getRank());
        for (int l = 0; l < ability.getGuide().length; l++) {
            player.sendMessage(ability.getGuide()[l]);
        }
        player.sendMessage(getTimerText(ability));
        player.sendMessage(ChatColor.GREEN + "---------------");
    }

    public String getTypeText(Ability ability) {
        Ability.Type type = ability.getAbilityType();
        return switch (type) {
            case Active_Continue ->
                    ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "지속" + ChatColor.WHITE;
            case Active_Immediately ->
                    ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "즉발" + ChatColor.WHITE;
            case Passive_AutoMatic ->
                    ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "자동" + ChatColor.WHITE;
            case Passive_Manual ->
                    ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "수동" + ChatColor.WHITE;
            case null -> "Unknown";
        };
    }

    private String getTimerText(Ability ability) {
        return switch (ability.getAbilityType()) {
            case Active_Continue -> String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "%d초", ability.getCoolDown(), ability.getDuration());
            case Active_Immediately -> String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음", ability.getCoolDown());
            case Passive_AutoMatic, Passive_Manual -> ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "없음 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음";
            case null -> "None";
        };
    }

    private void handleStop(CommandSender sender) {
        if (GameManager.getScenario() == GameManager.ScriptStatus.NoPlay) {
            sender.sendMessage(ChatColor.RED + "아직 게임을 시작하지 않았습니다.");
            return;
        }
        GameManager.setScenario(GameManager.ScriptStatus.NoPlay);
        this.gameManager.gameReadyStop();
        this.gameManager.gameStartStop();
        this.gameManager.gameProgressStop();
        this.gameManager.gameWarningStop();
        this.gameManager.getOKSign().clear();
        ConfigManager.DamageGuard = false;
        for (int l = 0; l < AbilityInitializer.AbilityList.size(); l++) {
            AbilityInitializer.AbilityList.get(l).cancelDTimer();
            AbilityInitializer.AbilityList.get(l).cancelCTimer();
            AbilityInitializer.AbilityList.get(l).setRunAbility(false);
            AbilityInitializer.AbilityList.get(l).setPlayer(null, false);
        }
        GameManager.getPlayerList().clear();
        Bukkit.broadcastMessage(ChatColor.GRAY + "------------------------------");
        Bukkit.broadcastMessage(String.format(ChatColor.YELLOW +
                "관리자 %s님이 게임 카운터를 중단시켰습니다.", sender.getName()));
        Bukkit.broadcastMessage(ChatColor.GRAY + "모든 설정이 취소됩니다.");
        Bukkit.broadcastMessage(ChatColor.GRAY + "옵저버 설정은 초기화 되지 않습니다.");
    }

    private void handleSkip(CommandSender sender) {
        if (GameManager.getScenario() == GameManager.ScriptStatus.AbilitySelect) {
            Bukkit.broadcastMessage(String.format(ChatColor.GRAY +
                    "관리자 %s님이 능력을 강제로 확정시켰습니다.", sender.getName()));
            this.gameManager.getOKSign().clear();
            this.gameManager.getOKSign().addAll(GameManager.getPlayerList());
            this.gameManager.gameStart();
        } else {
            sender.sendMessage(ChatColor.RED + "능력 추첨중이 아닙니다.");
        }
    }

    private void handleAblist(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "명령이 올바르지 않습니다. [/va ablist [0~10]");
            return;
        }
        try {
            int page = Integer.parseInt(args[1]);
            if (page < 0) {
                sender.sendMessage(ChatColor.RED + "페이지가 올바르지 않습니다.");
                return;
            }
            sender.sendMessage(ChatColor.GOLD + "==== 능력 목록 및 코드 ====");
            sender.sendMessage(String.format(ChatColor.AQUA + "페이지 %d...[0~10]", page));
            for (int code = page * 8; code < (page + 1) * 8; code++) {
                if (code < AbilityInitializer.AbilityList.size()) {
                    Ability ability = AbilityInitializer.AbilityList.get(code);
                    sender.sendMessage(String.format(
                            ChatColor.GREEN + "[%d] " + ChatColor.WHITE + "%s",
                            code, ability.getAbilityName()));
                }
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "페이지가 올바르지 않습니다.");
        }
    }

    private void handleAbi(CommandSender sender, String[] d) {
        // 예외 처리
        if (d.length != 3) {
            sender.sendMessage(ChatColor.RED + "명령이 올바르지 않습니다. [/va abi [플레이어] [명령코드]]");
            return;
        }

        Player target = Bukkit.getServer().getPlayerExact(d[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "존재하지 않는 플레이어입니다.");
            return;
        }

        int abicode;
        try {
            abicode = Integer.parseInt(d[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
            return;
        }
        if (abicode < -1 || abicode > AbilityInitializer.AbilityList.size() - 1) {
            sender.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
            return;
        }

        assignAbility(sender, abicode, target);
    }

    public void assignAbility(CommandSender sender, int abicode, Player target) {
        // 특정 플레이어 능력 해제
        if (abicode == -1) {
            for (Ability ability : AbilityInitializer.AbilityList) {
                if (ability.isOwner(target)) {
                    ability.setPlayer(null, true);
                }
            }
            target.sendMessage(ChatColor.RED + "당신의 능력이 모두 해제되었습니다.");
            sender.sendMessage(String.format(ChatColor.GREEN + "%s" +
                    ChatColor.WHITE + "님의 능력을 모두 해제했습니다.", target.getName()));
            return;
        }

        // 기존 능력 해제
        Ability ability = AbilityInitializer.AbilityList.get(abicode);
        if (ConfigManager.AbilityOverLap) {
            // 중복 모드에서 액티브 능력 중복은 불가함.
            if (ability.getAbilityType() == Ability.Type.Active_Continue ||
                    ability.getAbilityType() == Ability.Type.Active_Immediately) {
                for (Ability ab : AbilityInitializer.AbilityList) {
                    if ((ab.getAbilityType() == Ability.Type.Active_Continue || ab.getAbilityType() == Ability.Type.Active_Immediately)
                            && ab.isOwner(target)) {
                        ab.setPlayer(null, true);
                    }
                }
            }
        } else {
            for (Ability ab : AbilityInitializer.AbilityList) {
                if (ab.isOwner(target)) {
                    ab.setPlayer(null, true);
                }
            }
        }

        // 새로운 능력 적용
        ability.setPlayer(target, true);
        ability.setRunAbility(true);
        sender.sendMessage(String.format(ChatColor.GREEN + "%s" + ChatColor.WHITE + "님에게 " +
                            ChatColor.GREEN + "%s" + ChatColor.WHITE + " 능력 할당이 완료되었습니다.",
                            Objects.requireNonNull(target).getName(), ability.getAbilityName()));
        String senderName = sender instanceof Player ? sender.getName() : "Console";
        plugin.getLogger().info(String.format("%s님이 %s님에게 %s 능력을 할당했습니다.",
                senderName, target.getName(), ability.getAbilityName()));
    }

}
