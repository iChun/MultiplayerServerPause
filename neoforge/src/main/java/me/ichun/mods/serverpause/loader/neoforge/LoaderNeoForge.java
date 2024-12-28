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
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.function.Supplier;

@Mod(ServerPause.MOD_ID)
public class LoaderNeoForge extends ServerPause
{
    public LoaderNeoForge(IEventBus eventBus, ModContainer container)
    {
        modProxy = this;

        //client config
        if(FMLEnvironment.dist.isClient())
        {
            initClient(container);
        }
        eventBus.addListener(this::registerPayloadHandler);

        eventHandlerServer = new EventHandlerServer();

        config = iChunUtil.d().registerConfig(new Config(), eventBus);
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient(ModContainer container)
    {
        eventHandlerClient = new EventHandlerClient();

        container.registerExtensionPoint(IConfigScreenFactory.class, (Supplier<IConfigScreenFactory>)() -> (modContainer, screen) -> new WorkspaceConfigs(screen, MOD_ID));
    }

    private void registerPayloadHandler(RegisterPayloadHandlersEvent event)
    {
        channel = new PacketChannelNeoForge(event, CHANNEL_ID, NETWORK_PROTOCOL, PACKET_TYPES);
    }
}
