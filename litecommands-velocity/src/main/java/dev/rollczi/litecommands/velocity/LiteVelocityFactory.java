package dev.rollczi.litecommands.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.implementation.LiteFactory;
import net.kyori.adventure.text.Component;
import panda.std.Option;
import panda.std.Result;

import javax.smartcardio.CommandAPDU;

public final class LiteVelocityFactory {

    private LiteVelocityFactory() {
    }

    public static LiteCommandsBuilder<CommandSource> builder(ProxyServer proxy) {
        return LiteFactory.builder(CommandSource.class)
                .typeBind(ProxyServer.class, () -> proxy)
                .contextualBind(CommandSource.class, (commandSource, invocation) -> Result.ok(commandSource))

                .resultHandler(String.class, new StringHandler())
                .resultHandler(Component.class, new KyoriComponentHandler())

                .platform(new LiteVelocityRegistryPlatform(proxy));
    }

}
