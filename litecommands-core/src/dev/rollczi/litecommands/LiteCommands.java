package dev.rollczi.litecommands;

import dev.rollczi.litecommands.command.CommandManager;

public interface LiteCommands<SENDER> {

    String VERSION = "3.0.0-BETA-pre8";

    CommandManager<SENDER> getCommandManager();

    void register();

    void unregister();

}
