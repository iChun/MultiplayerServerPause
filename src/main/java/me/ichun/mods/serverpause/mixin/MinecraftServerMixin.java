package me.ichun.mods.serverpause.mixin;

import me.ichun.mods.serverpause.common.ServerPause;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Inject(method = "tickServer", at = @At("HEAD"), cancellable = true)
    private void onTickServer(BooleanSupplier hasTimeLeft, CallbackInfo ci)
    {
        MinecraftServer server = ((MinecraftServer)(Object)this);

        //Taken from IntegratedServer's tickServer
        boolean isServerPaused = ServerPause.eventHandlerServer.serverPaused;
        ServerPause.eventHandlerServer.serverPaused = ServerPause.eventHandlerServer.isPaused;

        ProfilerFiller profilerFiller = server.getProfiler();
        if (!isServerPaused && ServerPause.eventHandlerServer.serverPaused) {
            profilerFiller.push("autoSave");
            LOGGER.info("Saving and pausing game...");
            server.saveEverything(false, false, false);
            profilerFiller.pop();
        }

        if(ServerPause.eventHandlerServer.serverPaused)
        {
            //taken from tickChildren. Necessary so that the server still responds to connect attempts and commands.
            //Basically tick everything except the worlds
            profilerFiller.push("commandFunctions");
            server.getFunctions().tick();
            profilerFiller.popPush("connection");
            server.getConnection().tick(); //Tick the connection so things don't time out.
            profilerFiller.popPush("server gui refresh");
            List<Runnable> tickables = ((MinecraftServerAccessorMixin)server).getTickables();
            for(int i = 0; i < tickables.size(); i++)
            {
                tickables.get(i).run();
            }
            profilerFiller.pop();

            if(server.isDedicatedServer())
            {
                ((DedicatedServer)server).handleConsoleInputs();
            }

            ci.cancel(); //cancel the tick
        }
    }
}
