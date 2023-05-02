package dev.rollczi.litecommands.command.requirements;

import dev.rollczi.litecommands.argument.Argument;

public interface CommandArgumentRequirement<SENDER, OUT> extends CommandRequirement<SENDER, OUT> {

    Argument<?> getArgument();

    boolean isOptional();

}
