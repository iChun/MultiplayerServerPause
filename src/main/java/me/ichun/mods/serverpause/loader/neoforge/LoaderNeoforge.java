package me.ichun.mods.serverpause.loader.neoforge;

import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.loader.neoforge.client.EventHandlerClientNeoforge;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@Mod(ServerPause.MOD_ID)
public class LoaderNeoforge extends ServerPause
{
    public LoaderNeoforge(IEventBus modEventBus)
    {
        modProxy = this;

        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::registerPayloadHandler);

        NeoForge.EVENT_BUS.register(eventHandlerServer = new EventHandlerServerNeoforge());

        //register config
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
        config = new ConfigNeoforge(configBuilder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configBuilder.build());
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        NeoForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClientNeoforge());
    }

    private void registerPayloadHandler(RegisterPayloadHandlerEvent event)
    {
        channel = new PacketChannelNeoforge(event, CHANNEL_ID, NETWORK_PROTOCOL, PACKET_TYPES);
    }

    @Override
    public MinecraftServer getServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
    }
}
