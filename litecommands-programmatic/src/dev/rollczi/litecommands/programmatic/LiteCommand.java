package dev.rollczi.litecommands.programmatic;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.SimpleArgument;
import dev.rollczi.litecommands.command.builder.CommandBuilder;
import dev.rollczi.litecommands.command.executor.CommandExecutor;
import dev.rollczi.litecommands.command.executor.LiteContext;
import dev.rollczi.litecommands.flag.FlagArgument;
import dev.rollczi.litecommands.join.JoinArgument;
import dev.rollczi.litecommands.meta.Meta;
import dev.rollczi.litecommands.meta.MetaKey;
import dev.rollczi.litecommands.quoted.QuotedStringArgumentResolver;
import dev.rollczi.litecommands.requirement.BindRequirement;
import dev.rollczi.litecommands.requirement.ContextRequirement;
import dev.rollczi.litecommands.scheduler.SchedulerPoll;
import dev.rollczi.litecommands.wrapper.WrapFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;

public class LiteCommand<SENDER> {

    protected final String name;
    protected final List<String> aliases;
    protected final Meta meta = Meta.create();

    protected Function<LiteContext<SENDER>, Object> executor = liteContext -> null;
    protected final List<Argument<?>> arguments = new ArrayList<>();
    protected final List<ContextRequirement<?>> contextRequirements = new ArrayList<>();
    protected final List<BindRequirement<?>> bindRequirements = new ArrayList<>();

    protected final List<LiteCommand<SENDER>> subCommands = new ArrayList<>();

    public LiteCommand(String name, List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public LiteCommand(String name) {
        this(name, Collections.emptyList());
    }

    public LiteCommand(String name, String... aliases) {
        this(name, Arrays.asList(aliases));
    }

    public LiteCommand<SENDER> argument(String name, Class<?> type) {
        this.arguments.add(new SimpleArgument<>(name, WrapFormat.notWrapped(type), false));
        return this;
    }

    public LiteCommand<SENDER> argument(Argument<?> argument) {
        this.arguments.add(argument);
        return this;
    }

    public LiteCommand<SENDER> argumentQuoted(String name) {
        Argument<String> argument = new SimpleArgument<>(name, WrapFormat.notWrapped(String.class));
        argument.meta().put(Meta.ARGUMENT_KEY, QuotedStringArgumentResolver.KEY);

        return this.argument(argument);
    }

    public LiteCommand<SENDER> argumentOptional(String name, Class<?> type) {
        this.arguments.add(new SimpleArgument<>(name, WrapFormat.of(type, Optional.class)));
        return this;
    }

    public LiteCommand<SENDER> argumentNullable(String name, Class<?> type) {
        this.arguments.add(new SimpleArgument<>(name, WrapFormat.of(type, Optional.class)));
        return this;
    }

    public LiteCommand<SENDER> argumentFlag(String name) {
        this.arguments.add(new FlagArgument(name, WrapFormat.notWrapped(boolean.class)));
        return this;
    }

    public LiteCommand<SENDER> argumentJoin(String name) {
        this.arguments.add(new JoinArgument<>(name, WrapFormat.notWrapped(String.class)));
        return this;
    }

    public LiteCommand<SENDER> argumentJoin(String name, String separator, int limit) {
        this.arguments.add(new JoinArgument<>(name, WrapFormat.notWrapped(String.class), separator, limit));
        return this;
    }

    public LiteCommand<SENDER> context(String name, Class<?> type) {
        this.contextRequirements.add(ContextRequirement.of(() -> name, type));
        return this;
    }

    public LiteCommand<SENDER> bind(String name, Class<?> type) {
        this.bindRequirements.add(BindRequirement.of(() -> name, type));
        return this;
    }

    public LiteCommand<SENDER> permissions(String... permissions) {
        this.meta.listEditor(Meta.PERMISSIONS).addAll(permissions).apply();
        return this;
    }

    public LiteCommand<SENDER> async() {
        return this.meta(Meta.POLL_TYPE, SchedulerPoll.ASYNCHRONOUS);
    }

    public <T> LiteCommand<SENDER> meta(MetaKey<T> key, T value) {
        this.meta.put(key, value);
        return this;
    }

    public final LiteCommand<SENDER> onExecute(Consumer<LiteContext<SENDER>> executor) {
        this.executor = liteContext -> {
            executor.accept(liteContext);
            return null;
        };
        return this;
    }

    @ApiStatus.Experimental
    public final LiteCommand<SENDER> onExecute(Function<LiteContext<SENDER>, Object> executor) {
        this.executor = executor;
        return this;
    }

    protected void execute(LiteContext<SENDER> context) {
        Object object = this.executor.apply(context);
        context.returnResult(object);
    }

    @SafeVarargs
    public final LiteCommand<SENDER> subCommands(LiteCommand<SENDER>... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
        return this;
    }

    CommandBuilder<SENDER> toRoute() {
        CommandBuilder<SENDER> builder = CommandBuilder.<SENDER>create()
            .routeName(name)
            .routeAliases(aliases)
            .applyMeta(meta -> meta.apply(this.meta))
            .applyMeta(meta -> meta.listEditor(Meta.COMMAND_ORIGIN_TYPE).add(this.getClass()).apply())
            .appendExecutor(root -> CommandExecutor.builder(root)
                .executor(liteContext -> execute(liteContext))
                .arguments(arguments)
                .contextRequirements(contextRequirements)
                .bindRequirements(bindRequirements)
                .build()
            );

        for (LiteCommand<SENDER> subCommand : subCommands) {
            builder.appendChild(subCommand.toRoute());
        }

        return builder;
    }

}
