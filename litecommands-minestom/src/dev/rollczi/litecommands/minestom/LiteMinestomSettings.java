package dev.rollczi.litecommands.minestom;

import dev.rollczi.litecommands.settings.PermissionResolver;
import dev.rollczi.litecommands.platform.PlatformSettings;
import net.minestom.server.command.CommandSender;

public class LiteMinestomSettings implements PlatformSettings {

    private PermissionResolver<CommandSender> permissionResolver = (sender, permission) -> true;

    public LiteMinestomSettings permissionResolver(PermissionResolver<CommandSender> permissionResolver) {
        this.permissionResolver = permissionResolver;
        return this;
    }

    boolean hasPermission(CommandSender handle, String permission) {
        return permissionResolver.hasPermission(handle, permission);
    }

}
