package me.ichun.mods.serverpause.loader.forge;

import me.ichun.mods.serverpause.common.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigForge extends Config
{
    public ConfigForge(ForgeConfigSpec.Builder builder)
    {
        builder.push("general");

        final ForgeConfigSpec.BooleanValue configPauseWhenAllPlayersPaused = builder.comment("Set to true to pause the server when all connected players are paused.").define("pauseWhenAllPlayersPaused", true);
        pauseWhenAllPlayersPaused = new ConfigWrapper<>(configPauseWhenAllPlayersPaused::get, configPauseWhenAllPlayersPaused::set, configPauseWhenAllPlayersPaused::save);

        final ForgeConfigSpec.BooleanValue configPauseWhenNoPlayers = builder.comment("Set to true to pause the server when no players are connected.").define("pauseWhenNoPlayers", false);
        pauseWhenNoPlayers = new ConfigWrapper<>(configPauseWhenNoPlayers::get, configPauseWhenNoPlayers::set, configPauseWhenNoPlayers::save);

        final ForgeConfigSpec.BooleanValue configSendChatMessageWhenPauseStateChanges = builder.comment("Set to true to send a chat message to all players (and to the server log) when the server pause state changes.").define("sendChatMessageWhenPauseStateChanges", false);
        sendChatMessageWhenPauseStateChanges = new ConfigWrapper<>(configSendChatMessageWhenPauseStateChanges::get, configSendChatMessageWhenPauseStateChanges::set, configSendChatMessageWhenPauseStateChanges::save);

        final ForgeConfigSpec.BooleanValue configSendChatMessageWhenPlayerPauseStateChanges = builder.comment("Set to true to send a chat message to all players (and to the server log) when the a player's pause state changes.").define("sendChatMessageWhenPlayerPauseStateChanges", false);
        sendChatMessageWhenPlayerPauseStateChanges = new ConfigWrapper<>(configSendChatMessageWhenPlayerPauseStateChanges::get, configSendChatMessageWhenPlayerPauseStateChanges::set, configSendChatMessageWhenPlayerPauseStateChanges::save);

        builder.pop();
    }
}
