package me.ichun.mods.serverpause.common.core;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Config
{
    public ConfigWrapper<Boolean> pauseWhenAllPlayersPaused;
    public ConfigWrapper<Boolean> pauseWhenNoPlayers;
    public ConfigWrapper<Boolean> sendChatMessageWhenPauseStateChanges;
    public ConfigWrapper<Boolean> sendChatMessageWhenPlayerPauseStateChanges;

    protected static class Reference
    {
        public static final String PAUSE_WHEN_ALL_PLAYERS_PAUSED_COMMENT = "Set to true to pause the server when all connected players are paused.";
        public static final String PAUSE_WHEN_NO_PLAYERS_COMMENT = "Set to true to pause the server when no players are connected.";
        public static final String SEND_CHAT_MESSAGE_WHEN_PAUSE_STATE_CHANGES_COMMENT = "Set to true to send a chat message to all players (and to the server log) when the server pause state changes.";
        public static final String SEND_CHAT_MESSAGE_WHEN_PLAYER_PAUSE_STATE_CHANGES_COMMENT = "Set to true to send a chat message to all players (and to the server log) when the a player's pause state changes.";
    }

    public static class ConfigWrapper<T>
    {
        public final Supplier<T> getter;
        public final Consumer<T> setter;
        public final Runnable saver;

        public ConfigWrapper(Supplier<T> getter, Consumer<T> setter) {
            this.getter = getter;
            this.setter = setter;
            this.saver = null;
        }

        public ConfigWrapper(Supplier<T> getter, Consumer<T> setter, Runnable saver) {
            this.getter = getter;
            this.setter = setter;
            this.saver = saver;
        }

        public T get()
        {
            return getter.get();
        }

        public void set(T obj)
        {
            setter.accept(obj);
        }

        public void save()
        {
            if(saver != null)
            {
                saver.run();
            }
        }
    }
}
