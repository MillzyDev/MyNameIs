package dev.millzy.mynameis.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.millzy.mynameis.NameHandler
import dev.millzy.mynameis.config.Config
import dev.millzy.mynameis.config.ConfigManager
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object MyNameIsCommand {
    fun register() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                CommandManager.literal("mynameis")
                    .then(
                        CommandManager.argument("name", StringArgumentType.string())
                            .executes(MyNameIsCommand::changeName)
                    )
                    .requires { source -> source.isExecutedByPlayer } // this command should on be run by the player
            )
        }
    }

    private fun changeName(context: CommandContext<ServerCommandSource>): Int {
        // we assume the command is run by the player, relatively safe considering the command requirement
        // this still isn't great design though.
        val player: ServerPlayerEntity = context.source.player!!
        val newName: String = context.getArgument("name", String::class.java)
        val maxLength: Int = ConfigManager.config?.maxLength ?: 48

        if (newName.length > maxLength) {
            context.source.sendFeedback({Text.of("That name is too long! Can be up to $maxLength characters.")}, false)
        }

        val uuid: String = player.uuidAsString

        NameHandler.setName(uuid, newName)

        context.source.sendFeedback({Text.of("Changed name to $newName")}, false)

        val playerManager = context.source.server.playerManager
        playerManager.sendToAll(PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player))

        return 0
    }
}