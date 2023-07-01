package me.ichun.mods.serverpause.loader.fabric.client;

import me.ichun.mods.serverpause.common.ServerPause;
import net.fabricmc.api.ClientModInitializer;

public class LoaderFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ServerPause.eventHandlerClient = new EventHandlerClientFabric();
    }
}
