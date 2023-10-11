package dev.rollczi.litecommands.unit;

import dev.rollczi.litecommands.command.CommandRoute;
import dev.rollczi.litecommands.argument.parser.input.ParseableInput;
import dev.rollczi.litecommands.input.raw.RawCommand;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.platform.AbstractPlatform;
import dev.rollczi.litecommands.platform.PlatformInvocationListener;
import dev.rollczi.litecommands.platform.PlatformSender;
import dev.rollczi.litecommands.platform.PlatformSuggestionListener;
import dev.rollczi.litecommands.argument.suggester.input.SuggestionInput;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestPlatform extends AbstractPlatform<TestSender, TestSettings> {

    private final Map<CommandRoute<TestSender>, PlatformInvocationListener<TestSender>> executeListeners = new LinkedHashMap<>();
    private final Map<CommandRoute<TestSender>, PlatformSuggestionListener<TestSender>> suggestListeners = new LinkedHashMap<>();

    public TestPlatform() {
        super(new TestSettings());
    }

    @Override
    protected void hook(CommandRoute<TestSender> commandRoute, PlatformInvocationListener<TestSender> invocationHook, PlatformSuggestionListener<TestSender> suggestionHook) {
        this.executeListeners.put(commandRoute, invocationHook);
        this.suggestListeners.put(commandRoute, suggestionHook);
    }

    @Override
    protected void unhook(CommandRoute<TestSender> commandRoute) {
        this.executeListeners.remove(commandRoute);
        this.suggestListeners.remove(commandRoute);
    }

    public AssertExecute execute(String command) {
        return this.execute(new TestPlatformSender(), command);
    }

    public AssertExecute execute(PlatformSender sender, String command) {
        try {
            return this.executeAsync(sender, command).get(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<AssertExecute> executeAsync(String command) {
        return this.executeAsync(new TestPlatformSender(), command);
    }

    public CompletableFuture<AssertExecute> executeAsync(PlatformSender sender, String command) {
        TestSender testSender = new TestSender();

        RawCommand rawCommand = RawCommand.from(command);
        String label = rawCommand.getLabel();
        ParseableInput<?> input = rawCommand.toParseableInput();

        Invocation<TestSender> invocation = new Invocation<>(testSender, sender, label, label, input);
        CommandRoute<TestSender> route = this.commandRoutes.get(label);

        if (route == null) {
            throw new IllegalStateException("No command found for " + command);
        }

        PlatformInvocationListener<TestSender> listener = this.executeListeners.get(route);

        if (listener == null) {
            throw new IllegalStateException("No command listener found for " + command);
        }

        return listener.execute(invocation, input)
            .thenApply(result -> new AssertExecute(result, invocation));
    }

    public AssertSuggest suggest(String command) {
        return this.suggest(new TestPlatformSender(), command);
    }

    public AssertSuggest suggest(PlatformSender platformSender, String command) {
        TestSender testSender = new TestSender();

        RawCommand rawCommand = RawCommand.from(command);
        String label = rawCommand.getLabel();
        SuggestionInput<?> arguments = rawCommand.toSuggestionInput();

        Invocation<TestSender> invocation = new Invocation<>(testSender, platformSender, label, label, arguments);
        CommandRoute<TestSender> route = this.commandRoutes.get(label);

        if (route == null) {
            throw new IllegalStateException("No command found for " + command);
        }

        PlatformSuggestionListener<TestSender> listener = this.suggestListeners.get(route);

        if (listener == null) {
            throw new IllegalStateException("No command listener found for " + command);
        }

        return new AssertSuggest(listener.suggest(invocation, arguments));
    }

    public CommandRoute<TestSender> findCommand(String command) {
        RawCommand rawCommand = RawCommand.from(command);
        String label = rawCommand.getLabel();
        CommandRoute<TestSender> route = this.commandRoutes.get(label);

        if (route == null) {
            throw new IllegalStateException("No command found for " + command);
        }

        return route;
    }


}
