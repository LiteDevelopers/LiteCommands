package dev.rollczi.litecommands.bungee;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.LiteFactory;
import dev.rollczi.litecommands.bind.basic.OriginalSenderBind;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public final class LiteBungeeFactory {

    private LiteBungeeFactory() {

    }

    public static LiteCommandsBuilder<CommandSender, LiteBungeePlatformManager> builder(Plugin plugin) {
        return LiteFactory.<CommandSender, LiteBungeePlatformManager>builder()
                .bind(Plugin.class, plugin)
                .bind(ProxyServer.class, plugin.getProxy())
                .bind(CommandSender.class, new OriginalSenderBind())
                .platform(new LiteBungeePlatformManager(plugin));
    }

}
