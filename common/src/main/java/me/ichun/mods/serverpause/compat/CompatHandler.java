package me.ichun.mods.serverpause.compat;

import com.mojang.logging.LogUtils;
import me.ichun.mods.serverpause.common.ServerPause;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

public final class CompatHandler
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final HashMap<String, Class<?>> REGISTERED_COMPATS = Util.make(new HashMap<>(), m -> {
        m.put("sereneSeasons", CompatSereneSeasons.class);
        m.put("valkyrienSkies", CompatValkyrienSkies.class);
    });

    private static HashSet<Compat> compats;

    public static void tickServer(MinecraftServer server, boolean isPaused)
    {
        if(compats == null)
        {
            compats = new HashSet<>();
            checkCompats();
        }

        compats.removeIf(c -> !c.tickServer(server, isPaused));
    }

    private static void checkCompats()
    {
        REGISTERED_COMPATS.forEach((k, v) -> {
            if(!ServerPause.config.disabledCompatibilities.contains(k))
            {
                try
                {
                    Compat compat = (Compat)v.getDeclaredConstructor().newInstance();
                    if(compat.check())
                    {
                        compats.add(compat);
                    }
                }
                catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    LOGGER.error("Error creating compat class: {}", v.getName(), e);
                }
            }
        });
    }

    private static abstract class Compat
    {
        abstract boolean check();
        abstract boolean tickServer(MinecraftServer server, boolean isPaused); //returns true if handled properly, false if it failed
    }

    //Sorry Valkyrien Skies - Reflection is dirty, I know! Feel free to suggest a better alternative
    //Set the server's physics pipeline pause state as per - https://github.com/ValkyrienSkies/Valkyrien-Skies-2/blob/1.20/main/common/src/main/java/org/valkyrienskies/mod/mixin/client/MixinMinecraft.java#L115-L123
    private static class CompatValkyrienSkies extends Compat
    {
        Method getVsPipeline; //IShipObjectWorldServerProvider
        Method setArePhysicsRunning; //VSPipeline

        @Override
        boolean check()
        {
            try
            {
                Class<?> clzProvider = Class.forName("org.valkyrienskies.mod.common.IShipObjectWorldServerProvider"); //Minecraft Server extends this
                Class<?> clzPipeline = Class.forName("org.valkyrienskies.core.apigame.world.VSPipeline");
                LOGGER.info("Found ValkyrienSkies classes, looking for methods");

                getVsPipeline = clzProvider.getDeclaredMethod("getVsPipeline"); // Method returns VSPipeline
                getVsPipeline.setAccessible(true);

                setArePhysicsRunning = clzPipeline.getDeclaredMethod("setArePhysicsRunning", boolean.class);
                setArePhysicsRunning.setAccessible(true);

                LOGGER.info("Found ValkyrienSkies methods! All ok!");
                return true;
            }
            catch(ClassNotFoundException ignored){}
            catch(NoSuchMethodException e)
            {
                LOGGER.error("Error getting a method!", e);
            }

            return false;
        }

        @Override
        boolean tickServer(MinecraftServer server, boolean isPaused)
        {
            if(server.isDedicatedServer()) //single player is managed by the mod fine.
            {
                //get the pipeline
                try
                {
                    Object pipeline = getVsPipeline.invoke(server);
                    if(pipeline != null)
                    {
                        setArePhysicsRunning.invoke(pipeline, !isPaused);
                    }

                    return true;
                }
                catch(IllegalAccessException | InvocationTargetException e)
                {
                    LOGGER.error("Error ticking for ValkyrienSkies compatibility", e);
                    return false;
                }
            }
            return true;
        }
    }

    private static class CompatSereneSeasons extends Compat
    {
        Method getSeasonSavedData;
        Field seasonCycleTicks;
        Method markDirty;
        HashMap<ResourceKey<Level>, Integer> pausedSeasonTicks = new HashMap<>();

        @Override
        boolean check()
        {
            try
            {
                Class<?> clzModList = Class.forName("net.minecraftforge.fml.ModList");
                Method modListGet = clzModList.getDeclaredMethod("get");
                modListGet.setAccessible(true);

                Method modListIsLoaded = clzModList.getDeclaredMethod("isLoaded", String.class);
                modListIsLoaded.setAccessible(true);

                Object modList = modListGet.invoke(null);
                boolean isSereneSeasonsLoaded = (boolean)modListIsLoaded.invoke(modList, "sereneseasons");
                if(!isSereneSeasonsLoaded)
                {
                    return false;
                }

                Class<?> clzSeasonHandler = Class.forName("sereneseasons.season.SeasonHandler");
                Class<?> clzSeasonSavedData = Class.forName("sereneseasons.season.SeasonSavedData");
                LOGGER.info("Found Serene Seasons classes, looking for methods");

                getSeasonSavedData = clzSeasonHandler.getDeclaredMethod("getSeasonSavedData", Level.class);
                getSeasonSavedData.setAccessible(true);

                seasonCycleTicks = clzSeasonSavedData.getDeclaredField("seasonCycleTicks");
                seasonCycleTicks.setAccessible(true);

                markDirty = null;
                try
                {
                    markDirty = clzSeasonSavedData.getMethod("setDirty");
                }
                catch(NoSuchMethodException ignored)
                {
                    try
                    {
                        markDirty = clzSeasonSavedData.getMethod("markDirty");
                    }
                    catch(NoSuchMethodException ignored1){}
                }
                if(markDirty != null)
                {
                    markDirty.setAccessible(true);
                }
                else
                {
                    LOGGER.warn("Could not resolve Serene Seasons dirty method, continuing without save dirty mark");
                }

                LOGGER.info("Found Serene Seasons methods! All ok!");
                return true;
            }
            catch(ClassNotFoundException ignored){}
            catch(NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e)
            {
                LOGGER.error("Error getting Serene Seasons methods!", e);
            }

            return false;
        }

        @Override
        boolean tickServer(MinecraftServer server, boolean isPaused)
        {
            if(!isPaused)
            {
                pausedSeasonTicks.clear();
                return true;
            }

            try
            {
                for(ServerLevel level : server.getAllLevels())
                {
                    ResourceKey<Level> dimension = level.dimension();
                    Object seasonSavedData = getSeasonSavedData.invoke(null, level);
                    if(seasonSavedData == null)
                    {
                        pausedSeasonTicks.remove(dimension);
                        continue;
                    }

                    int currentSeasonTicks = seasonCycleTicks.getInt(seasonSavedData);
                    Integer frozenSeasonTicks = pausedSeasonTicks.get(dimension);

                    if(frozenSeasonTicks == null)
                    {
                        pausedSeasonTicks.put(dimension, currentSeasonTicks);
                        continue;
                    }

                    if(currentSeasonTicks != frozenSeasonTicks)
                    {
                        seasonCycleTicks.setInt(seasonSavedData, frozenSeasonTicks);
                        if(markDirty != null)
                        {
                            markDirty.invoke(seasonSavedData);
                        }
                    }
                }

                return true;
            }
            catch(IllegalAccessException | InvocationTargetException e)
            {
                LOGGER.error("Error ticking for Serene Seasons compatibility", e);
                return false;
            }
        }
    }
}
