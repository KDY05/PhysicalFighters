package io.github.kdy05.physicalFighters.config;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigManager {

    private final PhysicalFighters plugin;

    private Config config;
    private boolean noFoodMode = false;
    private boolean infinityDur = false;

    public ConfigManager(PhysicalFighters plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reloadConfigs();
    }

    public void reloadConfigs() {
        plugin.reloadConfig();
        FileConfiguration fileConfig = plugin.getConfig();

        int earlyInvincibleTime = fileConfig.getInt("EarlyInvincibleTime", 10);
        int restrictionTime = fileConfig.getInt("RestrictionTime", 15);

        if (earlyInvincibleTime <= 0) {
            plugin.getLogger().info("초반 무적 시간이 0분 이하로 설정되어 초반 무적이 비활성화됩니다.");
            earlyInvincibleTime = 0;
        }
        if (restrictionTime <= 0) {
            plugin.getLogger().info("제약 시간이 0분 이하로 설정되어 제약 시간이 비활성화됩니다.");
            restrictionTime = 0;
        }

        this.config = new Config(
                fileConfig.getInt("OnKill", 2),
                fileConfig.getBoolean("KillerOutput", true),
                fileConfig.getInt("SetLev", 60),
                earlyInvincibleTime,
                restrictionTime,
                fileConfig.getBoolean("ClearInventory", true),
                fileConfig.getBoolean("NoAbilitySetting", false),
                fileConfig.getBoolean("AbilityOverLap", false)
        );

        plugin.getLogger().info("설정이 다시 로드되었습니다.");
    }

    // Config 위임 메서드
    public int getOnKill() {
        return config.onKill;
    }

    public boolean isKillerOutput() {
        return config.killerOutput;
    }

    public int getSetLev() {
        return config.setLev;
    }

    public int getEarlyInvincibleTime() {
        return config.earlyInvincibleTime;
    }

    public int getRestrictionTime() {
        return config.restrictionTime;
    }

    public boolean isClearInventory() {
        return config.clearInventory;
    }

    public boolean isNoAbilitySetting() {
        return config.noAbilitySetting;
    }

    public boolean isAbilityOverLap() {
        return config.abilityOverLap;
    }

    // 런타임 토글 상태 접근자
    public boolean isNoFoodMode() {
        return noFoodMode;
    }

    public void setNoFoodMode(boolean noFoodMode) {
        this.noFoodMode = noFoodMode;
    }

    public boolean isInfinityDur() {
        return infinityDur;
    }

    public void setInfinityDur(boolean infinityDur) {
        this.infinityDur = infinityDur;
    }

    /**
     * 게임 설정을 담는 불변 클래스.
     */
    private static final class Config {
        private final int onKill;
        private final boolean killerOutput;
        private final int setLev;
        private final int earlyInvincibleTime;
        private final int restrictionTime;
        private final boolean clearInventory;
        private final boolean noAbilitySetting;
        private final boolean abilityOverLap;

        private Config(
                int onKill,
                boolean killerOutput,
                int setLev,
                int earlyInvincibleTime,
                int restrictionTime,
                boolean clearInventory,
                boolean noAbilitySetting,
                boolean abilityOverLap
        ) {
            this.onKill = onKill;
            this.killerOutput = killerOutput;
            this.setLev = setLev;
            this.earlyInvincibleTime = earlyInvincibleTime;
            this.restrictionTime = restrictionTime;
            this.clearInventory = clearInventory;
            this.noAbilitySetting = noAbilitySetting;
            this.abilityOverLap = abilityOverLap;
        }
    }
}
