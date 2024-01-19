package me.ichun.mods.serverpause.loader.neoforge;

import me.ichun.mods.serverpause.common.core.EventHandlerServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

public class EventHandlerServerNeoforge extends EventHandlerServer
{
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        onPlayerLogin(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        onPlayerLogout(event.getEntity());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        resetServer(event.getServer(), true);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event)
    {
        resetServer(event.getServer(), false);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event)
    {
        registerPauseCommand(event.getDispatcher());
    }
}
