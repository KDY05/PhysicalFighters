package io.github.kdy05.physicalFighters.core;

import io.github.kdy05.physicalFighters.utils.AbilityInitializer;
import io.github.kdy05.physicalFighters.utils.TimerBase;
import io.github.kdy05.physicalFighters.PhysicalFighters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 게임 상태와 플레이어 관리를 담당하는 핵심 클래스
 */
public class GameManager {
    
    // Constants
    private static final int COUNTDOWN_DURATION = 15;
    private static final int READY_DURATION = 9;
    private static final int WARNING_INTERVAL = 20;
    private static final int PROGRESS_INFO_INTERVAL = 600;
    private static final int MAX_TIMER_DURATION = 99999999;
    
    // Dependencies
    private final PhysicalFighters plugin;
    private final Random random = new Random();
    
    // Game state
    private static ScriptStatus scenario = ScriptStatus.NoPlay;
    private final LinkedList<Player> exceptionList = new LinkedList<>();
    private static final ArrayList<Player> playerList = new ArrayList<>();
    private final ArrayList<Player> okSign = new ArrayList<>();
    private int peopleCount = 0;
    
    // Timers
    private final GameTimer gameReadyTimer = new GameTimer(TimerType.READY);
    private final GameTimer gameStartTimer = new GameTimer(TimerType.START);
    private final GameTimer gameProgressTimer = new GameTimer(TimerType.PROGRESS);
    private final GameTimer gameWarningTimer = new GameTimer(TimerType.WARNING);

    public enum ScriptStatus {
        NoPlay, ScriptStart, AbilitySelect, GameStart
    }
    
    private enum TimerType {
        READY, START, PROGRESS, WARNING
    }

    public GameManager(PhysicalFighters plugin) {
        this.plugin = plugin;
    }

    // =========================== Public API ===========================
    
    // State management
    public static ScriptStatus getScenario() { return scenario; }
    public static void setScenario(ScriptStatus scenario) { GameManager.scenario = scenario; }
    public static ArrayList<Player> getPlayerList() { return playerList; }
    public LinkedList<Player> getExceptionList() { return exceptionList; }
    public ArrayList<Player> getOKSign() { return okSign; }

    // Game flow control
    public void gameReady(CommandSender sender) {
        if (scenario != ScriptStatus.NoPlay) {
            sender.sendMessage(ChatColor.RED + "(!) 이미 게임이 시작되었습니다.");
            return;
        }
        scenario = ScriptStatus.ScriptStart;
        broadcastMessage(ChatColor.YELLOW + "(!) 잠시 후 게임을 시작합니다.");
        gameReadyTimer.startTimer(READY_DURATION, false);
    }

    public void gameStart() {
        gameStartTimer.startTimer(COUNTDOWN_DURATION, false);
    }

    public void gameProgress() {
        gameProgressTimer.startTimer(MAX_TIMER_DURATION, false);
    }

    public void gameWarningStart() {
        gameWarningTimer.startTimer(MAX_TIMER_DURATION, false);
    }

    // Stop methods
    public void gameReadyStop() { gameReadyTimer.stopTimer(); }
    public void gameStartStop() { gameStartTimer.stopTimer(); }
    public void gameProgressStop() { gameProgressTimer.stopTimer(); }
    public void gameWarningStop() { gameWarningTimer.endTimer(); }

    // Player actions
    public void handleObserve(Player player) {
        if (scenario != ScriptStatus.NoPlay) {
            player.sendMessage(ChatColor.RED + "게임 시작 이후는 옵저버 처리가 불가능합니다.");
            return;
        }
        if (exceptionList.contains(player)) {
            playerList.add(player);
            exceptionList.remove(player);
            player.sendMessage(ChatColor.GREEN + "게임 예외 처리가 해제되었습니다.");
        } else {
            exceptionList.add(player);
            playerList.remove(player);
            player.sendMessage(ChatColor.GOLD + "게임 예외 처리가 완료되었습니다.");
            player.sendMessage(ChatColor.GREEN + "/va ob을 다시 사용하시면 해제됩니다.");
        }
    }

    public void handleYes(Player player) {
        if (isValidAbilitySelection(player)) {
            confirmPlayerAbility(player);
            checkAllPlayersConfirmed();
        }
    }

    public void handleNo(Player player) {
        if (isValidAbilitySelection(player)) {
            if (assignRandomAbility(player) == null) {
                player.sendMessage(ChatColor.RED + "(!) 능력의 갯수가 부족하여 재추첨이 불가합니다.");
                return;
            }
            plugin.getGameCommand().handleCheck(player);
            confirmPlayerAbility(player);
            checkAllPlayersConfirmed();
        }
    }

    // =========================== Private Helper Methods ===========================
    
    private boolean isValidAbilitySelection(Player player) {
        return scenario == ScriptStatus.AbilitySelect && 
               !exceptionList.contains(player) && 
               !okSign.contains(player);
    }
    
    private void confirmPlayerAbility(Player player) {
        okSign.add(player);
        player.sendMessage(ChatColor.GOLD + "능력이 확정되었습니다. 다른 플레이어를 기다려주세요.");
        broadcastMessage(String.format(ChatColor.YELLOW + "%s" + ChatColor.WHITE + "님이 능력을 확정했습니다.", 
                         player.getName()));
        broadcastMessage(String.format(ChatColor.GREEN + "남은 인원 : " + ChatColor.WHITE + "%d명", 
                         playerList.size() - okSign.size()));
    }
    
    private void checkAllPlayersConfirmed() {
        if (okSign.size() == playerList.size()) {
            gameStart();
        }
    }

    private void initializePlayerList() {
        broadcastMessage(ChatColor.AQUA + "인식된 플레이어 목록");
        broadcastMessage(ChatColor.GOLD + "==========");
        
        Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        int validPlayerIndex = 0;
        
        for (Player player : onlinePlayers) {
            if (exceptionList.contains(player)) continue;
            
            if (validPlayerIndex < Ability.getAbilityCount()) {
                playerList.add(player);
                broadcastMessage(String.format(ChatColor.GREEN + "%d. " + ChatColor.WHITE + "%s",
                               validPlayerIndex, player.getName()));
            } else {
                broadcastMessage(String.format(ChatColor.RED + "%d. %s (Error)",
                               validPlayerIndex, player.getName()));
            }
            validPlayerIndex++;
        }
        
        peopleCount = onlinePlayers.length - exceptionList.size();
        
        if (peopleCount <= Ability.getAbilityCount()) {
            broadcastMessage(String.format(ChatColor.YELLOW + "총 인원수 : %d명", peopleCount));
        } else {
            broadcastMessage(String.format(ChatColor.RED + "총 인원수 : %d명", peopleCount));
            broadcastMessage("인원이 능력의 개수보다 많습니다. 에러 처리된 분들은 능력을");
            broadcastMessage("받을 수 없으며 모든 게임 진행 대상에서 제외됩니다.");
        }
        
        broadcastMessage(ChatColor.GOLD + "==========");
        
        if (playerList.isEmpty()) {
            broadcastMessage(ChatColor.RED + "경고, 실질 플레이어가 없습니다. 게임 강제 종료.");
            scenario = ScriptStatus.NoPlay;
            gameReadyTimer.stopTimer();
            broadcastMessage(ChatColor.GRAY + "모든 설정이 취소됩니다.");
            playerList.clear();
        }
    }

    private void showGameInfo() {
        broadcastMessage(ChatColor.DARK_PURPLE + "Physical Fighters");
        broadcastMessage(String.format(ChatColor.GRAY + "VER. %d", PhysicalFighters.BuildNumber));
        broadcastMessage(ChatColor.GREEN + "제작 : " + ChatColor.WHITE + "염료");
        broadcastMessage(ChatColor.GREEN + "원작 : " + ChatColor.WHITE + "제온(VisualAbility)");
        broadcastMessage(ChatColor.AQUA + "업데이트 : " + ChatColor.WHITE + "어라랍");
    }

    private void handleAbilitySetup() {
        if (!ConfigManager.NoAbilitySetting) {
            broadcastMessage(ChatColor.GRAY + "능력 설정 초기화 및 추첨 준비...");
            resetAllAbilities();
        } else {
            broadcastMessage(ChatColor.GOLD + "능력을 추첨하지 않습니다.");
            broadcastMessage("시작전에 능력이 이미 부여되었다면 보존됩니다.");
            okSign.clear();
            okSign.addAll(playerList);
            enableAllAbilities();
            gameStart();
        }
    }

    private void processAbilitySelection() {
        scenario = ScriptStatus.AbilitySelect;
        
        if (peopleCount < Ability.getAbilityCount()) {
            distributeAbilitiesWithChoice();
            gameWarningStart();
        } else {
            distributeAbilitiesInstantly();
            gameStart();
        }
    }

    private void distributeAbilitiesWithChoice() {
        for (Player player : playerList) {
            if (assignRandomAbility(player) == null) {
                player.sendMessage(ChatColor.RED + "경고, 능력의 갯수가 부족합니다.");
            } else {
                player.sendMessage(ChatColor.YELLOW + "(!) /va check " + ChatColor.WHITE + "= 능력 확인");
                player.sendMessage(ChatColor.YELLOW + "(!) /va yes " + ChatColor.WHITE + "= 능력 사용.");
                player.sendMessage(ChatColor.YELLOW + "(!) /va no " + ChatColor.WHITE + "= 능력 재추첨.(1회)");
            }
        }
        for (Player player : exceptionList) {
            player.sendMessage(ChatColor.GREEN + "능력 추첨중입니다");
        }
    }

    private void distributeAbilitiesInstantly() {
        broadcastMessage(ChatColor.AQUA + "능력 갯수보다 플레이어 수가 같거나 많으므로 즉시 확정됩니다.");
        for (Player player : playerList) {
            if (assignRandomAbility(player) == null) {
                player.sendMessage(ChatColor.RED + "경고, 능력의 갯수가 부족합니다.");
            } else {
                okSign.add(player);
                player.sendMessage(ChatColor.GREEN + "당신에게 능력이 부여되었습니다. " + 
                                 ChatColor.YELLOW + "/va check" + ChatColor.WHITE + "로 확인하세요.");
            }
        }
        for (Player player : exceptionList) {
            player.sendMessage(ChatColor.GREEN + "능력 추첨 완료");
        }
    }

    private void startGameLogic() {
        broadcastMessage(ChatColor.GREEN + "게임이 시작되었습니다.");
        logPlayerAbilities();
        setupInvincibility();
        setPlayerBase();
        enableAllAbilities();
        gameProgress();
    }

    private void logPlayerAbilities() {
        plugin.getLogger().info("플레이어들의 능력");
        int count = 0;
        for (Ability ability : AbilityInitializer.AbilityList) {
            if (ability.getPlayer() != null) {
                plugin.getLogger().info(String.format("%d. %s - %s",
                        count, ability.getPlayer().getName(), ability.getAbilityName()));
                count++;
            }
        }
        plugin.getLogger().info("-------------------------");
    }

    private void setupInvincibility() {
        if (ConfigManager.EarlyInvincibleTime != 0) {
            broadcastMessage("시작 직후 " + ConfigManager.EarlyInvincibleTime + "분간은 무적입니다.");
            ConfigManager.DamageGuard = true;
        } else {
            broadcastMessage(ChatColor.RED + "초반 무적은 작동하지 않습니다.");
        }
    }

    private void setPlayerBase() {
        for (Player player : playerList) {
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(10.0f);
            player.setExhaustion(0.0f);
            player.setLevel(ConfigManager.Setlev);
            if (ConfigManager.ClearInventory) {
                player.getInventory().clear();
            }
            // TODO: 기본템 로직 여기에 넣기
        }
    }

    private Ability assignRandomAbility(Player player) {
        // Remove current ability
        for (Ability ability : AbilityInitializer.AbilityList) {
            if (ability.isOwner(player)) {
                ability.setPlayer(null, false);
                break;
            }
        }

        List<Ability> availableAbilities = getAvailableAbilities();
        if (availableAbilities.isEmpty()) return null;
        
        Ability selectedAbility = availableAbilities.get(random.nextInt(availableAbilities.size()));
        selectedAbility.setPlayer(player, false);
        return selectedAbility;
    }

    private List<Ability> getAvailableAbilities() {
        List<Ability> available = new ArrayList<>();
        for (Ability ability : AbilityInitializer.AbilityList) {
            if (ability.getPlayer() == null && 
                (playerList.size() > 6 || ability != AbilityInitializer.mirroring)) {
                available.add(ability);
            }
        }
        return available;
    }

    private void resetAllAbilities() {
        for (Ability ability : AbilityInitializer.AbilityList) {
            ability.setRunAbility(false);
            ability.setPlayer(null, false);
        }
    }

    private void enableAllAbilities() {
        for (Ability ability : AbilityInitializer.AbilityList) {
            ability.setRunAbility(true);
            ability.setPlayer(ability.getPlayer(), false);
        }
    }

    private void handleInvincibilityCountdown(int count) {
        if (ConfigManager.EarlyInvincibleTime == 0) return;
        
        int remainingSeconds = ConfigManager.EarlyInvincibleTime * 60 - count;
        
        if (remainingSeconds == 0) {
            broadcastMessage(ChatColor.GREEN + "초반 무적이 해제되었습니다. 이제 데미지를 입습니다.");
            ConfigManager.DamageGuard = false;
        } else if (remainingSeconds <= 5 && remainingSeconds >= 1) {
            broadcastMessage(String.format(ChatColor.YELLOW + "%d초 후" + ChatColor.WHITE + " 초반무적이 해제됩니다.", 
                           remainingSeconds));
        } else if (remainingSeconds == 60) {
            broadcastMessage(ChatColor.YELLOW + "초반 무적이 " + ChatColor.WHITE + "1분 후 해제됩니다.");
        }
    }

    private void showWarningMessage() {
        broadcastMessage(ChatColor.RED + "경고, 게임이 올바르게 시작되지 않았습니다.");
        broadcastMessage(ChatColor.RED + "/va yes나 /va no 명령으로 능력을 확정하세요.");
        for (Player player : playerList) {
            if (!okSign.contains(player)) {
                broadcastMessage(ChatColor.YELLOW + player.getName() + "님의 능력이 확정되지 않았습니다.");
            }
        }
    }

    private void showPeriodicInfo(int count) {
        if (count > 0 && count % PROGRESS_INFO_INTERVAL == 0) {
            showGameInfo();
        }
    }

    private void broadcastMessage(String message) {
        Bukkit.broadcastMessage(message);
    }

    // =========================== Timer Class ===========================
    
    private final class GameTimer extends TimerBase {
        private final TimerType type;

        public GameTimer(TimerType type) {
            this.type = type;
        }

        @Override
        public void onTimerStart() {
            if (type == TimerType.START) {
                scenario = ScriptStatus.GameStart;
            }
        }

        @Override
        public void onTimerRunning(int count) {
            switch (type) {
                case READY -> handleReadyTimer(count);
                case START -> handleStartTimer(count);
                case PROGRESS -> handleProgressTimer(count);
                case WARNING -> handleWarningTimer(count);
            }
        }

        @Override
        public void onTimerEnd() {}

        private void handleReadyTimer(int count) {
            switch (count) {
                case 0 -> initializePlayerList();
                case 3 -> showGameInfo();
                case 7 -> handleAbilitySetup();
                case 9 -> processAbilitySelection();
            }
        }

        private void handleStartTimer(int count) {
            switch (count) {
                case 0 -> gameWarningStop();
                case 3 -> broadcastMessage(ChatColor.WHITE + "모든 플레이어들의 능력을 확정했습니다.");
                case 5 -> broadcastMessage(ChatColor.YELLOW + "잠시 후 게임이 시작됩니다.");
                case 10 -> broadcastMessage(ChatColor.GOLD + "5초 전");
                case 11 -> broadcastMessage(ChatColor.GOLD + "4초 전");
                case 12 -> broadcastMessage(ChatColor.GOLD + "3초 전");
                case 13 -> broadcastMessage(ChatColor.GOLD + "2초 전");
                case 14 -> broadcastMessage(ChatColor.GOLD + "1초 전");
                case 15 -> startGameLogic();
            }
        }

        private void handleProgressTimer(int count) {
            showPeriodicInfo(count);
            handleInvincibilityCountdown(count);
        }

        private void handleWarningTimer(int count) {
            if (count > 0 && count % WARNING_INTERVAL == 0) {
                showWarningMessage();
            }
        }
    }
}