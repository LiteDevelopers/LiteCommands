package dev.rollczi.litecommands.fabric;

import dev.rollczi.litecommands.platform.PlatformSettings;
import dev.rollczi.litecommands.settings.PermissionResolver;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.ApiStatus;

public class LiteFabricSettings implements PlatformSettings {

    private String inputInspectionDisplay = "[...]";
    private PermissionResolver<CommandSource> permissionResolver = (sender, permission) -> true;

    String getInputInspectionDisplay() {
        return this.inputInspectionDisplay;
    }

    /**
     * The name of the brigadier argument used to inspection, default is "[...]".
     * Inspection sometimes displays while the player is typing the command.
     * LiteCommands don't support brigadier suggestions, but we must provide a name for the default input argument.
     */
    @ApiStatus.Experimental
    public LiteFabricSettings inputInspectionDisplay(String name) {
        this.inputInspectionDisplay = name;
        return this;
    }

    public boolean hasPermission(CommandSource source, String permission) {
        return this.permissionResolver.hasPermission(source, permission);
    }

    public LiteFabricSettings permissionResolver(PermissionResolver<CommandSource> permissionResolver) {
        this.permissionResolver = permissionResolver;
        return this;
    }
}
