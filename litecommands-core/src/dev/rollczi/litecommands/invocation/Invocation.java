package dev.rollczi.litecommands.invocation;

import dev.rollczi.litecommands.argument.input.InputArguments;
import dev.rollczi.litecommands.command.input.Input;
import dev.rollczi.litecommands.platform.PlatformSender;

public class Invocation<SENDER> {

    private final SENDER handle;
    private final PlatformSender platformSender;
    private final String command;
    private final String label;
    private final Input<?> arguments;
    private final InvocationContext context;

    public Invocation(SENDER handle, PlatformSender platformSender, String command, String label, Input<?> arguments, InvocationContext context) {
        this.handle = handle;
        this.platformSender = platformSender;
        this.command = command;
        this.label = label;
        this.arguments = arguments;
        this.context = context;
    }

    public Invocation(SENDER handle, PlatformSender platformSender, String command, String label, Input<?> arguments) {
        this.handle = handle;
        this.platformSender = platformSender;
        this.command = command;
        this.label = label;
        this.arguments = arguments;
        this.context = InvocationContext.builder().build();
    }

    public SENDER sender() {
        return this.handle;
    }

    public PlatformSender platformSender() {
        return this.platformSender;
    }

    public String name() {
        return this.command;
    }

    public String label() {
        return this.label;
    }

    public Input<?> arguments() {
        return this.arguments;
    }

    public InvocationContext context() {
        return context;
    }

}
