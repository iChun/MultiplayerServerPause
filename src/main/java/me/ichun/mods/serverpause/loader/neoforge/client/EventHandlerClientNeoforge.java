package me.ichun.mods.serverpause.loader.neoforge.client;

import me.ichun.mods.serverpause.client.core.EventHandlerClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.TickEvent;

public class EventHandlerClientNeoforge extends EventHandlerClient
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
