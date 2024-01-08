package me.ichun.mods.serverpause.common.core;

import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.mixin.MinecraftServerAccessorMixin;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public abstract class MinecraftServerMethods
{
    private static final Logger LOGGER = LogManager.getLogger();

    //Mixin methods
    public static void onTickServer(MinecraftServer server, CallbackInfo ci)
    {
        //Taken from IntegratedServer's tickServer
        boolean isServerPaused = ServerPause.eventHandlerServer.serverPaused;
        ServerPause.eventHandlerServer.serverPaused = ServerPause.eventHandlerServer.isPaused;

        ProfilerFiller profilerFiller = server.getProfiler();
        if(!isServerPaused && ServerPause.eventHandlerServer.serverPaused)
        {
            profilerFiller.push("autoSave");
            LOGGER.info("Saving and pausing game...");
            server.saveEverything(false, false, false);
            profilerFiller.pop();
        }

        if(ServerPause.eventHandlerServer.serverPaused)
        {
            //taken from tickChildren. Necessary so that the server still responds to connect attempts and commands.
            //Basically tick everything except the worlds and players
            server.getPlayerList().getPlayers().forEach((player) -> {
                player.connection.suspendFlushing();
            });
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
            profilerFiller.popPush("send chunks");

            for(ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.connection.chunkSender.sendNextChunks(player);
                player.connection.resumeFlushing();
            }
            profilerFiller.pop();

            if(server.isDedicatedServer())
            {
                ((DedicatedServer)server).handleConsoleInputs();

                //This is so that if there are no players on the server, it doesn't show "1/XYZ"
                long l = Util.getNanos();
                if (l - ((MinecraftServerAccessorMixin)server).getLastServerStatus() >= 5000000000L) {
                    ((MinecraftServerAccessorMixin)server).setLastServerStatus(l);
                    ((MinecraftServerAccessorMixin)server).setStatus(((MinecraftServerAccessorMixin)server).invokeBuildServerStatus());
                }
            }

            ci.cancel(); //cancel the tick
        }
    }
}
