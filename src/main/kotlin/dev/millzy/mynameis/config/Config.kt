package dev.millzy.mynameis.config

class Config(configData: ConfigData) {
    var modEnabled: Boolean = false
    var maxLength: Int = 0

    init {
        modEnabled = configData.modEnabled
        maxLength = configData.maxLength
    }
}