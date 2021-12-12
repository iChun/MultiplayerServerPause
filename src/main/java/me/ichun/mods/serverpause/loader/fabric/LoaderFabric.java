package me.ichun.mods.serverpause.loader.fabric;

import me.ichun.mods.serverpause.common.ServerPause;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;

public class LoaderFabric extends ServerPause
        implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        modProxy = this;

        eventHandlerServer = new EventHandlerServerFabric();

        channel = new PacketChannelFabric(CHANNEL_ID, PACKET_TYPES);

        //register config
        ConfigFabric configFabric = new ConfigFabric();
        config = configFabric;
		Config.builder(MOD_ID).setBranch(new String[]{MOD_ID}).add(configFabric).saveOnExit().build();
    }

    @Override
    public MinecraftServer getServer()
    {
        return ((PacketChannelFabric)channel).serverInstance;
    }
}
