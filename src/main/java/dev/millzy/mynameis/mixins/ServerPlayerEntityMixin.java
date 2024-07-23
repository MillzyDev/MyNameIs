package dev.millzy.mynameis.mixins;

import com.mojang.authlib.GameProfile;
import dev.millzy.mynameis.NameHandler;
import dev.millzy.mynameis.config.Config;
import dev.millzy.mynameis.config.ConfigManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "getPlayerListName", at = @At("TAIL"), cancellable = true)
    private void replaceListName(CallbackInfoReturnable<Text> callbackInfo) {
        Config config = ConfigManager.INSTANCE.getConfig();
        assert config != null;

        if (!config.getModEnabled()) {
            return;
        }

        String name = NameHandler.INSTANCE.getName(uuidString);

        if (name == null) {
            return;
        }

        callbackInfo.setReturnValue(
                Team.decorateName(
                        this.getScoreboardTeam(),
                        Text.of(NameHandler.INSTANCE.getName(uuidString)
                        )
                )
        );
    }
}
