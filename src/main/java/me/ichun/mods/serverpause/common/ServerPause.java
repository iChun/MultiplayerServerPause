package me.ichun.mods.serverpause.common;

import me.ichun.mods.serverpause.client.core.EventHandlerClient;
import me.ichun.mods.serverpause.common.core.Config;
import me.ichun.mods.serverpause.common.core.EventHandlerServer;
import me.ichun.mods.serverpause.common.network.AbstractPacket;
import me.ichun.mods.serverpause.common.network.PacketChannel;
import me.ichun.mods.serverpause.common.network.packet.PacketClientPause;
import me.ichun.mods.serverpause.common.network.packet.PacketServerPause;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerPause
{
    public static final String MOD_ID = "serverpause";
    public static final String MOD_NAME = "Multiplayer Server Pause";

    private static final Logger LOGGER = LogManager.getLogger();

    public static final String NETWORK_PROTOCOL = "1";
    public static final Class<? extends AbstractPacket>[] PACKET_TYPES = new Class[] {
            PacketClientPause.class,
            PacketServerPause.class
    };
    public static final ResourceLocation CHANNEL_ID = new ResourceLocation(MOD_ID, "channel");
    public static PacketChannel channel;

    public static ServerPause modProxy;
    public static EventHandlerServer eventHandlerServer;
    public static EventHandlerClient eventHandlerClient;

    public static Config config;

    public abstract MinecraftServer getServer();
}
