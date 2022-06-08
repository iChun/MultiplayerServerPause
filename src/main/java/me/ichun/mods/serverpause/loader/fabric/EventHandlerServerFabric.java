package me.ichun.mods.serverpause.loader.fabric;

import me.ichun.mods.serverpause.common.core.EventHandlerServer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class EventHandlerServerFabric extends EventHandlerServer
{
    public EventHandlerServerFabric()
    {
        ServerPlayConnectionEvents.INIT.register((handler, server) -> onPlayerLogin(handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> onPlayerLogout(handler.getPlayer()));

        ServerLifecycleEvents.SERVER_STARTING.register(server -> resetServer(server, true));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> resetServer(server, false));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerPauseCommand(dispatcher));
    }
}
