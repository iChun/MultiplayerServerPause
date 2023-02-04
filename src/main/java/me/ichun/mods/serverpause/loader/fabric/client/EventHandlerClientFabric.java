package me.ichun.mods.serverpause.loader.fabric.client;

import me.ichun.mods.serverpause.client.core.EventHandlerClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class EventHandlerClientFabric extends EventHandlerClient
{
    public EventHandlerClientFabric()
    {
        ClientTickEvents.END_CLIENT_TICK.register(client -> onClientTickEnd());

        ClientLoginConnectionEvents.INIT.register((handler, client) -> onClientServerConnectionChange());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> onClientServerConnectionChange());
    }
}
