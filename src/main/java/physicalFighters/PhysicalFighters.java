package physicalFighters;

import physicalFighters.core.Ability;
import physicalFighters.core.CommandManager;
import physicalFighters.core.EventManager;
import physicalFighters.core.AbilityList;
import physicalFighters.scripts.MainScripter;

import java.util.Timer;

import org.bukkit.plugin.java.JavaPlugin;

public class PhysicalFighters extends JavaPlugin {
    private static PhysicalFighters plugin;
    public static int BuildNumber = 20250708;
    public static Timer TracerTimer;

    public static boolean AutoKick;
    public static boolean AutoBan;
    public static boolean KillerOutput;

    public static boolean ClearInventory;
    public static boolean Respawn;
    public static int EarlyInvincibleTime;
    public static int RestrictionTime;
    public static boolean NoAbilitySetting;

    public static int Setlev;
    public static boolean DefaultArmed;
    public static boolean TableGive;
    public static boolean WoodGive;

    public static boolean NoFoodMode;
    public static boolean InfinityDur;
    public static boolean HalfMonsterDamage;

    public static boolean Specialability;
    public static boolean AbilityOverLap;
    public static boolean PrintTip;

    public static boolean Gods = false;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info(String.format("빌드정보 " + BuildNumber));
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
        if (Specialability) {
            getLogger().info("인기 능력만 사용합니다.");
        }
        getLogger().info(String.format("능력 %d개가 등록되었습니다.", AbilityList.AbilityList.size()));
    }

    private void loadConfigs() {
        getLogger().info("기본설정 로드중입니다.");
        saveDefaultConfig();

        AutoKick = getConfig().getBoolean("AutoKick", true);
        AutoBan = getConfig().getBoolean("AutoBan", true);
        KillerOutput = getConfig().getBoolean("KillerOutput", true);

        ClearInventory = getConfig().getBoolean("ClearInventory", true);
        Respawn = getConfig().getBoolean("Respawn", false);
        EarlyInvincibleTime = getConfig().getInt("EarlyInvincibleTime", 10);
        RestrictionTime = getConfig().getInt("RestrictionTime", 15);
        NoAbilitySetting = getConfig().getBoolean("NoAbilitySetting", false);

        Setlev = getConfig().getInt("SetLev", 60);
        DefaultArmed = getConfig().getBoolean("DefaultArmed", false);
        TableGive = getConfig().getBoolean("TableGive", false);
        WoodGive = getConfig().getBoolean("WoodGive", false);

        NoFoodMode = getConfig().getBoolean("NoFoodMode", false);
        InfinityDur = getConfig().getBoolean("InfinityDur", false);
        HalfMonsterDamage = getConfig().getBoolean("HalfMonsterDamage", false);

        Specialability = getConfig().getBoolean("Specialability", true);
        AbilityOverLap = getConfig().getBoolean("AbilityOverLap", false);
        PrintTip = getConfig().getBoolean("PrintTip", true);
    }

    @Override
    public void onDisable() {
        if (TracerTimer != null)
            TracerTimer.cancel();
        getLogger().info("플러그인을 종료합니다.");
    }

    public static PhysicalFighters getPlugin() {
        return plugin;
    }

}
