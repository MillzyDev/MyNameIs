package dev.millzy.mynameis

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import org.apache.commons.io.IOUtils
import java.io.*

object NameHandler {
    private var names: HashMap<String, String>? = null
    private var GSON: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create()

    fun getName(uuid: String): String? {
        return names!![uuid]
    }

    fun setName(uuid: String, name: String) {
        names!![uuid] = name
    }

    fun loadNames(): Boolean {
        names = null
        try {
            val namesFile = File(
                FabricLoader.getInstance().configDir.toFile(), "mynameis.names.json"
            )

            if (namesFile.exists()) {
                val json = IOUtils.toString(
                    InputStreamReader(
                        FileInputStream(namesFile),
                        "UTF-8"
                    )
                )
                val mapType = object: TypeToken<HashMap<String, String>>() {}.type
                names = GSON.fromJson(json, mapType)
            }
            else {
                names = HashMap()
            }

            val writer = BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(namesFile),
                    "UTF-8")
            )
            writer.write(GSON.toJson(names))
            writer.close()

            return true
        }
        catch (e: IOException) {
            MyNameIsMod.logger.error("Unable to read player names!")
            e.printStackTrace()
            names = HashMap()
            return false
        }
    }

    fun saveNames() {
        try {
            val namesFile = File(
                FabricLoader.getInstance().configDir.toFile(), "mynameis.names.json"
            )

            val writer = BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(namesFile),
                    "UTF-8")
            )
            writer.write(GSON.toJson(names))
            writer.close()
        }
        catch (e: IOException) {
            MyNameIsMod.logger.error("Failed to save player names for this server.")
            e.printStackTrace()
        }
    }
}