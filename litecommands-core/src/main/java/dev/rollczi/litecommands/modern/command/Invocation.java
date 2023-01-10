package dev.rollczi.litecommands.modern.command;

import dev.rollczi.litecommands.modern.platform.PlatformSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Invocation<SENDER> {

    private final SENDER handle;
    private final PlatformSender platformSender;
    private final String command;
    private final String label;
    private final List<String> rawArguments;

    public Invocation(SENDER handle, PlatformSender platformSender, String command, String label, String[] rawArguments) {
        this.handle = handle;
        this.platformSender = platformSender;
        this.command = command;
        this.label = label;
        this.rawArguments = Arrays.asList(rawArguments);
    }

    public SENDER handle() {
        return handle;
    }

    public PlatformSender platformSender() {
        return platformSender;
    }

    public String name() {
        return command;
    }

    public String label() {
        return label;
    }

    public String[] arguments() {
        return rawArguments.toArray(new String[0]);
    }

    public List<String> argumentsList() {
        return Collections.unmodifiableList(rawArguments);
    }

    public Optional<String> argument(int route) {
        return route < rawArguments.size() && route >= 0 ? Optional.of(rawArguments.get(route)) : Optional.empty();
    }

    public Optional<String> firstArgument() {
        return this.argument(0);
    }

    public Optional<String> lastArgument() {
        return this.argument(this.arguments().length - 1);
    }

}
