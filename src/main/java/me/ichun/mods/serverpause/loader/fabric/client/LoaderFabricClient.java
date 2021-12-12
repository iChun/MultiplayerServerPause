package me.ichun.mods.serverpause.loader.fabric.client;

import me.ichun.mods.serverpause.common.ServerPause;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class LoaderFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ServerPause.eventHandlerClient = new EventHandlerClientFabric();

        if(FabricLoader.getInstance().isModLoaded("cloth-config"))
        {
            ConfigScreenBuilder.setMain(ServerPause.MOD_ID, new ClothConfigScreenBuilder());
        }
    }
}
