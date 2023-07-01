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
    }

    @Transitive
    @ConfigEntries(includeAll = true)
    public static class General implements ConfigGroup
    {
        public General()
        {
            INSTANCE_GENERAL = this;
        }

        @ConfigEntry(nameKey = "general.pauseWhenNoPlayers.name", descriptionKey = "general.pauseWhenNoPlayers", comment = "Set to true to pause the server when all connected players are paused.")
        public boolean pauseWhenAllPlayersPaused = true;
        @ConfigEntry(nameKey = "general.pauseWhenNoPlayers.name", descriptionKey = "general.pauseWhenNoPlayers", comment = "Set to true to pause the server when no players are connected.")
        public boolean pauseWhenNoPlayers = false;
        @ConfigEntry(nameKey = "general.sendChatMessageWhenPauseStateChanges.name", descriptionKey = "general.sendChatMessageWhenPauseStateChanges", comment = "Set to true to send a chat message to all players (and to the server log) when the server pause state changes.")
        public boolean sendChatMessageWhenPauseStateChanges = false;
    }
}
