package me.ichun.mods.serverpause.loader.fabric;

import me.ichun.mods.serverpause.common.network.AbstractPacket;
import me.ichun.mods.serverpause.common.network.PacketChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class PacketChannelFabric extends PacketChannel
{
    public MinecraftServer serverInstance;

    @SafeVarargs
    public PacketChannelFabric(ResourceLocation name, Class<? extends AbstractPacket>... packetTypes)
    {
        super(name, packetTypes);

        //Fabric doesn't do any network protocol checks.

        //receiving the packet
        ServerPlayNetworking.registerGlobalReceiver(channelId, (server, player, handler, buffer, responseSender) -> {
            AbstractPacket packet = readPacket(buffer);
            packet.process(player).ifPresent(server::submit);
        });
        if(FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT))
        {
            ClientClassloaderHaxor.registerClientReceiver(channelId, this);
        }

        //Register our server listeners
        ServerLifecycleEvents.SERVER_STARTING.register(server -> serverInstance = server);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> serverInstance = null);
    }

    public FriendlyByteBuf writePacket(AbstractPacket packet)
    {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeByte(clzToId.getByte(packet.getClass()));
        packet.writeTo(buffer);

        return buffer;
    }

    public AbstractPacket readPacket(FriendlyByteBuf buffer)
    {
        byte id = buffer.readByte();
        Class<? extends AbstractPacket> clz = idToClz[id];
        AbstractPacket packet;
        try
        {
            packet = clz.getDeclaredConstructor().newInstance();
            packet.readFrom(buffer);
        }
        catch(NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ignored)
        {
            throw new RuntimeException("Unable to create packet for " + channelId.toString() + " with id " + id);
        }
        return packet;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void sendToServer(AbstractPacket packet)
    {
        ClientPlayNetworking.send(channelId, writePacket(packet));
    }

    @Override
    public void sendTo(AbstractPacket packet, ServerPlayer player)
    {
        ServerPlayNetworking.send(player, channelId, writePacket(packet));
    }

    @Override
    public void sendToAll(AbstractPacket packet)
    {
        sendTo(packet, PlayerLookup.all(serverInstance));
    }

    @Override
    public void sendToTracking(AbstractPacket packet, Entity entity)
    {
        sendTo(packet, PlayerLookup.tracking(entity));
    }

    @Override
    public void sendToAround(AbstractPacket packet, ServerLevel world, double x, double y, double z, double radius)
    {
        sendTo(packet, PlayerLookup.around(world, new Vec3(x, y, z), radius));
    }

    private void sendTo(AbstractPacket packet, Collection<ServerPlayer> players)
    {
        FriendlyByteBuf buf = writePacket(packet);
        for(ServerPlayer player : players)
        {
            ServerPlayNetworking.send(player, channelId, new FriendlyByteBuf(buf.copy()));
        }
    }

    @Environment(EnvType.CLIENT)
    public static class ClientClassloaderHaxor
    {
        @Environment(EnvType.CLIENT)
        public static void registerClientReceiver(ResourceLocation channelId, PacketChannelFabric channel)
        {
            ClientPlayNetworking.registerGlobalReceiver(channelId, (client, handler, buffer, responseSender) -> {
                AbstractPacket packet = channel.readPacket(buffer);
                packet.process(client.player).ifPresent(client::submit);
            });
        }
    }
}
