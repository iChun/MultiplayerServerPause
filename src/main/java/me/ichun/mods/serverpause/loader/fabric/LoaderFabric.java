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
        Config modConfig = new Config(MOD_ID, new String[]{MOD_ID}, configFabric);
        modConfig.load();
        Runtime.getRuntime().addShutdownHook(new Thread(modConfig::save));
    }

    @Override
    public MinecraftServer getServer()
    {
        return ((PacketChannelFabric)channel).serverInstance;
    }
}
