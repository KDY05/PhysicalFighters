package io.github.kdy05.physicalFighters.game;

import io.github.kdy05.physicalFighters.BuildConfig;
import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
import io.github.kdy05.physicalFighters.util.TimerBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 게임 상태와 플레이어 관리를 담당하는 핵심 클래스
 */
public final class GameManager {
    // Constants
    private static final int COUNTDOWN_DURATION = 15;
    private static final int READY_DURATION = 9;
    private static final int WARNING_INTERVAL = 20;
    private static final int PROGRESS_INFO_INTERVAL = 600;
    private static final int MAX_TIMER_DURATION = 99999999;
    
    // Dependencies
    private final PhysicalFighters plugin;
    private final AbilityDistributor abilityDistributor = new AbilityDistributor();
    
    // Game state
    private ScriptStatus scenario = ScriptStatus.NoPlay;
    private final LinkedList<Player> exceptionList = new LinkedList<>();
    private final ArrayList<Player> playerList = new ArrayList<>();
    private final ArrayList<Player> okSign = new ArrayList<>();
    
    // Timers
    private final GameTimer gameReadyTimer = new GameTimer(TimerType.READY);
    private final GameTimer gameStartTimer = new GameTimer(TimerType.START);
    private final GameTimer gameProgressTimer = new GameTimer(TimerType.PROGRESS);
    private final GameTimer gameWarningTimer = new GameTimer(TimerType.WARNING);

    private enum TimerType {
        READY, START, PROGRESS, WARNING
    }

    public enum ScriptStatus {
        NoPlay, ScriptStart, AbilitySelect, GameStart
    }

    public GameManager(PhysicalFighters plugin) {
        this.plugin = plugin;
    }

    // =========================== Public API ===========================
    
    // State management
    public ScriptStatus getScenario() { return scenario; }
    public int getGameTime() { return gameProgressTimer.getCount(); }

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

    public void startGame() {
        gameStartTimer.startTimer(COUNTDOWN_DURATION, false);
    }

    public void forceGameStart() {
        okSign.clear();
        okSign.addAll(playerList);
        startGame();
    }

    public void stopGame() {
        scenario = ScriptStatus.NoPlay;
        gameReadyTimer.stopTimer();
        gameStartTimer.stopTimer();
        gameProgressTimer.stopTimer();
        gameWarningTimer.endTimer();
        plugin.getInvincibilityManager().forceStop();
        okSign.clear();
        playerList.clear();
    }

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
            if (abilityDistributor.assignRandomAbility(player, playerList.size())) {
                GameUtils.showInfo(player, plugin.getConfigManager().isAbilityOverLap());
                confirmPlayerAbility(player);
                checkAllPlayersConfirmed();
            } else {
                player.sendMessage(ChatColor.RED + "(!) 능력의 개수가 부족하여 재추첨이 불가합니다.");
            }
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
            startGame();
        }
    }

    private void initializePlayerList() {
        playerList.clear();
        okSign.clear();

        broadcastMessage(ChatColor.AQUA + "인식된 플레이어 목록");
        broadcastMessage(ChatColor.GOLD + "==========");

        int abilityCount = AbilityRegistry.AbilityList.size();
        int index = 0;
        int overflowCount = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (exceptionList.contains(player)) continue;

            if (index < abilityCount) {
                playerList.add(player);
                broadcastMessage(String.format(ChatColor.GREEN + "%d. " + ChatColor.WHITE + "%s",
                        index, player.getName()));
            } else {
                broadcastMessage(String.format(ChatColor.RED + "%d. %s (Error)", index, player.getName()));
                overflowCount++;
            }
            index++;
        }

        int totalValidPlayers = playerList.size() + overflowCount;
        if (overflowCount == 0) {
            broadcastMessage(String.format(ChatColor.YELLOW + "총 인원수 : %d명", totalValidPlayers));
        } else {
            broadcastMessage(String.format(ChatColor.RED + "총 인원수 : %d명", totalValidPlayers));
            broadcastMessage("인원이 능력의 개수보다 많습니다. 에러 처리된 분들은 능력을");
            broadcastMessage("받을 수 없으며 모든 게임 진행 대상에서 제외됩니다.");
        }

        broadcastMessage(ChatColor.GOLD + "==========");

        if (playerList.isEmpty()) {
            broadcastMessage(ChatColor.RED + "경고, 실질 플레이어가 없습니다. 게임 강제 종료.");
            scenario = ScriptStatus.NoPlay;
            gameReadyTimer.stopTimer();
            broadcastMessage(ChatColor.GRAY + "모든 설정이 취소됩니다.");
        }
    }

    private void showGameInfo(boolean full) {
        broadcastMessage(ChatColor.DARK_RED + "Physical Fighters");
        broadcastMessage(String.format(ChatColor.GRAY + "VER. %d", BuildConfig.BUILD_NUMBER));
        if (full) {
            broadcastMessage(ChatColor.GREEN + "제작: " + ChatColor.WHITE + "염료");
            broadcastMessage(ChatColor.GREEN + "원작(VisualAbility): " + ChatColor.WHITE + "제온");
            broadcastMessage(ChatColor.AQUA + "업데이트: " + ChatColor.WHITE + "어라랍");
            broadcastMessage("원작자 카페: https://cafe.naver.com/craftproducer");
            broadcastMessage("공식 배포처: https://github.com/KDY05/PhysicalFighters");
        }
    }

    private void handleAbilitySetup() {
        if (!plugin.getConfigManager().isNoAbilitySetting()) {
            broadcastMessage(ChatColor.GRAY + "능력 설정 초기화 및 추첨 준비...");
            abilityDistributor.resetAllAbilities();
        } else {
            broadcastMessage(ChatColor.GOLD + "능력을 추첨하지 않습니다.");
            broadcastMessage("시작전에 능력이 이미 부여되었다면 보존됩니다.");
            okSign.clear();
            okSign.addAll(playerList);
            abilityDistributor.enableAllAbilities();
            startGame();
        }
    }

    private void processAbilitySelection() {
        scenario = ScriptStatus.AbilitySelect;

        if (playerList.size() < AbilityRegistry.AbilityList.size()) {
            distributeAbilitiesWithChoice();
            gameWarningTimer.startTimer(MAX_TIMER_DURATION, false);
        } else {
            distributeAbilitiesInstantly();
            startGame();
        }
    }

    private void distributeAbilitiesWithChoice() {
        for (Player player : playerList) {
            if (!abilityDistributor.assignRandomAbility(player, playerList.size())) {
                player.sendMessage(ChatColor.RED + "경고, 능력의 개수가 부족합니다.");
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
        broadcastMessage(ChatColor.AQUA + "능력 개수보다 플레이어 수가 같거나 많으므로 즉시 확정됩니다.");
        for (Player player : playerList) {
            if (!abilityDistributor.assignRandomAbility(player, playerList.size())) {
                player.sendMessage(ChatColor.RED + "경고, 능력의 개수가 부족합니다.");
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
        plugin.getInvincibilityManager().startInvincibility(plugin.getConfigManager().getEarlyInvincibleTime());
        setPlayerBase();
        abilityDistributor.enableAllAbilities();
        gameProgressTimer.startTimer(MAX_TIMER_DURATION, false);
    }

    private void setPlayerBase() {
        for (Player player : playerList) {
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(10.0f);
            player.setExhaustion(0.0f);
            player.setLevel(plugin.getConfigManager().getSetLev());
            if (plugin.getConfigManager().isClearInventory()) {
                player.getInventory().clear();
            }
            plugin.getBaseKitManager().giveBasicItems(player);
        }
    }

    private void showWarningMessage() {
        for (Player player : playerList) {
            if (!okSign.contains(player)) {
                broadcastMessage(ChatColor.YELLOW + player.getName() + "님의 능력이 확정되지 않았습니다.");
            }
        }
        broadcastMessage(ChatColor.RED + "경고, 게임이 올바르게 시작되지 않았습니다.");
        broadcastMessage(ChatColor.RED + "/va yes나 /va no 명령으로 능력을 확정하세요.");
    }

    private void showPeriodicInfo(int count) {
        if (count > 0 && count % PROGRESS_INFO_INTERVAL == 0) {
            showGameInfo(false);
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
            if (type == TimerType.READY) {
                handleReadyTimer(count);
            } else if (type == TimerType.START) {
                handleStartTimer(count);
            } else if (type == TimerType.PROGRESS) {
                handleProgressTimer(count);
            } else if (type == TimerType.WARNING) {
                handleWarningTimer(count);
            }
        }

        @Override
        public void onTimerEnd() {}

        private void handleReadyTimer(int count) {
            if (count == 0) {
                initializePlayerList();
            } else if (count == 3) {
                showGameInfo(true);
            } else if (count == 7) {
                handleAbilitySetup();
            } else if (count == 9) {
                processAbilitySelection();
            }
        }

        private void handleStartTimer(int count) {
            if (count == 0) {
                gameWarningTimer.endTimer();
            } else if (count == 3) {
                broadcastMessage(ChatColor.WHITE + "모든 플레이어들의 능력을 확정했습니다.");
            } else if (count == 5) {
                broadcastMessage(ChatColor.YELLOW + "잠시 후 게임이 시작됩니다.");
            } else if (count >= 10 && count <= 14) {
                broadcastMessage(ChatColor.GOLD + String.format("%d초 전", 15 - count));
            } else if (count == 15) {
                startGameLogic();
            }
        }

        private void handleProgressTimer(int count) {
            showPeriodicInfo(count);
        }

        private void handleWarningTimer(int count) {
            if (count > 0 && count % WARNING_INTERVAL == 0) {
                showWarningMessage();
            }
        }
    }
}