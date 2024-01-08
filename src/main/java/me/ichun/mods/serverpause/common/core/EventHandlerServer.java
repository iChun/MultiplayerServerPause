package me.ichun.mods.serverpause.common.core;

import com.mojang.brigadier.CommandDispatcher;
import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.common.network.packet.PacketServerPause;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

    public boolean forcePause;
    public boolean wasForcePaused;

    public void onPlayerLogin(Player player)
    {
        pauseState.put(player.getGameProfile().getId(), false);
        checkAndUpdatePauseState();
        //send message if config for per-player
        if(!(!ServerPause.modProxy.getServer().isDedicatedServer() && pauseState.size() == 1) && ServerPause.config.sendChatMessageWhenPlayerPauseStateChanges.get())
        {
            int pauseCount = 0;
            for(Map.Entry<UUID, Boolean> e : pauseState.entrySet())
            {
                if(e.getValue())
                {
                    pauseCount++;
                }
            }
            if(pauseCount > 0)
            {
                String prefix = pauseCount + "/" + pauseState.size(); // X/Y paused.
                MutableComponent chat = Component.translatable("serverpause.message.playersPaused", prefix);
                player.sendSystemMessage(chat);
            }
        }

        if(isPaused)
        {
            ServerPause.channel.sendTo(new PacketServerPause(true), (ServerPlayer)player);
            if(!(!ServerPause.modProxy.getServer().isDedicatedServer() && pauseState.size() == 1) && (forcePause || wasForcePaused || ServerPause.config.sendChatMessageWhenPauseStateChanges.get()))
            {
                player.sendSystemMessage(Component.translatable("serverpause.message.paused"));
            }
        }
    }

    public void onPlayerLogout(Player player)
    {
        pauseState.remove(player.getGameProfile().getId());
        checkAndUpdatePauseState();
    }

    public void updatePlayerState(Player player, boolean paused)
    {
        Boolean wasPaused = pauseState.put(player.getGameProfile().getId(), paused);
        //send message if config for per-player
        if(ServerPause.modProxy.getServer() != null && !(!ServerPause.modProxy.getServer().isDedicatedServer() && pauseState.size() == 1) && ServerPause.config.sendChatMessageWhenPlayerPauseStateChanges.get() && (wasPaused == null || wasPaused != paused))
        {
            String playerName = player.getGameProfile().getName();
            int pauseCount = 0;
            for(Map.Entry<UUID, Boolean> e : pauseState.entrySet())
            {
                if(e.getValue())
                {
                    pauseCount++;
                }
            }
            String prefix = "(" + pauseCount + "/" + pauseState.size() + ") ";
            ServerPause.modProxy.getServer().sendSystemMessage(Component.literal(paused ? prefix + playerName + " paused." : prefix + playerName + " unpaused."));
            MutableComponent chat = Component.translatable(paused ? "serverpause.message.playerPaused" : "serverpause.message.playerUnpaused", prefix + playerName);
            for(ServerPlayer player1 : ServerPause.modProxy.getServer().getPlayerList().getPlayers())
            {
                player1.sendSystemMessage(chat);
            }
        }
        checkAndUpdatePauseState();
    }

    public void registerPauseCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        PauseCommand.register(dispatcher);
    }

    public void toggleForcePause()
    {
        wasForcePaused = forcePause;
        forcePause = !forcePause;

        checkAndUpdatePauseState(); //sets wasForcePause to false;
    }

    public void checkAndUpdatePauseState()
    {
        boolean shouldPause = ServerPause.config.pauseWhenAllPlayersPaused.get();
        if(pauseState.isEmpty())
        {
            shouldPause = (forcePause || ServerPause.config.pauseWhenNoPlayers.get()) && ServerPause.modProxy.getServer() != null && ServerPause.modProxy.getServer().isDedicatedServer(); //DO NOT PAUSE INTEGRATED SERVERS WITH NO PLAYERS
        }
        else if(!forcePause && ServerPause.config.pauseWhenAllPlayersPaused.get())
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

        if(isPaused != shouldPause && ServerPause.modProxy.getServer() != null)
        {
            isPaused = shouldPause;

            ServerPause.channel.sendToAll(new PacketServerPause(isPaused));

            if(!(!ServerPause.modProxy.getServer().isDedicatedServer() && pauseState.size() == 1) && (forcePause || wasForcePaused || ServerPause.config.sendChatMessageWhenPauseStateChanges.get()))
            {
                ServerPause.modProxy.getServer().sendSystemMessage(Component.literal(isPaused ? "Server paused." : "Server unpaused."));
                MutableComponent chat = Component.translatable(isPaused ? "serverpause.message.paused" : "serverpause.message.unpaused");
                for(ServerPlayer player : ServerPause.modProxy.getServer().getPlayerList().getPlayers())
                {
                    player.sendSystemMessage(chat);
                }
            }
        }
        wasForcePaused = false;
    }

    public void resetServer(MinecraftServer server, boolean starting)
    {
        pauseState.clear();
        isPaused = starting && ServerPause.config.pauseWhenNoPlayers.get() && server.isDedicatedServer(); //DO NOT PAUSE INTEGRATED SERVERS WITH NO PLAYERS
        serverPaused = false;
        forcePause = wasForcePaused = false;
    }
}
