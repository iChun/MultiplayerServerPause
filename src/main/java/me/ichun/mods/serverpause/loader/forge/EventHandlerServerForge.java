package me.ichun.mods.serverpause.loader.forge;

import me.ichun.mods.serverpause.common.core.EventHandlerServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
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
    public void onServerStarting(FMLServerStartingEvent event)
    {
        resetServer(event.getServer(), true);
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        resetServer(event.getServer(), false);
    }
}
