package dev.rollczi.litecommands.fabric.server;

import dev.rollczi.litecommands.command.CommandRoute;
import dev.rollczi.litecommands.fabric.FabricAbstractCommand;
import dev.rollczi.litecommands.fabric.FabricAbstractPlatform;
import dev.rollczi.litecommands.platform.PlatformInvocationListener;
import dev.rollczi.litecommands.platform.PlatformSettings;
import dev.rollczi.litecommands.platform.PlatformSuggestionListener;
import net.minecraft.server.command.ServerCommandSource;

public class FabricServerPlatform extends FabricAbstractPlatform<ServerCommandSource> {

    public FabricServerPlatform(PlatformSettings settings) {
        super(settings, source -> new FabricServerSender(source));
    }

    @Override
    protected void registerEvents() {
        if (COMMAND_API_V2) {
            net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                registerToDispatcher(dispatcher);
            });
        } else {
            net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                registerToDispatcher(dispatcher);
            });
        }
    }

    @Override
    protected FabricAbstractCommand<ServerCommandSource> createCommand(CommandRoute<ServerCommandSource> commandRoute, PlatformInvocationListener<ServerCommandSource> invocationHook, PlatformSuggestionListener<ServerCommandSource> suggestionHook) {
        return new FabricServerCommand(this, commandRoute, invocationHook, suggestionHook);
    }
}
