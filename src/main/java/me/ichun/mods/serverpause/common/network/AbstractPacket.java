package me.ichun.mods.serverpause.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.Optional;

public abstract class AbstractPacket //Yes I know we could be an interface.
{
    public abstract void writeTo(PacketBuffer buf);
    public abstract void readFrom(PacketBuffer buf);
    public abstract Optional<Runnable> process(PlayerEntity player); //done on networking thread. BEL is usually the client or server object. Player is the Player object who sent the packet on the server and the Minecraft Player on the client.
}
