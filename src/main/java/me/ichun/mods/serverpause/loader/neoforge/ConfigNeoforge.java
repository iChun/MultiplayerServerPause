package me.ichun.mods.serverpause.loader.neoforge;

import me.ichun.mods.serverpause.common.core.Config;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigNeoforge extends Config
{
    public ConfigNeoforge(ModConfigSpec.Builder builder)
    {
        builder.push("general");

        final ModConfigSpec.BooleanValue configPauseWhenAllPlayersPaused = builder.comment(Reference.PAUSE_WHEN_ALL_PLAYERS_PAUSED_COMMENT).define("pauseWhenAllPlayersPaused", true);
        pauseWhenAllPlayersPaused = new ConfigWrapper<>(configPauseWhenAllPlayersPaused::get, configPauseWhenAllPlayersPaused::set, configPauseWhenAllPlayersPaused::save);

        final ModConfigSpec.BooleanValue configPauseWhenNoPlayers = builder.comment(Reference.PAUSE_WHEN_NO_PLAYERS_COMMENT).define("pauseWhenNoPlayers", false);
        pauseWhenNoPlayers = new ConfigWrapper<>(configPauseWhenNoPlayers::get, configPauseWhenNoPlayers::set, configPauseWhenNoPlayers::save);

        final ModConfigSpec.BooleanValue configSendChatMessageWhenPauseStateChanges = builder.comment(Reference.SEND_CHAT_MESSAGE_WHEN_PAUSE_STATE_CHANGES_COMMENT).define("sendChatMessageWhenPauseStateChanges", false);
        sendChatMessageWhenPauseStateChanges = new ConfigWrapper<>(configSendChatMessageWhenPauseStateChanges::get, configSendChatMessageWhenPauseStateChanges::set, configSendChatMessageWhenPauseStateChanges::save);

        final ModConfigSpec.BooleanValue configSendChatMessageWhenPlayerPauseStateChanges = builder.comment(Reference.SEND_CHAT_MESSAGE_WHEN_PLAYER_PAUSE_STATE_CHANGES_COMMENT).define("sendChatMessageWhenPlayerPauseStateChanges", false);
        sendChatMessageWhenPlayerPauseStateChanges = new ConfigWrapper<>(configSendChatMessageWhenPlayerPauseStateChanges::get, configSendChatMessageWhenPlayerPauseStateChanges::set, configSendChatMessageWhenPlayerPauseStateChanges::save);

        builder.pop();
    }
}
