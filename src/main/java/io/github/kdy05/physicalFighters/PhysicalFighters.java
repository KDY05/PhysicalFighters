package io.github.kdy05.physicalFighters;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.CommandManager;
import io.github.kdy05.physicalFighters.core.EventManager;
import io.github.kdy05.physicalFighters.core.AbilityList;
import io.github.kdy05.physicalFighters.scripts.MainScripter;

import org.bukkit.plugin.java.JavaPlugin;

public class PhysicalFighters extends JavaPlugin {
    private static PhysicalFighters plugin;
    public static int BuildNumber = 20250708;

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

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("빌드정보 " + BuildNumber);
        getLogger().info("Edit By 염료");
        getLogger().info("Updated By 어라랍");

        CommandManager commandManager = new CommandManager(this);
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        loadConfigs();

        getLogger().info("능력을 초기화합니다.");
        Ability.InitAbilityBase(this, commandManager);

        getLogger().info("스크립터를 초기화합니다.");
        commandManager.RegisterCommand(new MainScripter(this));

        if (EarlyInvincibleTime <= 0) {
            getLogger().info("초반 무적 시간이 0분 이하로 설정되어 초반 무적이 비활성화됩니다.");
            EarlyInvincibleTime = 0;
        }
        if (RestrictionTime <= 0) {
            getLogger().info("제약 시간이 0분 이하로 설정되어 제약 시간이 비활성화됩니다.");
            RestrictionTime = 0;
        }
        getLogger().info(String.format("능력 %d개가 등록되었습니다.", AbilityList.AbilityList.size()));
    }

    private void loadConfigs() {
        getLogger().info("기본 설정 로드 중입니다.");
        saveDefaultConfig();

        AutoKick = getConfig().getBoolean("AutoKick", true);
        AutoBan = getConfig().getBoolean("AutoBan", true);
        KillerOutput = getConfig().getBoolean("KillerOutput", true);

        ClearInventory = getConfig().getBoolean("ClearInventory", true);
        EarlyInvincibleTime = getConfig().getInt("EarlyInvincibleTime", 10);
        RestrictionTime = getConfig().getInt("RestrictionTime", 15);
        NoAbilitySetting = getConfig().getBoolean("NoAbilitySetting", false);
        Setlev = getConfig().getInt("SetLev", 60);

        AbilityOverLap = getConfig().getBoolean("AbilityOverLap", false);
    }

    @Override
    public void onDisable() {
        getLogger().info("플러그인을 종료합니다.");
    }

    public static PhysicalFighters getPlugin() {
        return plugin;
    }

}
