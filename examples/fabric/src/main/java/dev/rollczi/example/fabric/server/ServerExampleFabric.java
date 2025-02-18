package dev.rollczi.example.fabric.server;

import dev.rollczi.example.fabric.server.command.ExampleCommand;
import dev.rollczi.litecommands.fabric.LiteFabricFactory;
import dev.rollczi.litecommands.luckperms.LuckPermsResolver;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

public class ServerExampleFabric implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        LiteFabricFactory.server()
            .settings(settings ->
                settings.permissionResolver(
                    new LuckPermsResolver<>(source -> {
                        if (source instanceof ServerCommandSource && ((ServerCommandSource) source).isExecutedByPlayer()) {
                            return ((ServerCommandSource) source).getPlayer().getUuid();
                        } else if (source instanceof ServerCommandSource && ((ServerCommandSource) source).output instanceof MinecraftServer) {
                            return LuckPermsResolver.CONSOLE;
                        }
                        return null;
                    })
                )
            )
            .commands(new ExampleCommand())
            .build();
    }
}
