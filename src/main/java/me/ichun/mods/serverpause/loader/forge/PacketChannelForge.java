package me.ichun.mods.serverpause.loader.forge;

import me.ichun.mods.serverpause.common.network.AbstractPacket;
import me.ichun.mods.serverpause.common.network.PacketChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class PacketChannelForge extends PacketChannel
{
    private final SimpleChannel channel;

    @SafeVarargs
    public PacketChannelForge(ResourceLocation name, int protocolVersion, Class<? extends AbstractPacket>...packetTypes)
    {
        this(name, protocolVersion, true, true, packetTypes);
    }

    @SafeVarargs
    public PacketChannelForge(ResourceLocation name, int protocolVersion, boolean clientRequired, boolean serverRequired, Class<? extends AbstractPacket>...packetTypes)
    {
        super(name, packetTypes);

        ChannelBuilder channelBuilder = ChannelBuilder.named(name).networkProtocolVersion(protocolVersion);
        if(!clientRequired)
        {
            channelBuilder = channelBuilder.optionalClient();
        }
        if(!serverRequired)
        {
            channelBuilder = channelBuilder.optionalServer();
        }
        channel = channelBuilder.simpleChannel();
        channel.messageBuilder(PacketHolder.class)
            .encoder((packet, buffer) -> {
                buffer.writeByte(clzToId.getByte(packet.packet.getClass()));
                packet.packet.writeTo(buffer);
            })
            .decoder((buffer) -> {
                Class<? extends AbstractPacket> clz = idToClz[buffer.readByte()];
                AbstractPacket packet = null;
                try
                {
                    packet = clz.newInstance();
                    packet.readFrom(buffer);
                }
                catch(InstantiationException | IllegalAccessException ignored){}
                return new PacketHolder(packet);
            })
            .consumerNetworkThread((packet, context) -> {
                packet.packet.process(context.getDirection() == NetworkDirection.PLAY_TO_SERVER ? context.getSender() : getPlayer()).ifPresent(context::enqueueWork);
                context.setPacketHandled(true);
            })
            .add();
    }

    @Override
    public void sendTo(AbstractPacket packet, ServerPlayer player)
    {
        channel.send(new PacketHolder(packet), PacketDistributor.PLAYER.with(player));
    }

    @Override
    public void sendToAll(AbstractPacket packet)
    {
        channel.send(new PacketHolder(packet), PacketDistributor.ALL.noArg());
    }

    @Override
    public void sendToTracking(AbstractPacket packet, Entity entity)
    {
        channel.send(packet, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
    }

    @Override
    public void sendToAround(AbstractPacket packet, ServerLevel world, double x, double y, double z, double radius)
    {
        channel.send(packet, PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(x, y, z, radius, world.dimension())));
    }

    @Override
    public void sendToServer(AbstractPacket packet)
    {
        if(FMLEnvironment.dist.isClient())
        {
            sendToServerImpl(packet);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void sendToServerImpl(AbstractPacket packet)
    {
        channel.send(new PacketHolder(packet), Minecraft.getInstance().getConnection().getConnection());
    }

    @OnlyIn(Dist.CLIENT)
    public static Player getPlayer()
    {
        return Minecraft.getInstance().player;
    }

    public static class PacketHolder
    {
        private AbstractPacket packet;
        private PacketHolder(AbstractPacket packet)
        {
            this.packet = packet;
        }
    }
}
