package dev.rollczi.litecommands.fabric.client;

import dev.rollczi.litecommands.fabric.FabricAbstractPlatform;
import dev.rollczi.litecommands.fabric.LiteFabricSettings;
import dev.rollczi.litecommands.platform.PlatformSender;
import dev.rollczi.litecommands.platform.PlatformSenderFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@Environment(EnvType.CLIENT)
public class FabricClientPlatform extends FabricAbstractPlatform<FabricClientCommandSource> {

    public FabricClientPlatform(LiteFabricSettings settings) {
        super(settings);
    }

    @Override
    protected void registerEvents() {
        if (!COMMAND_API_V2) {
            throw new UnsupportedOperationException("The current 'fabric-api' does not include fabric-command-api-v2. Please update 'fabric-api'");
        }
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess) -> {
            this.registerAllCommands(dispatcher);
        });
    }

    @Override
    public PlatformSenderFactory<FabricClientCommandSource> getSenderFactory() {
        return this::createSender;
    }

    @Override
    public PlatformSender createSender(FabricClientCommandSource nativeSender) {
        return new FabricClientSender(nativeSender, this);
    }
}
