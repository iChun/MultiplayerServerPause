package me.ichun.mods.serverpause.common.core;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.serverpause.common.ServerPause;
import org.jetbrains.annotations.NotNull;

public class Config extends ConfigBase
{
    public boolean pauseWhenAllPlayersPaused = true;
    public boolean pauseWhenNoPlayers = false;
    public boolean sendChatMessageWhenPauseStateChanges = false;
    public boolean sendChatMessageWhenPlayerPauseStateChanges = false;

    @NotNull
    @Override
    public String getModId()
    {
        return ServerPause.MOD_ID;
    }

    @NotNull
    @Override
    public String getConfigName()
    {
        return ServerPause.MOD_NAME;
    }
}
