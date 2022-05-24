package me.ichun.mods.serverpause.common.core;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Config
{
    public ConfigWrapper<Boolean> pauseWhenAllPlayersPaused;
    public ConfigWrapper<Boolean> pauseWhenNoPlayers;
    public ConfigWrapper<Boolean> sendChatMessageWhenPauseStateChanges;

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
