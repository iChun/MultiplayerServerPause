package me.ichun.mods.serverpause.loader.neoforge;

import io.netty.buffer.Unpooled;
import me.ichun.mods.serverpause.common.network.AbstractPacket;
import me.ichun.mods.serverpause.common.network.PacketChannel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.lang.reflect.InvocationTargetException;

public class PacketChannelNeoforge extends PacketChannel
{
    public PacketChannelNeoforge(RegisterPayloadHandlerEvent event, ResourceLocation name, int protocolVersion, Class<? extends AbstractPacket>... packetTypes)
    {
        super(name, packetTypes);

        event.registrar(name.getNamespace())
            .versioned(Integer.toString(protocolVersion))
            .play(name, this::readPacket, PacketPayload::process);
    }

    private PacketPayload asPayload(AbstractPacket packet)
    {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer()); // Taken from Fabric's PacketByteBufs.create();
        return writePacket(packet, buffer);
    }

    private PacketPayload writePacket(AbstractPacket packet, FriendlyByteBuf buffer)
    {
        PacketPayload payload = new PacketPayload(packet);
        payload.write(buffer);
        return payload;
    }

    private PacketPayload readPacket(FriendlyByteBuf buffer)
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
        return new PacketPayload(packet);
    }

    @Override
    public void sendToServer(AbstractPacket packet)
    {
        PacketDistributor.SERVER.noArg().send(asPayload(packet));
    }

    @Override
    public void sendTo(AbstractPacket packet, ServerPlayer player)
    {
        PacketDistributor.PLAYER.with(player).send(asPayload(packet));
    }

    @Override
    public void sendToAll(AbstractPacket packet)
    {
        PacketDistributor.ALL.noArg().send(asPayload(packet));
    }

    @Override
    public void sendToTracking(AbstractPacket packet, Entity entity)
    {
        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity).send(asPayload(packet));
    }

    @Override
    public void sendToAround(AbstractPacket packet, ServerLevel world, double x, double y, double z, double radius)
    {
        PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(x, y, z, radius, world.dimension())).send(asPayload(packet));
    }

    private class PacketPayload implements CustomPacketPayload
    {
        private final AbstractPacket packet;

        private PacketPayload(AbstractPacket packet)
        {
            this.packet = packet;
        }

        @Override
        public void write(FriendlyByteBuf buffer)
        {
            buffer.writeByte(clzToId.getByte(packet.getClass()));
            packet.writeTo(buffer);
        }

        @Override
        public ResourceLocation id()
        {
            return channelId;
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        public void process(PlayPayloadContext context)
        {
            Player player = context.player().get(); //should be set unless we're in configuration stage;

            packet.process(player).ifPresent(r -> context.workHandler().submitAsync(r));
        }
    }
}
