package me.ichun.mods.serverpause.client.core;

import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.common.network.packet.PacketClientPause;
import net.minecraft.client.Minecraft;

public class EventHandlerClient
{
    public boolean serverPause;

    public boolean isClientPaused;

    public void onClientTickEnd()
    {
        Minecraft mc = Minecraft.getInstance();

        boolean clientPause = mc.getConnection() != null && mc.getConnection().getConnection().isConnected() && (mc.screen != null && mc.screen.isPauseScreen() || mc.getOverlay() != null && mc.getOverlay().isPauseScreen());
        if(isClientPaused != clientPause)
        {
            isClientPaused = clientPause;

            if(mc.getConnection() != null && mc.getConnection().getConnection().isConnected() && !(mc.hasSingleplayerServer() && !mc.getSingleplayerServer().isPublished())) //if !(server is integrated and not open to lan)
            {
                ServerPause.channel.sendToServer(new PacketClientPause(isClientPaused));
            }
        }
    }

    public void onClientServerConnectionChange()
    {
        serverPause = false;
        isClientPaused = false;
    }
}
