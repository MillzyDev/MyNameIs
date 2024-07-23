package dev.millzy.mynameis.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.millzy.mynameis.MyNameIsMod
import net.fabricmc.loader.api.FabricLoader
import org.apache.commons.io.IOUtils
import java.io.*

object ConfigManager {
    private var GSON: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create()
    
    var config: Config? = null
        private set

    fun loadConfig(): Boolean {
        config = null
        try {
            val configData: ConfigData
            val configFile = File(
                FabricLoader.getInstance().configDir.toFile(), "mynameis.json"
            )

            if (configFile.exists()) {
                val json: String = IOUtils.toString(
                    InputStreamReader(
                        FileInputStream(configFile),
                        "UTF-8"
                    )
                )
                configData = GSON.fromJson(json, ConfigData::class.java)
            }
            else {
                configData = ConfigData()
            }

            val writer = BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(configFile),
                    "UTF-8")
            )
            writer.write(GSON.toJson(configData))
            writer.close()

            config = Config(configData)
            return true
        }
        catch (e: IOException) {
            MyNameIsMod.logger.error("Unable to read config!")
            e.printStackTrace()
            config = Config(ConfigData())
            return false
        }
    }

}