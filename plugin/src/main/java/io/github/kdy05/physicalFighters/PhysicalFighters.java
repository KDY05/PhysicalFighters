package io.github.kdy05.physicalFighters;

import io.github.kdy05.physicalFighters.ability.AbilityRegistry;
import io.github.kdy05.physicalFighters.api.AdapterRegistry;
import io.github.kdy05.physicalFighters.api.AttributeAdapter;
import io.github.kdy05.physicalFighters.api.PotionEffectTypeAdapter;
import io.github.kdy05.physicalFighters.api.VersionedAdapter;
import io.github.kdy05.physicalFighters.command.CommandManager;
import io.github.kdy05.physicalFighters.command.GameCommand;
import io.github.kdy05.physicalFighters.command.UtilCommand;
import io.github.kdy05.physicalFighters.config.ConfigManager;
import io.github.kdy05.physicalFighters.game.BaseKitManager;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.game.GameManager;
import io.github.kdy05.physicalFighters.game.InvincibilityManager;
import io.github.kdy05.physicalFighters.util.ServerVersionDetector;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PhysicalFighters extends JavaPlugin {

    private static PhysicalFighters plugin;
    private GameManager gameManager;
    private ConfigManager configManager;
    private BaseKitManager baseKitManager;
    private InvincibilityManager invincibilityManager;

    private static final String[] ATTRIBUTE_ADAPTERS = {
            "io.github.kdy05.physicalFighters.v1_21_3.AttributeAdapter_1_21_3",
            "io.github.kdy05.physicalFighters.v1_16_5.AttributeAdapter_1_16_5",
    };

    private static final String[] POTION_EFFECT_TYPE_ADAPTERS = {
            "io.github.kdy05.physicalFighters.v1_20_5.PotionEffectTypeAdapter_1_20_5",
            "io.github.kdy05.physicalFighters.v1_16_5.PotionEffectTypeAdapter_1_16_5",
    };

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("빌드정보: " + BuildConfig.BUILD_NUMBER);
        getLogger().info("(C) 어라랍, 염료, 제온");

        if (!initializeAdapter()) {
            getLogger().severe("지원하지 않는 서버 버전입니다. 플러그인을 비활성화합니다.");
            getLogger().severe("지원 버전: 1.16.5-1.21.11");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        configManager = new ConfigManager(this);
        getServer().getPluginManager().registerEvents(new EventManager(this), this);

        getLogger().info(String.format("능력 %d개가 등록되었습니다.", AbilityRegistry.getTypeCount()));

        gameManager = new GameManager(this);
        CommandManager commandManager = CommandManager.builder()
                .addCommand(new GameCommand(this, gameManager))
                .addCommand(new UtilCommand(this, configManager))
                .addCommand(AbilityRegistry::dispatchCommand)
                .build();

        Objects.requireNonNull(getCommand("va")).setExecutor(commandManager);
        Objects.requireNonNull(getCommand("va")).setTabCompleter(commandManager);

        baseKitManager = new BaseKitManager(this);
        invincibilityManager = new InvincibilityManager(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("플러그인을 종료합니다.");
    }

    private boolean initializeAdapter() {
        String version = ServerVersionDetector.detectVersion();
        getLogger().info("감지된 서버 버전: " + version);

        AttributeAdapter attributeAdapter = findCompatibleAdapter(version, ATTRIBUTE_ADAPTERS);
        PotionEffectTypeAdapter potionEffectTypeAdapter = findCompatibleAdapter(version, POTION_EFFECT_TYPE_ADAPTERS);

        if (attributeAdapter == null || potionEffectTypeAdapter == null) {
            getLogger().severe("지원하지 않는 서버 버전: " + version);
            return false;
        }

        AdapterRegistry.register(attributeAdapter, potionEffectTypeAdapter);
        getLogger().info("어댑터 로드 완료: " + attributeAdapter.getClass().getSimpleName()
                + ", " + potionEffectTypeAdapter.getClass().getSimpleName());
        return true;
    }

    @SuppressWarnings("unchecked")
    private <T extends VersionedAdapter> T findCompatibleAdapter(String version, String[] candidates) {
        for (String className : candidates) {
            try {
                T adapter = (T) Class.forName(className).getDeclaredConstructor().newInstance();
                if (adapter.isCompatible(version)) {
                    return adapter;
                }
            } catch (Exception e) {
                // 클래스 로드 실패 (해당 버전에서 존재하지 않는 API 참조) — 다음 후보로
            }
        }
        return null;
    }

    public static PhysicalFighters getPlugin() {
        return plugin;
    }

    public GameManager getGameManager() {
        return gameManager;
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
