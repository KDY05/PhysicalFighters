package io.github.kdy05.physicalFighters.config

import io.github.kdy05.physicalFighters.PhysicalFighters

class ConfigManager(private val plugin: PhysicalFighters) {

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

        var setLev = fileConfig.getInt("SetLev", 60)
        if (setLev <= 0) {
            plugin.logger.info("시작 레벨이 0 이하로 설정되어 0으로 조정됩니다.")
            setLev = 0
        }

        var earlyInvincibleTime = fileConfig.getInt("EarlyInvincibleTime", 10)
        if (earlyInvincibleTime <= 0) {
            plugin.logger.info("초반 무적 시간이 0분 이하로 설정되어 초반 무적이 비활성화됩니다.")
            earlyInvincibleTime = 0
        }

        return Config(
            onKill = fileConfig.getInt("OnKill", 2),
            killerOutput = fileConfig.getBoolean("KillerOutput", true),
            setLev = setLev,
            earlyInvincibleTime = earlyInvincibleTime,
            clearInventory = fileConfig.getBoolean("ClearInventory", true),
            noAbilitySetting = fileConfig.getBoolean("NoAbilitySetting", false),
            isNoFoodMode = fileConfig.getBoolean("NoFoodMode", false),
            isInfinityDur = fileConfig.getBoolean("InfinityDur", false)
        )
    }

    private fun saveToFile() {
        with(plugin.config) {
            set("OnKill", config.onKill)
            set("KillerOutput", config.killerOutput)
            set("SetLev", config.setLev)
            set("EarlyInvincibleTime", config.earlyInvincibleTime)
            set("ClearInventory", config.clearInventory)
            set("NoAbilitySetting", config.noAbilitySetting)
            set("NoFoodMode", config.isNoFoodMode)
            set("InfinityDur", config.isInfinityDur)
        }
        plugin.saveConfig()
    }

    val onKill: Int get() = config.onKill
    val isKillerOutput: Boolean get() = config.killerOutput
    val setLev: Int get() = config.setLev
    val earlyInvincibleTime: Int get() = config.earlyInvincibleTime
    val isClearInventory: Boolean get() = config.clearInventory
    val isNoAbilitySetting: Boolean get() = config.noAbilitySetting
    val isNoFoodMode: Boolean get() = config.isNoFoodMode
    val isInfinityDur: Boolean get() = config.isInfinityDur

    fun updateOnKill(value: Int) { config = config.copy(onKill = value); saveToFile() }
    fun updateKillerOutput(value: Boolean) { config = config.copy(killerOutput = value); saveToFile() }
    fun updateSetLev(value: Int) { config = config.copy(setLev = value); saveToFile() }
    fun updateEarlyInvincibleTime(value: Int) { config = config.copy(earlyInvincibleTime = value); saveToFile() }
    fun updateClearInventory(value: Boolean) { config = config.copy(clearInventory = value); saveToFile() }
    fun updateNoAbilitySetting(value: Boolean) { config = config.copy(noAbilitySetting = value); saveToFile() }
    fun updateNoFoodMode(value: Boolean) { config = config.copy(isNoFoodMode = value); saveToFile() }
    fun updateInfinityDur(value: Boolean) { config = config.copy(isInfinityDur = value); saveToFile() }

    private data class Config(
        val onKill: Int,
        val killerOutput: Boolean,
        val setLev: Int,
        val earlyInvincibleTime: Int,
        val clearInventory: Boolean,
        val noAbilitySetting: Boolean,
        val isNoFoodMode: Boolean,
        val isInfinityDur: Boolean
    )
}
