package dev.rollczi.litecommands.fabric.client;

import dev.rollczi.litecommands.identifier.Identifier;
import dev.rollczi.litecommands.platform.AbstractPlatformSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@Environment(EnvType.CLIENT)
public class FabricClientSender extends AbstractPlatformSender {
    private final FabricClientCommandSource source;
    private final FabricClientPlatform platform;

    public FabricClientSender(FabricClientCommandSource source, FabricClientPlatform platform) {
        this.source = source;
        this.platform = platform;
    }

    @Override
    public String getName() {
        return source.getPlayer().getGameProfile().getName();
    }

    @Override
    public Identifier getIdentifier() {
        return Identifier.of(source.getEntity().getUuid());
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.platform.getConfiguration().hasPermission(this.source, permission);
    }
}
