package me.ichun.mods.serverpause.mixin;

import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

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
    void setStatus(ServerStatus status);

    @Invoker
    ServerStatus invokeBuildServerStatus();
}
