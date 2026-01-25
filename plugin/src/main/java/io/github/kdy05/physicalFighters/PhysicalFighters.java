package io.github.kdy05.physicalFighters;

import io.github.kdy05.physicalFighters.api.AdapterRegistry;
import io.github.kdy05.physicalFighters.api.AttributeAdapter;
import io.github.kdy05.physicalFighters.api.PotionEffectTypeAdapter;
import io.github.kdy05.physicalFighters.core.*;
import io.github.kdy05.physicalFighters.util.CommandInterface;
import io.github.kdy05.physicalFighters.util.ServerVersionDetector;
import io.github.kdy05.physicalFighters.util.AbilityInitializer;
import io.github.kdy05.physicalFighters.command.GameCommand;

import io.github.kdy05.physicalFighters.command.UtilCommand;
import io.github.kdy05.physicalFighters.module.BaseKitManager;
import io.github.kdy05.physicalFighters.module.InvincibilityManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PhysicalFighters extends JavaPlugin {

    private static PhysicalFighters plugin;
    private GameManager gameManager;
    private BaseKitManager baseKitManager;
    private InvincibilityManager invincibilityManager;

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

        getServer().getPluginManager().registerEvents(new EventManager(), this);
        ConfigManager configManager = new ConfigManager(this);

        Ability.initPlugin(this);
        getLogger().info(String.format("능력 %d개가 등록되었습니다.", AbilityInitializer.AbilityList.size()));

        // CommandInterface 구현 능력 수집
        List<CommandInterface> abilityCommands = AbilityInitializer.AbilityList.stream()
                .filter(CommandInterface.class::isInstance)
                .map(CommandInterface.class::cast)
                .collect(Collectors.toList());

        gameManager = new GameManager(this);
        CommandManager commandManager = CommandManager.builder()
                .addCommand(new GameCommand(this, gameManager))
                .addCommand(new UtilCommand(this, configManager))
                .addAll(abilityCommands)
                .build();

        Objects.requireNonNull(getCommand("va")).setExecutor(commandManager);
        Objects.requireNonNull(getCommand("va")).setTabCompleter(commandManager);

        baseKitManager = new BaseKitManager(this);
        invincibilityManager = new InvincibilityManager(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("플러그인을 종료합니다.");
        plugin = null;
    }

    private boolean initializeAdapter() {
        String version = ServerVersionDetector.detectVersion();
        getLogger().info("감지된 서버 버전: " + version);

        try {
            AttributeAdapter attributeAdapter;
            PotionEffectTypeAdapter potionEffectTypeAdapter;
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
                getLogger().severe("지원하지 않는 서버 버전: " + version);
                return false;
            }

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

    public GameManager getGameManager() {
        return gameManager;
    }

    public BaseKitManager getBaseKitManager() {
        return baseKitManager;
    }

    public InvincibilityManager getInvincibilityManager() {
        return invincibilityManager;
    }
}
