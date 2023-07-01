package me.ichun.mods.serverpause.common.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.ichun.mods.serverpause.common.ServerPause;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class PauseCommand
{
    private static final SimpleCommandExceptionType ALREADY_PAUSED = new SimpleCommandExceptionType(Component.translatable("commands.pause.alreadyPaused"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pause").requires(p -> p.hasPermission(4))
                .executes(context -> {
                    if(ServerPause.eventHandlerServer.isPaused && !ServerPause.eventHandlerServer.forcePause)
                    {
                        throw ALREADY_PAUSED.create();
                    }
                    context.getSource().sendSuccess(() -> Component.translatable(ServerPause.eventHandlerServer.forcePause ? "commands.pause.unpausing" : "commands.pause.pausing"), true);
                    ServerPause.eventHandlerServer.toggleForcePause();
                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}
