package me.ichun.mods.serverpause.common.network.packet;

import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.common.network.AbstractPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

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
        //Boolean switch, doesn't need to be thread safe
        ServerPause.eventHandlerClient.serverPause = pauseState;
        return Optional.empty();
    }
}
