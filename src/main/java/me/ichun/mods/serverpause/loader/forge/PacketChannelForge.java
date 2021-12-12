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
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Predicate;

public class PacketChannelForge extends PacketChannel
{
    private final SimpleChannel channel;

    @SafeVarargs
    public PacketChannelForge(ResourceLocation name, String protocolVersion, Class<? extends AbstractPacket>...packetTypes)
    {
        this(name, protocolVersion, true, true, packetTypes);
    }

    @SafeVarargs
    public PacketChannelForge(ResourceLocation name, String protocolVersion, boolean clientRequired, boolean serverRequired, Class<? extends AbstractPacket>...packetTypes)
    {
        this(name, protocolVersion, o -> protocolVersion.equals(o) || !serverRequired, o -> protocolVersion.equals(o) || !clientRequired, packetTypes);
    }

    @SafeVarargs
    public PacketChannelForge(ResourceLocation name, String protocolVersion, Predicate<String> clientPredicate, Predicate<String> serverPredicate, Class<? extends AbstractPacket>...packetTypes)
    {
        super(name, packetTypes);

        channel = NetworkRegistry.newSimpleChannel(name, () -> protocolVersion, clientPredicate, serverPredicate);
        channel.registerMessage(0, PacketHolder.class,
                (packet, buffer) -> {
                    buffer.writeByte(clzToId.getByte(packet.packet.getClass()));
                    packet.packet.writeTo(buffer);
                },
                (buffer) -> {
                    Class<? extends AbstractPacket> clz = idToClz[buffer.readByte()];
                    AbstractPacket packet = null;
                    try
                    {
                        packet = clz.newInstance();
                        packet.readFrom(buffer);
                    }
                    catch(InstantiationException | IllegalAccessException ignored){}
                    return new PacketHolder(packet);
                },
                (packet, contextSupplier) -> {
                    NetworkEvent.Context context = contextSupplier.get();
                    packet.packet.process(context.getDirection() == NetworkDirection.PLAY_TO_SERVER ? context.getSender() : getPlayer()).ifPresent(context::enqueueWork);
                    context.setPacketHandled(true);
                });
    }

    @Override
    public void sendToServer(AbstractPacket packet)
    {
        channel.sendToServer(new PacketHolder(packet));
    }

    @Override
    public void sendTo(AbstractPacket packet, ServerPlayer player)
    {
        channel.sendTo(new PacketHolder(packet), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public void sendToAll(AbstractPacket packet)
    {
        channel.send(PacketDistributor.ALL.noArg(), new PacketHolder(packet));
    }

    @Override
    public void sendToTracking(AbstractPacket packet, Entity entity)
    {
        channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
    }

    @Override
    public void sendToAround(AbstractPacket packet, ServerLevel world, double x, double y, double z, double radius)
    {
        channel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, radius, world.dimension())), packet);
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
