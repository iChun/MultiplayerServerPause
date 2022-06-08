package me.ichun.mods.serverpause.mixin;

import me.ichun.mods.serverpause.common.ServerPause;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    //Thanks https://github.com/SpongePowered/Mixin/issues/209 for being reference to how I figured out @ModifyVariable
    @ModifyVariable(method = "runTick", //Method to check
            at = @At("STORE"),
            ordinal = 2, //third matching local variable of our matching type (boolean) (the method args counts as one as well)
            require = 1 //require at least 1 success
    )
    private boolean onPauseCheck(boolean pause)
    {
        Minecraft mc = ((Minecraft)(Object)this);
        return mc.getConnection() != null && mc.getConnection().getConnection().isConnected() && ServerPause.eventHandlerClient.serverPause || pause;
    }
}
