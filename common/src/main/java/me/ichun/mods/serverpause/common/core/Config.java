package me.ichun.mods.serverpause.common.core;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.compat.CompatHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Config extends ConfigBase
{
    public boolean pauseWhenAllPlayersPaused = true;
    public boolean pauseWhenNoPlayers = false;
    public boolean sendChatMessageWhenPauseStateChanges = false;
    public boolean sendChatMessageWhenPlayerPauseStateChanges = false;

    @Prop(validator = "validateCompatibilities")
    public List<String> disabledCompatibilities = new ArrayList<>();

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

    public boolean validateCompatibilities(Object o)
    {
        if(o instanceof String s)
        {
            return CompatHandler.REGISTERED_COMPATS.containsKey(s);
        }
        return false;
    }
}
