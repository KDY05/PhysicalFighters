package io.github.kdy05.physicalFighters;

import io.github.kdy05.physicalFighters.core.*;
import io.github.kdy05.physicalFighters.utils.AbilityInitializer;
import io.github.kdy05.physicalFighters.command.GameCommand;

import io.github.kdy05.physicalFighters.command.UtilCommand;
import io.github.kdy05.physicalFighters.utils.module.BaseKitManager;
import io.github.kdy05.physicalFighters.utils.module.InvincibilityManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PhysicalFighters extends JavaPlugin {

    public static int BuildNumber = 20250809;
    private static PhysicalFighters plugin;
    private GameManager gameManager;
    private GameCommand gameCommand;
    private ConfigManager configManager;
    private BaseKitManager baseKitManager;
    private InvincibilityManager invincibilityManager;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("빌드정보 " + BuildNumber);
        getLogger().info("Edit By 염료");
        getLogger().info("Updated By 어라랍");

        CommandManager commandManager = new CommandManager(this);
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        configManager = new ConfigManager(this);

        getLogger().info("능력을 초기화합니다.");
        Ability.InitAbilityBase(this, commandManager);

        getLogger().info("스크립터를 초기화합니다.");
        this.gameManager = new GameManager(this);
        this.gameCommand = new GameCommand(this, gameManager);
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

    public static PhysicalFighters getPlugin() {
        return plugin;
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
