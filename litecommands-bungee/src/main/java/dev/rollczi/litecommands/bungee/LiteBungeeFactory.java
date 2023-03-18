package dev.rollczi.litecommands.bungee;

import dev.rollczi.litecommands.bungee.tools.BungeeOnlyPlayerContextual;
import dev.rollczi.litecommands.modern.LiteCommandsBuilder;
import dev.rollczi.litecommands.modern.LiteCommandsFactory;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public final class LiteBungeeFactory {

    private LiteBungeeFactory() {
    }

    public static LiteCommandsBuilder<CommandSender, LiteBungeeSettings, ?> builder(Plugin plugin) {
        return builder(plugin, new LiteBungeeSettings());
    }

    public static LiteCommandsBuilder<CommandSender, LiteBungeeSettings, ?> builder(Plugin plugin, LiteBungeeSettings liteBungeeSettings) {
        return LiteCommandsFactory.builder(CommandSender.class, liteBungeeSettings)
            .platform(new BungeePlatform(plugin, liteBungeeSettings))

            .typeBind(ProxyServer.class, plugin::getProxy)
            .contextualBind(ProxiedPlayer.class, new BungeeOnlyPlayerContextual<>("Only players can use this command! (Set this message in LiteBungeeFactory)"))

            .resultHandler(BaseComponent.class, new BaseComponentHandler())
            .resultHandler(String.class, new StringHandler());
    }

}
