package me.ichun.mods.serverpause.loader.forge;

import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.loader.forge.client.EventHandlerClientForge;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod(ServerPause.MOD_ID)
public class LoaderForge extends ServerPause
{
    public LoaderForge()
    {
        modProxy = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        MinecraftForge.EVENT_BUS.register(eventHandlerServer = new EventHandlerServerForge());

        channel = new PacketChannelForge(CHANNEL_ID, NETWORK_PROTOCOL, PACKET_TYPES);

        //register config
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        config = new ConfigForge(configBuilder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configBuilder.build());
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClientForge());
    }

    @Override
    public MinecraftServer getServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
    }
}
