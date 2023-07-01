package me.ichun.mods.serverpause.loader.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.ichun.mods.serverpause.common.ServerPause;
import me.ichun.mods.serverpause.loader.fabric.ConfigFabric;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> {
            if(FabricLoader.getInstance().isModLoaded("cloth-config"))
            {
                return (new ClothConfigScreenBuilder()).build(parent, ((ConfigFabric)ServerPause.config).configInstance);
            }
            //  YACL support not in completeconfig currently. modid: yet_another_config_lib_v3
            return null;
        };
    }
}
