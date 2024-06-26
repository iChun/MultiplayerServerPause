package me.ichun.mods.serverpause.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGamePacketListenerImpl.class)
public interface ServerGamePacketListenerImplAccessorMixin
{
    @Accessor
    boolean getClientIsFloating();

    @Accessor
    int getAboveGroundTickCount();

    @Accessor
    void setAboveGroundTickCount(int i);

    @Accessor
    boolean getClientVehicleIsFloating();

    @Accessor
    int getAboveGroundVehicleTickCount();

    @Accessor
    void setAboveGroundVehicleTickCount(int i);
}
