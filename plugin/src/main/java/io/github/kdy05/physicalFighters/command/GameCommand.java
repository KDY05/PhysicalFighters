package io.github.kdy05.physicalFighters.command;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.util.AbilityInitializer;
import io.github.kdy05.physicalFighters.core.GameManager;
import io.github.kdy05.physicalFighters.util.AbilityUtils;
import io.github.kdy05.physicalFighters.PhysicalFighters;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.kdy05.physicalFighters.util.CommandInterface;

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
            if (!requirePlayer(sender)) return true;
            this.gameManager.handleYes((Player) sender);
            return true;
        } else if (args[0].equalsIgnoreCase("no")) {
            if (!requirePlayer(sender)) return true;
            this.gameManager.handleNo((Player) sender);
            return true;
        }

        // 운영자 권한 필터
        if (!sender.hasPermission("va.operate")) {
            return false;
        }

        // 운영자 커맨드
        if (args[0].equalsIgnoreCase("start")) {
            this.gameManager.gameReady(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("stop")) {
            handleStop(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("skip")) {
            handleSkip(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("ob")) {
            if (!requirePlayer(sender)) return true;
            this.gameManager.handleObserve((Player) sender);
            return true;
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
        if (!requirePlayer(sender)) {
            return;
        }
        AbilityUtils.showInfo((Player) sender);
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
            sender.sendMessage(ChatColor.RED + "명령이 올바르지 않습니다. [/va ablist [페이지번호]]");
            return;
        }
        
        int page;
        try {
            page = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "페이지가 올바르지 않습니다.");
            return;
        }
        
        final int ITEMS_PER_PAGE = 8;
        final int totalAbilities = AbilityInitializer.AbilityList.size();
        final int maxPage = (totalAbilities - 1) / ITEMS_PER_PAGE;
        
        if (page < 0 || page > maxPage) {
            sender.sendMessage(ChatColor.RED + String.format("페이지 범위를 벗어났습니다. (0~%d)", maxPage));
            return;
        }
        
        sender.sendMessage(ChatColor.GOLD + "==== 능력 목록 및 코드 ====");
        sender.sendMessage(String.format(ChatColor.AQUA + "페이지 %d/%d (총 %d개 능력)", page, maxPage, totalAbilities));
        
        final int startIndex = page * ITEMS_PER_PAGE;
        final int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalAbilities);
        
        for (int code = startIndex; code < endIndex; code++) {
            Ability ability = AbilityInitializer.AbilityList.get(code);
            sender.sendMessage(String.format(
                    ChatColor.GREEN + "[%d] " + ChatColor.WHITE + "%s " + ChatColor.GRAY + "%s",
                    code, ability.getAbilityName(), ability.getRank()));
        }
        
        if (totalAbilities == 0) {
            sender.sendMessage(ChatColor.YELLOW + "등록된 능력이 없습니다.");
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

        AbilityUtils.assignAbility(sender, abicode, target);
        plugin.getLogger().info("명령어에 의한 능력 할당입니다.");
    }

}
