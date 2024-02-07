package dev.rollczi.litecommands.sponge;

import dev.rollczi.litecommands.argument.parser.input.ParseableInput;
import dev.rollczi.litecommands.argument.suggester.input.SuggestionInput;
import dev.rollczi.litecommands.command.CommandRoute;
import dev.rollczi.litecommands.input.raw.RawCommand;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.meta.Meta;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.platform.PlatformInvocationListener;
import dev.rollczi.litecommands.platform.PlatformSuggestionListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.ArgumentReader;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LiteSpongeCommand implements Command.Raw {

    private final CommandRoute<CommandCause> commandRoute;
    private final PlatformInvocationListener<CommandCause> executeHook;
    private final PlatformSuggestionListener<CommandCause> suggestionHook;

    public LiteSpongeCommand(CommandRoute<CommandCause> commandRoute, PlatformInvocationListener<CommandCause> executeHook, PlatformSuggestionListener<CommandCause> suggestionHook) {
        this.commandRoute = commandRoute;
        this.executeHook = executeHook;
        this.suggestionHook = suggestionHook;
    }

    public CommandRoute<CommandCause> getCommandRoute() {
        return commandRoute;
    }

    @Override
    public CommandResult process(CommandCause cause, ArgumentReader.Mutable arguments) {
        ParseableInput<?> input = rawCommand(arguments).toParseableInput();
        Invocation<CommandCause> invocation = new Invocation<>(cause, new LiteSpongeSender(cause), commandRoute.getName(), commandRoute.getName(), input);
        this.executeHook.execute(invocation, input);
        return CommandResult.success();
    }

    @Override
    public List<CommandCompletion> complete(CommandCause cause, ArgumentReader.Mutable arguments) {
        SuggestionInput<?> input = rawCommand(arguments).toSuggestionInput();
        Invocation<CommandCause> invocation = new Invocation<>(cause, new LiteSpongeSender(cause), commandRoute.getName(), commandRoute.getName(), input);

        return this.suggestionHook.suggest(invocation, input)
            .getSuggestions()
            .stream()
            .map(suggestion -> CommandCompletion.of(suggestion.multilevel()))
            .collect(Collectors.toList());
    }

    private RawCommand rawCommand(ArgumentReader.Mutable arguments) {
        return RawCommand.from(RawCommand.COMMAND_SLASH + commandRoute.getName() + RawCommand.COMMAND_SEPARATOR + arguments.input());
    }

    @Override
    public boolean canExecute(CommandCause sender) {
        MissingPermissions missingPermissions = MissingPermissions.check(new LiteSpongeSender(sender), this.commandRoute);
        return missingPermissions.isPermitted();
    }

    @Override
    public Optional<Component> shortDescription(CommandCause cause) {
        List<Component> description = commandRoute.meta().get(Meta.DESCRIPTION).stream()
            .map(string -> Component.text(string))
            .collect(Collectors.toList());

        return Optional.of(Component.join(JoinConfiguration.newlines(), description));
    }

    @Override
    public Optional<Component> extendedDescription(CommandCause cause) {
        return shortDescription(cause);
    }

    @Override
    public Component usage(CommandCause cause) {
        return shortDescription(cause).orElse(Component.empty());
    }
}
