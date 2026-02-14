package io.github.kdy05.physicalFighters.config

import io.github.kdy05.physicalFighters.PhysicalFighters

class ConfigManager(private val plugin: PhysicalFighters) {

    var isNoFoodMode: Boolean = false
    var isInfinityDur: Boolean = false

    private var config: Config

    init {
        plugin.saveDefaultConfig()
        config = loadConfig()
    }

    fun reloadConfigs() {
        plugin.reloadConfig()
        config = loadConfig()
        plugin.logger.info("설정이 다시 로드되었습니다.")
    }

    private fun loadConfig(): Config {
        val fileConfig = plugin.config

        var earlyInvincibleTime = fileConfig.getInt("EarlyInvincibleTime", 10)
        var restrictionTime = fileConfig.getInt("RestrictionTime", 15)

        if (earlyInvincibleTime <= 0) {
            plugin.logger.info("초반 무적 시간이 0분 이하로 설정되어 초반 무적이 비활성화됩니다.")
            earlyInvincibleTime = 0
        }
        if (restrictionTime <= 0) {
            plugin.logger.info("제약 시간이 0분 이하로 설정되어 제약 시간이 비활성화됩니다.")
            restrictionTime = 0
        }

        return Config(
            onKill = fileConfig.getInt("OnKill", 2),
            killerOutput = fileConfig.getBoolean("KillerOutput", true),
            setLev = fileConfig.getInt("SetLev", 60),
            earlyInvincibleTime = earlyInvincibleTime,
            restrictionTime = restrictionTime,
            clearInventory = fileConfig.getBoolean("ClearInventory", true),
            noAbilitySetting = fileConfig.getBoolean("NoAbilitySetting", false),
            abilityOverLap = fileConfig.getBoolean("AbilityOverLap", false)
        )
    }

    val onKill: Int get() = config.onKill
    val isKillerOutput: Boolean get() = config.killerOutput
    val setLev: Int get() = config.setLev
    val earlyInvincibleTime: Int get() = config.earlyInvincibleTime
    val restrictionTime: Int get() = config.restrictionTime
    val isClearInventory: Boolean get() = config.clearInventory
    val isNoAbilitySetting: Boolean get() = config.noAbilitySetting
    val isAbilityOverLap: Boolean get() = config.abilityOverLap

    private data class Config(
        val onKill: Int,
        val killerOutput: Boolean,
        val setLev: Int,
        val earlyInvincibleTime: Int,
        val restrictionTime: Int,
        val clearInventory: Boolean,
        val noAbilitySetting: Boolean,
        val abilityOverLap: Boolean
    )
}
