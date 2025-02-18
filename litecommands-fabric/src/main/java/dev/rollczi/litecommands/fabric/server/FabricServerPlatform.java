package dev.rollczi.litecommands.fabric.server;

import dev.rollczi.litecommands.fabric.FabricAbstractPlatform;
import dev.rollczi.litecommands.fabric.LiteFabricSettings;
import dev.rollczi.litecommands.platform.PlatformSender;
import dev.rollczi.litecommands.platform.PlatformSenderFactory;
import net.minecraft.server.command.ServerCommandSource;

public class FabricServerPlatform extends FabricAbstractPlatform<ServerCommandSource> {

    public FabricServerPlatform(LiteFabricSettings settings) {
        super(settings);
    }

    @Override
    protected void registerEvents() {
        if (COMMAND_API_V2) {
            net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                this.registerAllCommands(dispatcher);
            });
        } else {
            net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                this.registerAllCommands(dispatcher);
            });
        }
    }

    @Override
    public PlatformSenderFactory<ServerCommandSource> getSenderFactory() {
        return this::createSender;
    }

    @Override
    public PlatformSender createSender(ServerCommandSource nativeSender) {
        return new FabricServerSender(nativeSender, this);
    }
}
