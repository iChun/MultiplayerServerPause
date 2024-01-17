package me.ichun.mods.serverpause.mixin;

import me.ichun.mods.serverpause.common.ServerPause;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin
{
    @Inject(method = "doTick", at = @At("HEAD"), cancellable = true)
    private void serverpause_onDoTick(CallbackInfo ci)
    {
        if(ServerPause.eventHandlerServer.serverPaused)
        {
            ci.cancel();
        }
    }
}
