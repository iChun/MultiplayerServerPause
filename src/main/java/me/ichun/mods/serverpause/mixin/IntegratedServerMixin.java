package me.ichun.mods.serverpause.mixin;

import me.ichun.mods.serverpause.common.core.MinecraftServerMethods;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin
{
    @Inject(method = "tickServer", at = @At("HEAD"), cancellable = true)
    private void serverpause_onTickServer(BooleanSupplier hasTimeLeft, CallbackInfo ci)
    {
        MinecraftServer server = ((IntegratedServer)(Object)this);

        MinecraftServerMethods.onTickServer(server, ci);
    }
}
