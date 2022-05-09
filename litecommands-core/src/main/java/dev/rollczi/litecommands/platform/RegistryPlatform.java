package dev.rollczi.litecommands.platform;

import dev.rollczi.litecommands.command.section.CommandSection;

public interface RegistryPlatform<SENDER> {

    void registerListener(CommandSection command, ExecuteListener<SENDER> executeListener, Completer<SENDER> completer);

    void unregisterListener(CommandSection command);

    void unregisterAll();

}
