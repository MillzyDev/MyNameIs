package dev.millzy.mynameis

import dev.millzy.mynameis.command.MyNameIsCommand
import dev.millzy.mynameis.config.ConfigManager
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object MyNameIsMod : ModInitializer {
    val logger: Logger = LoggerFactory.getLogger("mynameis")

	override fun onInitialize() {
		if (!ConfigManager.loadConfig()) {
			logger.info("Failed to load config. Will not proceed with initialisation.")
			return
		}

		if (!ConfigManager.config?.modEnabled!!) {
			logger.info("Mod is disabled. Will not proceed with initialisation.")
			return
		}

		if (!NameHandler.loadNames()) {
			logger.warn("Failed to load names.")
		}

		SERVER_STOPPING.register(MyNameIsMod::onServerStop)

		MyNameIsCommand.register()
		logger.info("Finished initialisation.")
	}

	private fun onServerStop(server: MinecraftServer) {
		NameHandler.saveNames()
		logger.info("Saved names!")
	}
}