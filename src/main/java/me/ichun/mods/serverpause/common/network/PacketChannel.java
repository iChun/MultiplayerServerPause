package me.ichun.mods.serverpause.common.network;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public abstract class PacketChannel
{
    protected final ResourceLocation channelId;
    protected final Object2ByteOpenHashMap<Class<? extends AbstractPacket>> clzToId;
    protected final Class<? extends AbstractPacket>[] idToClz;

    @SafeVarargs
    public PacketChannel(ResourceLocation name, Class<? extends AbstractPacket>...packetTypes)
    {
        channelId = name;

        clzToId = new Object2ByteOpenHashMap<>(packetTypes.length);
        for(int i = 0; i < packetTypes.length; i++)
        {
            clzToId.put(packetTypes[i], (byte)i);
        }
        idToClz = packetTypes;
    }

    public abstract void sendToServer(AbstractPacket packet);

    public abstract void sendTo(AbstractPacket packet, ServerPlayerEntity player);

    public abstract void sendToAll(AbstractPacket packet);

    public abstract void sendToTracking(AbstractPacket packet, Entity entity);

    public abstract void sendToAround(AbstractPacket packet, ServerWorld world, double x, double y, double z, double radius);

}
