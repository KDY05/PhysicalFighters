package io.github.kdy05.physicalFighters.util.module;

import io.github.kdy05.physicalFighters.core.ConfigManager;
import io.github.kdy05.physicalFighters.util.TimerBase;
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

public class InvincibilityManager implements Listener {
    
    private BossBar invincibilityBar;
    private InvincibilityTimer timer;
    private boolean isActive = false;
    private int customMinutes = 0;
    
    public InvincibilityManager() {
    }
    
    public void startInvincibility(int minutes) {
        if (minutes == 0) {
            broadcastMessage(ChatColor.RED + "무적 시간이 0분으로 설정되어 작동하지 않습니다.");
            return;
        }
        
        if (isActive) {
            broadcastMessage(ChatColor.RED + "이미 무적이 활성화되어 있습니다. 먼저 해제하세요.");
            return;
        }
        
        this.customMinutes = minutes;
        isActive = true;
        ConfigManager.DamageGuard = true;
        
        createBossBar();
        addPlayersToBar();
        
        timer = new InvincibilityTimer();
        timer.startTimer(minutes * 60, false);
        
        if (minutes == ConfigManager.EarlyInvincibleTime) {
            broadcastMessage("시작 직후 " + minutes + "분간 무적입니다.");
        } else {
            broadcastMessage("OP에 의해 " + minutes + "분간 무적이 설정되었습니다.");
        }
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
        
        broadcastMessage(ChatColor.GREEN + "무적이 해제되었습니다. 이제 대미지를 입습니다.");
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
        
        broadcastMessage(ChatColor.GREEN + "OP에 의해 무적이 해제되었습니다. 이제 대미지를 입습니다.");
    }
    
    public void toggle() {
        if (ConfigManager.DamageGuard) {
            if (isActive) {
                forceStop();
            } else {
                ConfigManager.DamageGuard = false;
                broadcastMessage(ChatColor.GREEN + "OP에 의해 무적이 해제되었습니다. 이제 대미지를 입습니다.");
            }
        } else {
            ConfigManager.DamageGuard = true;
            broadcastMessage(ChatColor.GREEN + "OP에 의해 무적이 설정되었습니다. 이제 대미지를 입지않습니다.");
        }
    }
    
    private void createBossBar() {
        String title = "무적 시간";
        invincibilityBar = Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SEGMENTED_12);
        invincibilityBar.setProgress(1.0);
    }
    
    private void addPlayersToBar() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            invincibilityBar.addPlayer(player);
        }
    }
    
    private void updateBossBar(int remainingSeconds) {
        if (invincibilityBar == null) return;
        
        int totalSeconds = customMinutes * 60;
        double progress = (double) remainingSeconds / totalSeconds;
        
        invincibilityBar.setProgress(Math.max(0.0, progress));
        
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        
        String title = String.format("무적 시간 %02d:%02d", minutes, seconds);
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
        if (isActive && invincibilityBar != null) {
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
            int remainingSeconds = customMinutes * 60 - count;
            
            updateBossBar(remainingSeconds);
            
            if (remainingSeconds == 0) {
                stopInvincibility();
            } else if (remainingSeconds <= 5 && remainingSeconds >= 1) {
                broadcastMessage(String.format(ChatColor.YELLOW + "%d초 후" + ChatColor.WHITE + " 무적이 해제됩니다.", 
                               remainingSeconds));
            } else if (remainingSeconds == 60) {
                broadcastMessage(ChatColor.YELLOW + "무적이 " + ChatColor.WHITE + "1분 후 해제됩니다.");
            }
        }
        
        @Override
        public void onTimerEnd() {
            stopInvincibility();
        }
    }

}