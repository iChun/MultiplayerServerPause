package me.ichun.mods.serverpause.mixin;

import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessorMixin
{
    @Accessor
    List<Runnable> getTickables();

    @Accessor
    long getLastServerStatus();

    @Accessor
    void setLastServerStatus(long l);

    @Accessor
    ServerStatus getStatus();

    @Accessor
    RandomSource getRandom();
}
