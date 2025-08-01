package io.github.kdy05.physicalFighters.utils.module;

import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.utils.TimerBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class InvincibilityManager implements Listener {
    
    private final List<Player> players;
    private BossBar invincibilityBar;
    private InvincibilityTimer timer;
    private boolean isActive = false;
    
    public InvincibilityManager(List<Player> players) {
        this.players = new ArrayList<>(players);
    }
    
    public void startInvincibility() {
        if (ConfigManager.EarlyInvincibleTime == 0) {
            broadcastMessage(ChatColor.RED + "초반 무적은 작동하지 않습니다.");
            return;
        }
        
        isActive = true;
        ConfigManager.DamageGuard = true;
        
        createBossBar();
        addPlayersToBar();
        
        timer = new InvincibilityTimer();
        timer.startTimer(ConfigManager.EarlyInvincibleTime * 60, false);
        
        broadcastMessage("시작 직후 " + ConfigManager.EarlyInvincibleTime + "분간 무적입니다.");
    }
    
    public void stopInvincibility() {
        if (!isActive) return;
        
        isActive = false;
        ConfigManager.DamageGuard = false;
        
        if (timer != null) {
            timer.stopTimer();
        }
        
        if (invincibilityBar != null) {
            invincibilityBar.removeAll();
            invincibilityBar = null;
        }
        
        broadcastMessage(ChatColor.GREEN + "초반 무적이 해제되었습니다. 이제 대미지를 입습니다.");
    }
    
    public boolean isInvincible() {
        return isActive && ConfigManager.DamageGuard;
    }
    
    public void forceStop() {
        if (!isActive) return;
        
        isActive = false;
        ConfigManager.DamageGuard = false;
        
        if (timer != null) {
            timer.stopTimer();
        }
        
        if (invincibilityBar != null) {
            invincibilityBar.removeAll();
            invincibilityBar = null;
        }
        
        broadcastMessage(ChatColor.GREEN + "OP에 의해 초반 무적이 해제되었습니다. 이제 대미지를 입습니다.");
    }
    
    private void createBossBar() {
        String title = "초반 무적 시간";
        invincibilityBar = Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SEGMENTED_12);
        invincibilityBar.setProgress(1.0);
    }
    
    private void addPlayersToBar() {
        for (Player player : players) {
            if (player.isOnline()) {
                invincibilityBar.addPlayer(player);
            }
        }
    }
    
    private void updateBossBar(int remainingSeconds) {
        if (invincibilityBar == null) return;
        
        int totalSeconds = ConfigManager.EarlyInvincibleTime * 60;
        double progress = (double) remainingSeconds / totalSeconds;
        
        invincibilityBar.setProgress(Math.max(0.0, progress));
        
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        
        String title = String.format("%s초반 무적 %02d:%02d", 
                                   ChatColor.GOLD, minutes, seconds);
        invincibilityBar.setTitle(title);
        
        if (remainingSeconds <= 30) {
            invincibilityBar.setColor(BarColor.RED);
        } else if (remainingSeconds <= 60) {
            invincibilityBar.setColor(BarColor.YELLOW);
        }
    }
    
    private void broadcastMessage(String message) {
        Bukkit.broadcastMessage(message);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isActive && players.contains(player) && invincibilityBar != null) {
            invincibilityBar.addPlayer(player);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (invincibilityBar != null) {
            invincibilityBar.removePlayer(player);
        }
    }
    
    private class InvincibilityTimer extends TimerBase {
        @Override
        public void onTimerStart() {}
        
        @Override
        public void onTimerRunning(int count) {
            int remainingSeconds = ConfigManager.EarlyInvincibleTime * 60 - count;
            
            updateBossBar(remainingSeconds);
            
            if (remainingSeconds == 0) {
                stopInvincibility();
            } else if (remainingSeconds <= 5 && remainingSeconds >= 1) {
                broadcastMessage(String.format(ChatColor.YELLOW + "%d초 후" + ChatColor.WHITE + " 초반무적이 해제됩니다.", 
                               remainingSeconds));
            } else if (remainingSeconds == 60) {
                broadcastMessage(ChatColor.YELLOW + "초반 무적이 " + ChatColor.WHITE + "1분 후 해제됩니다.");
            }
        }
        
        @Override
        public void onTimerEnd() {
            stopInvincibility();
        }
    }

}