package me.ichun.mods.serverpause.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessorMixin
{
    @Accessor
    List<Runnable> getTickables();
}
