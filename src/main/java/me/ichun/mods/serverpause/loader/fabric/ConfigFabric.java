package me.ichun.mods.serverpause.loader.fabric;

import me.ichun.mods.serverpause.common.core.Config;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

public class ConfigFabric extends Config
        implements ConfigContainer
{
    private static General INSTANCE_GENERAL = null;

    public me.lortseam.completeconfig.data.Config configInstance;

    public ConfigFabric()
    {
        pauseWhenAllPlayersPaused = new ConfigWrapper<>(() -> INSTANCE_GENERAL.pauseWhenAllPlayersPaused, v -> INSTANCE_GENERAL.pauseWhenAllPlayersPaused = v);
        pauseWhenNoPlayers = new ConfigWrapper<>(() -> INSTANCE_GENERAL.pauseWhenNoPlayers, v -> INSTANCE_GENERAL.pauseWhenNoPlayers = v);
        sendChatMessageWhenPauseStateChanges = new ConfigWrapper<>(() -> INSTANCE_GENERAL.sendChatMessageWhenPauseStateChanges, v -> INSTANCE_GENERAL.sendChatMessageWhenPauseStateChanges = v);
        sendChatMessageWhenPlayerPauseStateChanges = new ConfigWrapper<>(() -> INSTANCE_GENERAL.sendChatMessageWhenPlayerPauseStateChanges, v -> INSTANCE_GENERAL.sendChatMessageWhenPlayerPauseStateChanges = v);
    }

    @Transitive
    @ConfigEntries(includeAll = true)
    public static class General implements ConfigGroup
    {
        public General()
        {
            INSTANCE_GENERAL = this;
        }

        @ConfigEntry(nameKey = "general.pauseWhenNoPlayers.name", descriptionKey = "general.pauseWhenNoPlayers", comment = Reference.PAUSE_WHEN_ALL_PLAYERS_PAUSED_COMMENT)
        public boolean pauseWhenAllPlayersPaused = true;
        @ConfigEntry(nameKey = "general.pauseWhenNoPlayers.name", descriptionKey = "general.pauseWhenNoPlayers", comment = Reference.PAUSE_WHEN_NO_PLAYERS_COMMENT)
        public boolean pauseWhenNoPlayers = false;
        @ConfigEntry(nameKey = "general.sendChatMessageWhenPauseStateChanges.name", descriptionKey = "general.sendChatMessageWhenPauseStateChanges", comment = Reference.SEND_CHAT_MESSAGE_WHEN_PAUSE_STATE_CHANGES_COMMENT)
        public boolean sendChatMessageWhenPauseStateChanges = false;
        @ConfigEntry(nameKey = "general.sendChatMessageWhenPlayerPauseStateChanges.name", descriptionKey = "general.sendChatMessageWhenPlayerPauseStateChanges", comment = Reference.SEND_CHAT_MESSAGE_WHEN_PLAYER_PAUSE_STATE_CHANGES_COMMENT)
        public boolean sendChatMessageWhenPlayerPauseStateChanges = false;
    }
}
