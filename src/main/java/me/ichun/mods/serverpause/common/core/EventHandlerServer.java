package me.ichun.mods.serverpause.common.core;

import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.common.network.packet.PacketServerPause;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class EventHandlerServer
{
    public HashMap<UUID, Boolean> pauseState = new HashMap<>();
    public boolean isPaused;

    public boolean serverPaused; //instance of the server's last pause state

    public void onPlayerLogin(PlayerEntity player)
    {
        pauseState.put(player.getGameProfile().getId(), false);
        checkAndUpdatePauseState();
    }

    public void onPlayerLogout(PlayerEntity player)
    {
        pauseState.remove(player.getGameProfile().getId());
        checkAndUpdatePauseState();
    }

    public void updatePlayerState(PlayerEntity player, boolean paused)
    {
        pauseState.put(player.getGameProfile().getId(), paused);
        checkAndUpdatePauseState();
    }

    public void checkAndUpdatePauseState()
    {
        boolean shouldPause = true;
        if(pauseState.isEmpty())
        {
            shouldPause = ServerPause.config.pauseWhenNoPlayers.get() && ServerPause.modProxy.getServer() != null && ServerPause.modProxy.getServer().isDedicatedServer(); //DO NOT PAUSE INTEGRATED SERVERS WITH NO PLAYERS
        }
        else
        {
            for(Map.Entry<UUID, Boolean> e : pauseState.entrySet())
            {
                if(!e.getValue())
                {
                    shouldPause = false;
                    break;
                }
            }
        }

        if(isPaused != shouldPause)
        {
            isPaused = shouldPause;

            ServerPause.channel.sendToAll(new PacketServerPause(isPaused));

            if(!(!ServerPause.modProxy.getServer().isDedicatedServer() && pauseState.size() == 1) && ServerPause.config.sendChatMessageWhenPauseStateChanges.get())
            {
                ServerPause.modProxy.getServer().sendMessage(new StringTextComponent(isPaused ? "Server paused." : "Server unpaused."), Util.NIL_UUID);
                TranslationTextComponent chat = new TranslationTextComponent(isPaused ? "serverpause.message.paused" : "serverpause.message.unpaused");
                for(ServerPlayerEntity player : ServerPause.modProxy.getServer().getPlayerList().getPlayers())
                {
                    player.sendMessage(chat, ChatType.SYSTEM, Util.NIL_UUID);
                }
            }
        }
    }

    public void resetServer(MinecraftServer server, boolean starting)
    {
        pauseState.clear();
        isPaused = starting && ServerPause.config.pauseWhenNoPlayers.get() && server.isDedicatedServer(); //DO NOT PAUSE INTEGRATED SERVERS WITH NO PLAYERS
        serverPaused = false;
    }
}
