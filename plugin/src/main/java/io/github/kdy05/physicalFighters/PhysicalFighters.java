package io.github.kdy05.physicalFighters;

import io.github.kdy05.physicalFighters.api.AdapterRegistry;
import io.github.kdy05.physicalFighters.api.AttributeAdapter;
import io.github.kdy05.physicalFighters.api.PotionEffectTypeAdapter;
import io.github.kdy05.physicalFighters.core.*;
import io.github.kdy05.physicalFighters.util.ServerVersionDetector;
import io.github.kdy05.physicalFighters.util.AbilityInitializer;
import io.github.kdy05.physicalFighters.command.GameCommand;

import io.github.kdy05.physicalFighters.command.UtilCommand;
import io.github.kdy05.physicalFighters.util.module.BaseKitManager;
import io.github.kdy05.physicalFighters.util.module.InvincibilityManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PhysicalFighters extends JavaPlugin {

    public static final int BUILD_NUMBER = 20250809;
    private static PhysicalFighters plugin;
    private static AttributeAdapter attributeAdapter;
    private static PotionEffectTypeAdapter potionEffectTypeAdapter;

    private GameManager gameManager;
    private GameCommand gameCommand;
    private ConfigManager configManager;
    private BaseKitManager baseKitManager;
    private InvincibilityManager invincibilityManager;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("빌드정보 " + BUILD_NUMBER);
        getLogger().info("Edit By 염료");
        getLogger().info("Updated By 어라랍");

        if (!initializeAdapter()) {
            getLogger().severe("지원되지 않는 서버 버전입니다. 플러그인을 비활성화합니다.");
            getLogger().severe("지원 버전: 1.16.5+");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        CommandManager commandManager = new CommandManager(this);
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        configManager = new ConfigManager(this);

        getLogger().info("능력을 초기화합니다.");
        Ability.InitAbilityBase(this, commandManager);

        getLogger().info("스크립터를 초기화합니다.");
        gameManager = new GameManager(this);
        gameCommand = new GameCommand(this, gameManager);
        commandManager.registerCommand(gameCommand);
        commandManager.registerCommand(new UtilCommand(this));

        baseKitManager = new BaseKitManager(this);
        invincibilityManager = new InvincibilityManager();
        getServer().getPluginManager().registerEvents(invincibilityManager, this);

        getLogger().info(String.format("능력 %d개가 등록되었습니다.", AbilityInitializer.AbilityList.size()));
    }

    @Override
    public void onDisable() {
        getLogger().info("플러그인을 종료합니다.");
    }

    private boolean initializeAdapter() {
        String version = ServerVersionDetector.detectVersion();
        getLogger().info("감지된 서버 버전: " + version);

        try {
            if (ServerVersionDetector.isBetween("1.16.5", "1.20.4")) {
                // 1.16.5 ~ 1.20.4: v1_16_5 모듈 사용
                attributeAdapter = loadAdapter(
                    "io.github.kdy05.physicalFighters.v1_16_5.AttributeAdapter_1_16_5"
                );
                potionEffectTypeAdapter = loadAdapter(
                    "io.github.kdy05.physicalFighters.v1_16_5.PotionEffectTypeAdapter_1_16_5"
                );
            } else if (ServerVersionDetector.isBetween("1.20.5", "1.21.2")) {
                // 1.20.5 ~ 1.21.2: Attribute는 1.16.5, PotionEffectType은 1.20.5
                attributeAdapter = loadAdapter(
                    "io.github.kdy05.physicalFighters.v1_16_5.AttributeAdapter_1_16_5"
                );
                potionEffectTypeAdapter = loadAdapter(
                    "io.github.kdy05.physicalFighters.v1_20_5.PotionEffectTypeAdapter_1_20_5"
                );
            } else if (ServerVersionDetector.isAtLeast("1.21.3")) {
                // 1.21.3+: Attribute는 1.21.3, PotionEffectType은 1.20.5
                attributeAdapter = loadAdapter(
                    "io.github.kdy05.physicalFighters.v1_21_3.AttributeAdapter_1_21_3"
                );
                potionEffectTypeAdapter = loadAdapter(
                    "io.github.kdy05.physicalFighters.v1_20_5.PotionEffectTypeAdapter_1_20_5"
                );
            } else {
                getLogger().severe("지원되지 않는 서버 버전: " + version);
                return false;
            }

            // AdapterRegistry에 등록하여 api 모듈의 Factory/Utils에서 사용 가능하게 함
            AdapterRegistry.register(attributeAdapter, potionEffectTypeAdapter);

            getLogger().info("어댑터 로드 완료: " + attributeAdapter.getClass().getSimpleName()
                + ", " + potionEffectTypeAdapter.getClass().getSimpleName());
            return true;

        } catch (Exception e) {
            getLogger().severe("어댑터 로드 실패: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T loadAdapter(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        return (T) clazz.getDeclaredConstructor().newInstance();
    }

    public static PhysicalFighters getPlugin() {
        return plugin;
    }

    public static AttributeAdapter getAttributeAdapter() {
        return attributeAdapter;
    }

    public static PotionEffectTypeAdapter getPotionEffectTypeAdapter() {
        return potionEffectTypeAdapter;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public GameCommand getGameCommand() {
        return gameCommand;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public BaseKitManager getBaseKitManager() {
        return baseKitManager;
    }

    public InvincibilityManager getInvincibilityManager() {
        return invincibilityManager;
    }
}
