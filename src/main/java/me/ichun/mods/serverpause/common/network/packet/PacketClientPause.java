package me.ichun.mods.serverpause.common.network.packet;

import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.common.network.AbstractPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

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
    public void writeTo(FriendlyByteBuf buf)
    {
        buf.writeBoolean(pauseState);
    }

    @Override
    public void readFrom(FriendlyByteBuf buf)
    {
        pauseState = buf.readBoolean();
    }

    @Override
    public Optional<Runnable> process(Player player)
    {
        return Optional.of(() -> ServerPause.eventHandlerServer.updatePlayerState(player, pauseState));
    }
}
