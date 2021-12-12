package me.ichun.mods.serverpause.common.network.packet;

import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.common.network.AbstractPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.Optional;

public class PacketClientPause extends AbstractPacket
{
    public boolean pauseState;

    public PacketClientPause(){}

    public PacketClientPause(boolean pauseState)
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
        return Optional.of(() -> ServerPause.eventHandlerServer.updatePlayerState(player, pauseState));
    }
}
