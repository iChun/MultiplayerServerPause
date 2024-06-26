package me.ichun.mods.serverpause.loader.neoforge;

import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.neoforge.PacketChannelNeoForge;
import me.ichun.mods.serverpause.client.core.EventHandlerClient;
import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.common.core.Config;
import me.ichun.mods.serverpause.common.core.EventHandlerServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;

@Mod(ServerPause.MOD_ID)
public class LoaderNeoForge extends ServerPause
{
    public LoaderNeoForge(IEventBus eventBus)
    {
        modProxy = this;

        eventBus.addListener(this::onClientSetup);
        eventBus.addListener(this::registerPayloadHandler);

        eventHandlerServer = new EventHandlerServer();

        config = iChunUtil.d().registerConfig(new Config(), eventBus);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        initClient();
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient()
    {
        eventHandlerClient = new EventHandlerClient();

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new WorkspaceConfigs(screen)));
    }

    private void registerPayloadHandler(RegisterPayloadHandlerEvent event)
    {
        channel = new PacketChannelNeoForge(event, CHANNEL_ID, NETWORK_PROTOCOL, PACKET_TYPES);
    }
}
