package io.github.kdy05.physicalFighters.scripts;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.AbilityList;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.utils.TimerBase;
import io.github.kdy05.physicalFighters.PhysicalFighters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    private final MainScripter mainScripter;
    private final PhysicalFighters plugin;
    private final Random random = new Random();
    
    // Game state
    private static ScriptStatus scenario = ScriptStatus.NoPlay;
    private final LinkedList<Player> exceptionList = new LinkedList<>();
    private static final ArrayList<Player> playerList = new ArrayList<>();
    private final ArrayList<Player> okSign = new ArrayList<>();
    private World gameWorld;
    private int peopleCount = 0;
    
    // Timers
    private final GameTimer gameReadyTimer = new GameTimer(TimerType.READY);
    private final GameTimer gameStartTimer = new GameTimer(TimerType.START);
    private final GameTimer gameProgressTimer = new GameTimer(TimerType.PROGRESS);
    private final GameTimer gameWarningTimer = new GameTimer(TimerType.WARNING);
    
    public static int PlayDistanceBuffer = 0;

    public enum ScriptStatus {
        NoPlay, ScriptStart, AbilitySelect, GameStart
    }
    
    private enum TimerType {
        READY, START, PROGRESS, WARNING
    }

    public GameManager(MainScripter mainScripter, PhysicalFighters plugin) {
        this.mainScripter = mainScripter;
        this.plugin = plugin;
    }

    // =========================== Public API ===========================
    
    // State management
    public static ScriptStatus getScenario() { return scenario; }
    public static void setScenario(ScriptStatus scenario) { GameManager.scenario = scenario; }
    public LinkedList<Player> getExceptionList() { return exceptionList; }
    public static ArrayList<Player> getPlayerList() { return playerList; }
    public ArrayList<Player> getOKSign() { return okSign; }
    public World getGameWorld() { return gameWorld; }
    public void setGameWorld(World gameWorld) { this.gameWorld = gameWorld; }

    // Game flow control
    public void gameReady(Player player) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "(!) 당신은 권한이 없습니다.");
            return;
        }
        
        if (scenario != ScriptStatus.NoPlay) {
            player.sendMessage(ChatColor.RED + "(!) 이미 게임이 시작되어있습니다.");
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
    public void gameStartStop() { 
        gameStartTimer.stopTimer();
        Ability.restrictionTimer.stopTimer();
    }
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
            if (reRandomAbility(player) == null) {
                player.sendMessage(ChatColor.RED + "(!) 능력의 갯수가 부족하여 재추첨이 불가합니다.");
                return;
            }
            mainScripter.handleCheck(player);
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
            broadcastMessage("인원이 능력의 갯수보다 많습니다. Error 처리된분들은 능력을");
            broadcastMessage("받을수 없으며 모든 게임 진행 대상에서 제외됩니다.");
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
        broadcastMessage(ChatColor.DARK_RED + "Physical Fighters");
        broadcastMessage(String.format(ChatColor.RED + "VER. %d", PhysicalFighters.BuildNumber));
        broadcastMessage(ChatColor.GREEN + "제작 : " + ChatColor.WHITE + "염료");
        broadcastMessage(ChatColor.GREEN + "업데이트 : " + ChatColor.WHITE + "어라랍");
        broadcastMessage(ChatColor.DARK_AQUA + "본 플러그인은 '제온'님의 VisualAbility 모듈을 사용합니다.");
    }

    private void handleAbilitySetup() {
        if (!PhysicalFighters.NoAbilitySetting) {
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
                sendAbilityInstructions(player);
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

    private void sendAbilityInstructions(Player player) {
        player.sendMessage(ChatColor.YELLOW + "(!) /va check " + ChatColor.WHITE + "= 능력 확인");
        player.sendMessage(ChatColor.YELLOW + "(!) /va yes " + ChatColor.WHITE + "= 능력 사용.");
        player.sendMessage(ChatColor.YELLOW + "(!) /va no " + ChatColor.WHITE + "= 능력 재추첨.(1회)");
    }

    private void startGameLogic() {
        broadcastMessage(ChatColor.GREEN + "게임이 시작되었습니다.");
        logPlayerAbilities();
        setupInvincibility();
        setupRestrictions();
        respawnTeleport();
        setupWorldSettings();
        enableAllAbilities();
        gameProgress();
    }

    private void logPlayerAbilities() {
        plugin.getLogger().info("플레이어들의 능력");
        int count = 0;
        for (Ability ability : AbilityList.AbilityList) {
            if (ability.getPlayer() != null) {
                plugin.getLogger().info(String.format("%d. %s - %s",
                        count, ability.getPlayer().getName(), ability.getAbilityName()));
                count++;
            }
        }
        plugin.getLogger().info("-------------------------");
    }

    private void setupInvincibility() {
        if (PhysicalFighters.EarlyInvincibleTime != 0) {
            broadcastMessage("시작 직후 " + PhysicalFighters.EarlyInvincibleTime + "분간은 무적입니다.");
            EventManager.DamageGuard = true;
        } else {
            broadcastMessage(ChatColor.RED + "초반 무적은 작동하지 않습니다.");
        }
    }

    private void setupRestrictions() {
        if (PhysicalFighters.RestrictionTime != 0) {
            Ability.restrictionTimer.startTimer(PhysicalFighters.RestrictionTime * 60, false);
        } else {
            broadcastMessage(ChatColor.YELLOW + "제약 카운트는 동작하지 않습니다.");
        }
    }

    private void setupWorldSettings() {
        PlayDistanceBuffer = playerList.size() * 50;
        
        for (World world : Bukkit.getWorlds()) {
            world.setTime(1L);
            world.setStorm(false);
            world.setWeatherDuration(0);
            world.setPVP(true);
        }
    }

    private void respawnTeleport() {
        Location spawnLocation = gameWorld.getSpawnLocation();
        spawnLocation.setY(gameWorld.getHighestBlockYAt((int) spawnLocation.getX(), (int) spawnLocation.getZ()));
        
        for (Player player : playerList) {
            resetPlayerStats(player);
            teleportAndEquipPlayer(player, spawnLocation);
        }
        
        for (Player player : exceptionList) {
            player.teleport(spawnLocation);
        }
    }

    private void resetPlayerStats(Player player) {
        player.setFoodLevel(20);
        player.setLevel(PhysicalFighters.Setlev);
        player.setExhaustion(0.0F);
        player.setExp(0.0F);
        player.setHealth(20.0D);
        player.setSaturation(10.0F);
    }

    private void teleportAndEquipPlayer(Player player, Location location) {
        if (PhysicalFighters.ClearInventory) {
            player.getInventory().clear();
        }
        
        if (PhysicalFighters.Respawn) {
            player.teleport(location);
        }
        
        if (PhysicalFighters.DefaultArmed) {
            equipDefaultItems(player);
        }
        
        if (PhysicalFighters.TableGive) {
            player.getInventory().addItem(
                new ItemStack(Material.ENCHANTING_TABLE, 1),
                new ItemStack(Material.BOOKSHELF, 64)
            );
        }
        
        if (PhysicalFighters.WoodGive) {
            player.getInventory().addItem(new ItemStack(Material.OAK_LOG, 64));
        }
    }

    private void equipDefaultItems(Player player) {
        player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.GOLDEN_BOOTS));
        player.getInventory().addItem(
            new ItemStack(Material.GOLDEN_SWORD, 1),
            new ItemStack(Material.IRON_INGOT, 64),
            new ItemStack(Material.GOLD_INGOT, 64)
        );
        broadcastMessage(ChatColor.GREEN + "기본 무장이 제공됩니다.");
    }

    private Ability assignRandomAbility(Player player) {
        List<Ability> availableAbilities = getAvailableAbilities();
        if (availableAbilities.isEmpty()) return null;
        
        Ability selectedAbility = availableAbilities.get(random.nextInt(availableAbilities.size()));
        selectedAbility.setPlayer(player, false);
        return selectedAbility;
    }

    public Ability reRandomAbility(Player player) {
        // Remove current ability
        for (Ability ability : AbilityList.AbilityList) {
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
        
        for (Ability ability : AbilityList.AbilityList) {
            if (ability.getPlayer() == null && 
                (playerList.size() > 6 || ability != AbilityList.mirroring)) {
                available.add(ability);
            }
        }
        
        return available;
    }

    private void resetAllAbilities() {
        for (Ability ability : AbilityList.AbilityList) {
            ability.setRunAbility(false);
            ability.setPlayer(null, false);
        }
    }

    private void enableAllAbilities() {
        for (Ability ability : AbilityList.AbilityList) {
            ability.setRunAbility(true);
            ability.setPlayer(ability.getPlayer(), false);
        }
    }

    private void handleInvincibilityCountdown(int count) {
        if (PhysicalFighters.EarlyInvincibleTime == 0) return;
        
        int remainingSeconds = PhysicalFighters.EarlyInvincibleTime * 60 - count;
        
        if (remainingSeconds == 0) {
            broadcastMessage(ChatColor.GREEN + "초반 무적이 해제되었습니다. 이제 데미지를 입습니다.");
            EventManager.DamageGuard = false;
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
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!okSign.contains(player)) {
                player.sendMessage(ChatColor.YELLOW + "당신의 능력이 올바르게 확정되지 않았습니다.");
            }
        }
    }

    private void showPeriodicInfo(int count) {
        if (PhysicalFighters.PrintTip) {
            showTips(count);
        }
        
        if (count > 0 && count % PROGRESS_INFO_INTERVAL == 0) {
            broadcastMessage(ChatColor.DARK_RED + "Physical Fighters");
            broadcastMessage(ChatColor.GRAY + "빌드 넘버 " + PhysicalFighters.BuildNumber);
        }
    }

    private void showTips(int count) {
        switch (count) {
            case 60 -> broadcastMessage(ChatColor.GOLD + 
                "TIP. 본 플러그인은 제온님이 배포한 VisualAbility의 모듈을 사용합니다.\n" +
                "http://cafe.naver.com/craftproducer 제온님이 운영하는 카페입니다.");
            case 120 -> broadcastMessage(ChatColor.GOLD + 
                "TIP. 액티브 능력은 철괴나 금괴를 이용해 사용하며,\n" +
                "패시브 능력은 사용할 필요 없이 자동으로 능력이 적용됩니다.");
            case 180 -> broadcastMessage(ChatColor.GOLD + 
                "TIP. 불편 및 건의 사항은 디스코드 @kdy05_로 문의 해주세요.");
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