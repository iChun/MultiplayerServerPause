package me.ichun.mods.serverpause.loader.forge;

import me.ichun.mods.serverpause.common.core.EventHandlerServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlerServerForge extends EventHandlerServer
{
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        onPlayerLogin(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        onPlayerLogout(event.getPlayer());
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
}
