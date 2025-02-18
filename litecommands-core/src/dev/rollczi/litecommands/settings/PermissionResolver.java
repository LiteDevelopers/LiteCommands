package dev.rollczi.litecommands.settings;

@FunctionalInterface
public interface PermissionResolver<SENDER> {

    boolean hasPermission(SENDER sender, String permission);

}
