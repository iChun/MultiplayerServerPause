package me.ichun.mods.serverpause.common.core;

import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.common.network.packet.PacketServerPause;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class EventHandlerServer
{
    public HashMap<UUID, Boolean> pauseState = new HashMap<>();
    public boolean isPaused;

    public boolean serverPaused; //instance of the server's last pause state

    public void onPlayerLogin(Player player)
    {
        pauseState.put(player.getGameProfile().getId(), false);
        checkAndUpdatePauseState();
    }

    public void onPlayerLogout(Player player)
    {
        pauseState.remove(player.getGameProfile().getId());
        checkAndUpdatePauseState();
    }

    public void updatePlayerState(Player player, boolean paused)
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
                ServerPause.modProxy.getServer().sendMessage(new TextComponent(isPaused ? "Server paused." : "Server unpaused."), Util.NIL_UUID);
                TranslatableComponent chat = new TranslatableComponent(isPaused ? "serverpause.message.paused" : "serverpause.message.unpaused");
                for(ServerPlayer player : ServerPause.modProxy.getServer().getPlayerList().getPlayers())
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
