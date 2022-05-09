package dev.rollczi.litecommands.implementation;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.option.Opt;
import dev.rollczi.litecommands.argument.option.OptionArgument;
import dev.rollczi.litecommands.argument.option.OptionalArgumentSupplier;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.contextual.Contextual;
import dev.rollczi.litecommands.factory.CommandEditor;
import dev.rollczi.litecommands.factory.CommandStateFactory;
import dev.rollczi.litecommands.command.section.CommandSection;
import dev.rollczi.litecommands.command.CommandService;
import dev.rollczi.litecommands.handle.ExecuteResultHandler;
import dev.rollczi.litecommands.handle.Handler;
import dev.rollczi.litecommands.platform.RegistryPlatform;
import org.panda_lang.utilities.inject.Injector;
import panda.std.Option;
import panda.std.Result;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class LiteCommandsBuilderImpl<SENDER> implements LiteCommandsBuilder<SENDER> {

    private RegistryPlatform<SENDER> registryPlatform;
    private final ExecuteResultHandler<SENDER> executeResultHandler = new ExecuteResultHandler<>();

    private Injector injector = InjectorProvider.createInjector();
    private CommandStateFactory commandStateFactory;
    private final Set<Consumer<CommandStateFactory>> commandStateFactoryEditors = new HashSet<>();
    private final Map<Class<?>, CommandEditor> editors = new HashMap<>();

    private final Map<Class<?>, Supplier<?>> typeBinds = new HashMap<>();
    private final Map<Class<?>, Contextual<SENDER, ?>> contextualBinds = new HashMap<>();

    private final Set<Class<?>> commandsClasses = new HashSet<>();
    private final Set<Object> commandsInstances = new HashSet<>();
    private final Set<ArgumentStack<?>> arguments = new HashSet<>();

    private LiteCommandsBuilderImpl() {
    }

    @Override
    public LiteCommandsBuilderImpl<SENDER> platform(RegistryPlatform<SENDER> registryPlatform) {
        this.registryPlatform = registryPlatform;
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER> commandFactory(CommandStateFactory factory) {
        this.commandStateFactory = factory;
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER> configureFactory(Consumer<CommandStateFactory> consumer) {
        commandStateFactoryEditors.add(consumer);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER> commandEditor(Class<?> commandClass, CommandEditor commandEditor) {
        this.editors.put(commandClass, commandEditor);
        return this;
    }

    @Override
    public <T> LiteCommandsBuilderImpl<SENDER> resultHandler(Class<T> on, Handler<SENDER, T> handler) {
        this.executeResultHandler.register(on, handler);
        return this;
    }

    @Override
    public LiteCommandsBuilderImpl<SENDER> executorFactory(CommandStateFactory commandStateFactory) {
        this.commandStateFactory = commandStateFactory;
        return this;
    }

    @Override
    public LiteCommandsBuilderImpl<SENDER> command(Class<?>... commandClass) {
        commandsClasses.addAll(Arrays.asList(commandClass));
        return this;
    }

    @Override
    public LiteCommandsBuilderImpl<SENDER> commandInstance(Object... commandInstance) {
        commandsInstances.add(Arrays.asList(commandInstance));
        return this;
    }

    @Override
    public LiteCommandsBuilderImpl<SENDER> typeBind(Class<?> type, Supplier<?> supplier) {
        typeBinds.put(type, supplier);
        return this;
    }

    @Override
    public <T> LiteCommandsBuilderImpl<SENDER> contextualBind(Class<T> on, Contextual<SENDER, T> contextual) {
        this.contextualBinds.put(on, contextual);
        return this;
    }

    @Override
    public LiteCommandsBuilderImpl<SENDER> argument(Class<?> on, Argument<Arg> argument) {
        return this.argument(Arg.class, on, argument);
    }

    @Override
    public <T> LiteCommandsBuilderImpl<SENDER> optionalArgument(Class<T> on, OptionalArgumentSupplier<T> supplier) {
        return this.argument(Opt.class, Option.class, new OptionArgument<>(on, supplier));
    }

    @Override
    public <A extends Annotation> LiteCommandsBuilderImpl<SENDER> argument(Class<A> annotation, Class<?> on, Argument<A> argument) {
        for (ArgumentStack<?> stack : arguments) {
            if (stack.isKey(annotation)) {
                stack.add(on, argument);
                return this;
            }
        }

        ArgumentStack<A> stack = new ArgumentStack<>(annotation);

        stack.add(on, argument);
        this.arguments.add(stack);
        return this;
    }

    @Override
    public LiteCommands<SENDER> register() {
        if (registryPlatform == null) {
            throw new IllegalStateException("Registry platform is not set");
        }

        LiteCommands<SENDER> commands = new LiteCommandsImpl<>(registryPlatform, executeResultHandler);

        this.injector = this.injector.fork(resources -> {
            for (Map.Entry<Class<?>, Supplier<?>> entry : typeBinds.entrySet()) {
                resources.on(entry.getKey()).assignInstance(entry.getValue());
            }

            for (Map.Entry<Class<?>, Contextual<SENDER, ?>> entry : contextualBinds.entrySet()) {
                resources.on(entry.getKey()).assignHandler((property, annotation, args) -> { //TODO: add safe injection and cast ;-;
                    Contextual<SENDER, ?> contextual = entry.getValue();
                    InvokeContext invokeContext = InvokeContext.fromArgs(args);
                    LiteInvocation invocation = invokeContext.getInvocation();
                    Result<?, Object> result = contextual.extractFromContext((SENDER) invocation.sender().getHandle(), invocation);

                    return result.orNull();
                });
            }
        });

        if (this.commandStateFactory == null) {
            this.commandStateFactory = LiteCommandFactory.create(this.injector);
        }

        for (Consumer<CommandStateFactory> editor : commandStateFactoryEditors) {
            editor.accept(this.commandStateFactory);
        }

        for (ArgumentStack<?> argument : this.arguments) {
            this.registerArgumentStack(argument);
        }

        try {
            for (Class<?> commandsClass : this.commandsClasses) {
                Object command = this.injector.newInstance(commandsClass);

                this.commandsInstances.add(command);
            }
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        CommandService<SENDER> service = commands.getCommandService();

        for (Object instance : commandsInstances) {
            Option<CommandSection> option = this.commandStateFactory.create(instance);

            if (option.isEmpty()) {
                continue;
            }

            service.register(option.get());
        }

        return commands;
    }

    private <A extends Annotation> void registerArgumentStack(ArgumentStack<A> stack) {
        stack.map.forEach((on, argument) -> this.commandStateFactory.argument(stack.key, on, argument));
    }

    public static <T> LiteCommandsBuilder<T> builder() {
        return new LiteCommandsBuilderImpl<>();
    }

    private static final class ArgumentStack<A extends Annotation> {
        private final Class<A> key;
        private final Map<Class<?>, Argument<A>> map = new HashMap<>();

        private ArgumentStack(Class<A> key) {
            this.key = key;
        }

        @SuppressWarnings("unchecked")
        private void add(Class<?> on, Argument<?> argument) {
            map.put(on, (Argument<A>) argument);
        }

        private boolean isKey(Class<? extends Annotation> key) {
            return this.key.equals(key);
        }

    }

}
