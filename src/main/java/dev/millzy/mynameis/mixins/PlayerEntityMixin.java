package dev.millzy.mynameis.mixins;
import dev.millzy.mynameis.NameHandler;
import dev.millzy.mynameis.config.Config;
import dev.millzy.mynameis.config.ConfigManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Unique private Text cachedName = null;
    @Unique private int cachedAge = -999;
    @Unique private boolean ignoreNextCall = false;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team;decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
    private Text replaceName(Text text) {
        Config config = ConfigManager.INSTANCE.getConfig();
        assert config != null;

        if (!config.getModEnabled()) {
            return text;
        }

        if (cachedAge == age) {
            return cachedName;
        }

        if (!ignoreNextCall) {
            ignoreNextCall = true;

            String name = NameHandler.INSTANCE.getName(uuidString);
            if (name != null) {
                Text nameText = Text.of(name);

                ignoreNextCall = false;
                cachedName = nameText;
                cachedAge = age;

                return nameText;
            }
        }

        return text;
    }
}
