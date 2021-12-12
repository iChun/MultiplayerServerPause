package me.ichun.mods.serverpause.common.network.packet;

import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.common.network.AbstractPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.Optional;

public class PacketServerPause extends AbstractPacket
{
    public boolean pauseState;

    public PacketServerPause(){}

    public PacketServerPause(boolean pauseState)
    {
        this.pauseState = pauseState;
    }

    @Override
    public void writeTo(PacketBuffer buf)
    {
        buf.writeBoolean(pauseState);
    }

    @Override
    public void readFrom(PacketBuffer buf)
    {
        pauseState = buf.readBoolean();
    }

    @Override
    public Optional<Runnable> process(PlayerEntity player)
    {
        //Boolean switch, doesn't need to be thread safe
        ServerPause.eventHandlerClient.serverPause = pauseState;
        return Optional.empty();
    }
}
