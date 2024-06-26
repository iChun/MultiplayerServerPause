package me.ichun.mods.serverpause.loader.forge;

import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.forge.PacketChannelForge;
import me.ichun.mods.serverpause.client.core.EventHandlerClient;
import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.common.core.Config;
import me.ichun.mods.serverpause.common.core.EventHandlerServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ServerPause.MOD_ID)
public class LoaderForge extends ServerPause
{
    public LoaderForge()
    {
        modProxy = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        eventHandlerServer = new EventHandlerServer();

        channel = new PacketChannelForge(CHANNEL_ID, NETWORK_PROTOCOL, PACKET_TYPES);

        config = iChunUtil.d().registerConfig(new Config());
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        initClient();
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient()
    {
        eventHandlerClient = new EventHandlerClient();

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((mc, screen) -> new WorkspaceConfigs(screen)));
    }
}
