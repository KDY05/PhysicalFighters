package io.github.kdy05.physicalFighters.core;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final PhysicalFighters plugin;

    public static boolean DamageGuard = false;
    public static boolean NoFoodMode = false;
    public static boolean InfinityDur = false;
    public static boolean AutoKick;
    public static boolean AutoBan;
    public static boolean KillerOutput;
    public static int Setlev;
    public static int EarlyInvincibleTime;
    public static int RestrictionTime;
    public static boolean ClearInventory;
    public static boolean NoAbilitySetting;
    public static boolean AbilityOverLap;

    public ConfigManager(PhysicalFighters plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    private void loadConfigs() {
        plugin.getLogger().info("기본 설정 로드 중입니다.");
        plugin.saveDefaultConfig();
        reloadConfigs();
    }

    public void reloadConfigs() {
        FileConfiguration config = plugin.getConfig();

        AutoKick = config.getBoolean("AutoKick", true);
        AutoBan = config.getBoolean("AutoBan", true);
        KillerOutput = config.getBoolean("KillerOutput", true);

        ClearInventory = config.getBoolean("ClearInventory", true);
        EarlyInvincibleTime = config.getInt("EarlyInvincibleTime", 10);
        RestrictionTime = config.getInt("RestrictionTime", 15);
        NoAbilitySetting = config.getBoolean("NoAbilitySetting", false);
        Setlev = config.getInt("SetLev", 60);

        AbilityOverLap = config.getBoolean("AbilityOverLap", false);

        if (EarlyInvincibleTime <= 0) {
            plugin.getLogger().info("초반 무적 시간이 0분 이하로 설정되어 초반 무적이 비활성화됩니다.");
            EarlyInvincibleTime = 0;
        }
        if (RestrictionTime <= 0) {
            plugin.getLogger().info("제약 시간이 0분 이하로 설정되어 제약 시간이 비활성화됩니다.");
            RestrictionTime = 0;
        }
    }

}
