package me.ichun.mods.serverpause.loader.fabric;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.fabric.PacketChannelFabric;
import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.common.core.Config;
import me.ichun.mods.serverpause.common.core.EventHandlerServer;
import net.fabricmc.api.ModInitializer;

public class LoaderFabric extends ServerPause
        implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        modProxy = this;

        eventHandlerServer = new EventHandlerServer();

        channel = new PacketChannelFabric(CHANNEL_ID, PACKET_TYPES);

        //register config
        config = iChunUtil.d().registerConfig(new Config());
    }
}
