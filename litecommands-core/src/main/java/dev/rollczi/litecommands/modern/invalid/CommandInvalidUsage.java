package dev.rollczi.litecommands.modern.invalid;

public class CommandInvalidUsage {

    private Cause cause;
    //private final Schematic

    public enum Cause {

        UNKNOWN_COMMAND,
        MISSING_ARGUMENT,
        INVALID_ARGUMENT,

    }

}
