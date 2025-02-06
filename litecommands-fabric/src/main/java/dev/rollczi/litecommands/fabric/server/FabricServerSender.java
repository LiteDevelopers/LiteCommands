package dev.rollczi.litecommands.fabric.server;

import dev.rollczi.litecommands.identifier.Identifier;
import dev.rollczi.litecommands.platform.AbstractPlatformSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;

public class FabricServerSender extends AbstractPlatformSender {
    private final ServerCommandSource source;
    private final FabricServerPlatform platform;

    public FabricServerSender(ServerCommandSource source, FabricServerPlatform platform) {
        this.source = source;
        this.platform = platform;
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public Identifier getIdentifier() {
        List<Object> list = new ArrayList<>();
        list.add(source.getName());
        Entity entity = source.getEntity();
        if (entity != null) {
            list.add(entity.getUuid());
        }
        Object[] objects = list.toArray();
        return Identifier.of(objects);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.platform.getConfiguration().hasPermission(this.source, permission);
    }
}
