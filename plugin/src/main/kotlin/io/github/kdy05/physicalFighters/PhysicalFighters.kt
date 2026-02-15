package io.github.kdy05.physicalFighters

import io.github.kdy05.physicalFighters.ability.AbilityRegistry
import io.github.kdy05.physicalFighters.api.AdapterRegistry
import io.github.kdy05.physicalFighters.api.AttributeAdapter
import io.github.kdy05.physicalFighters.api.PotionEffectTypeAdapter
import io.github.kdy05.physicalFighters.api.VersionedAdapter
import io.github.kdy05.physicalFighters.command.CommandManager
import io.github.kdy05.physicalFighters.command.GameCommand
import io.github.kdy05.physicalFighters.command.UtilCommand
import io.github.kdy05.physicalFighters.config.ConfigManager
import io.github.kdy05.physicalFighters.game.BaseKitManager
import io.github.kdy05.physicalFighters.game.EventManager
import io.github.kdy05.physicalFighters.game.GameManager
import io.github.kdy05.physicalFighters.game.InvincibilityManager
import io.github.kdy05.physicalFighters.util.ServerVersionDetector
import org.bukkit.plugin.java.JavaPlugin

class PhysicalFighters : JavaPlugin() {

    lateinit var gameManager: GameManager
        private set
    lateinit var configManager: ConfigManager
        private set
    lateinit var baseKitManager: BaseKitManager
        private set
    lateinit var invincibilityManager: InvincibilityManager
        private set

    override fun onEnable() {
        plugin = this
        logger.info("빌드정보: ${BuildConfig.BUILD_NUMBER}")
        logger.info("(C) 어라랍, 염료, 제온")

        if (!initializeAdapter()) {
            logger.severe("지원하지 않는 서버 버전입니다. 플러그인을 비활성화합니다.")
            logger.severe("지원 버전: 1.16.5-1.21.11")
            server.pluginManager.disablePlugin(this)
            return
        }

        configManager = ConfigManager(this)
        server.pluginManager.registerEvents(EventManager(this), this)

        logger.info("능력 ${AbilityRegistry.getTypeCount()}개가 등록되었습니다.")

        gameManager = GameManager(this)
        val commandManager = CommandManager(
            GameCommand(this, gameManager),
            UtilCommand(this, configManager),
            AbilityRegistry::dispatchCommand
        )

        getCommand("va")!!.setExecutor(commandManager)
        getCommand("va")!!.tabCompleter = commandManager

        baseKitManager = BaseKitManager(this)
        invincibilityManager = InvincibilityManager(this)
    }

    override fun onDisable() {
        AbilityRegistry.deactivateAll()
        logger.info("플러그인을 종료합니다.")
    }

    private fun initializeAdapter(): Boolean {
        val version = ServerVersionDetector.detectVersion()
        logger.info("감지된 서버 버전: $version")

        val attributeAdapter = findCompatibleAdapter(version, ATTRIBUTE_ADAPTERS) as? AttributeAdapter
        val potionEffectTypeAdapter = findCompatibleAdapter(version, POTION_EFFECT_TYPE_ADAPTERS) as? PotionEffectTypeAdapter

        if (attributeAdapter == null || potionEffectTypeAdapter == null) {
            logger.severe("지원하지 않는 서버 버전: $version")
            return false
        }

        AdapterRegistry.register(attributeAdapter, potionEffectTypeAdapter)
        logger.info("어댑터 로드 완료: ${attributeAdapter.javaClass.simpleName}, ${potionEffectTypeAdapter.javaClass.simpleName}")
        return true
    }

    private fun findCompatibleAdapter(version: String, candidates: Array<String>): VersionedAdapter? {
        for (className in candidates) {
            try {
                val adapter = Class.forName(className).getDeclaredConstructor().newInstance() as VersionedAdapter
                if (adapter.isCompatible(version)) return adapter
            } catch (_: Exception) {
                // 클래스 로드 실패 (해당 버전에서 존재하지 않는 API 참조) — 다음 후보로
            }
        }
        return null
    }

    companion object {
        @JvmStatic
        lateinit var plugin: PhysicalFighters
            private set

        private val ATTRIBUTE_ADAPTERS = arrayOf(
            "io.github.kdy05.physicalFighters.v1_21_3.AttributeAdapter_1_21_3",
            "io.github.kdy05.physicalFighters.v1_16_5.AttributeAdapter_1_16_5",
        )

        private val POTION_EFFECT_TYPE_ADAPTERS = arrayOf(
            "io.github.kdy05.physicalFighters.v1_20_5.PotionEffectTypeAdapter_1_20_5",
            "io.github.kdy05.physicalFighters.v1_16_5.PotionEffectTypeAdapter_1_16_5",
        )
    }
}
