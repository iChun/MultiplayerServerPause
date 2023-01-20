package me.ichun.mods.serverpause.loader.forge.client;

import me.ichun.mods.serverpause.client.core.EventHandlerClient;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlerClientForge extends EventHandlerClient
{
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase.equals(TickEvent.Phase.END))
        {
            onClientTickEnd();
        }
    }

    @SubscribeEvent
    public void onClientConnect(ClientPlayerNetworkEvent.LoggingIn event)
    {
        onClientServerConnectionChange();
    }

    @SubscribeEvent
    public void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event)
    {
        onClientServerConnectionChange();
    }
}
