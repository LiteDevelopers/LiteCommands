package dev.rollczi.litecommands.implementation;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.simple.MultilevelArgument;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.argument.simple.SimpleMultilevelArgument;
import dev.rollczi.litecommands.argument.option.Opt;
import dev.rollczi.litecommands.argument.option.OptionArgument;
import dev.rollczi.litecommands.command.permission.LitePermissions;
import dev.rollczi.litecommands.contextual.Contextual;
import dev.rollczi.litecommands.factory.CommandEditor;
import dev.rollczi.litecommands.factory.CommandEditorRegistry;
import dev.rollczi.litecommands.factory.CommandStateFactory;
import dev.rollczi.litecommands.command.section.CommandSection;
import dev.rollczi.litecommands.command.CommandService;
import dev.rollczi.litecommands.handle.ExecuteResultHandler;
import dev.rollczi.litecommands.handle.Handler;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.handle.PermissionHandler;
import dev.rollczi.litecommands.platform.RegistryPlatform;
import dev.rollczi.litecommands.scheme.Scheme;
import dev.rollczi.litecommands.scheme.SchemeFormat;
import org.panda_lang.utilities.inject.Injector;
import panda.std.Option;

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
    private final CommandEditorRegistry editorRegistry = new CommandEditorRegistry();

    private final Map<Class<?>, Supplier<?>> typeBinds = new HashMap<>();
    private final Map<Class<?>, Contextual<SENDER, ?>> contextualBinds = new HashMap<>();

    private final Set<Class<?>> commandsClasses = new HashSet<>();
    private final Set<Object> commandsInstances = new HashSet<>();
    private final ArgumentsRegistry argumentsRegistry = new ArgumentsRegistry();

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
        this.editorRegistry.registerEditor(commandClass, commandEditor);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER> commandEditor(String name, CommandEditor commandEditor) {
        this.editorRegistry.registerEditor(name, commandEditor);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER> schemeFormat(SchemeFormat schemeFormat) {
        this.executeResultHandler.schemeFormat(schemeFormat);
        return this;
    }

    @Override
    public <T> LiteCommandsBuilderImpl<SENDER> resultHandler(Class<T> on, Handler<SENDER, T> handler) {
        this.executeResultHandler.register(on, handler);
        return this;
    }

    public LiteCommandsBuilder<SENDER> invalidUsageHandler(InvalidUsageHandler<SENDER> handler) {
        return this.resultHandler(Scheme.class, handler);
    }

    @Override
    public LiteCommandsBuilder<SENDER> permissionHandler(PermissionHandler<SENDER> handler) {
        return this.resultHandler(LitePermissions.class, handler);
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
    public <T> LiteCommandsBuilder<SENDER> argument(Class<T> on, OneArgument<T> argument) {
        return this.argumentMultilevel(on, argument);
    }

    @Override
    public <T> LiteCommandsBuilder<SENDER> argument(Class<T> on, String by, OneArgument<T> argument) {
        return this.argumentMultilevel(on, by, argument);
    }

    @Override
    public <T> LiteCommandsBuilderImpl<SENDER> argumentMultilevel(Class<T> on, MultilevelArgument<T> argument) {
        this.argument(Arg.class, on, new SimpleMultilevelArgument<>(argument));
        this.argument(Opt.class, on, new OptionArgument<>(on, argument));
        return this;
    }

    @Override
    public <T> LiteCommandsBuilder<SENDER> argumentMultilevel(Class<T> on, String by, MultilevelArgument<T> argument) {
        this.argument(Arg.class, on, by, new SimpleMultilevelArgument<>(argument));
        this.argument(Opt.class, on, by, new OptionArgument<>(on, argument));
        return this;
    }

    @Override
    public <A extends Annotation> LiteCommandsBuilderImpl<SENDER> argument(Class<A> annotation, Class<?> on, Argument<A> argument) {
        this.argumentsRegistry.register(annotation, on, argument);
        return this;
    }

    @Override
    public <A extends Annotation> LiteCommandsBuilderImpl<SENDER> argument(Class<A> annotation, Class<?> on, String by, Argument<A> argument) {
        this.argumentsRegistry.register(annotation, on, by, argument);
        return this;
    }

    @Override
    public LiteCommands<SENDER> register() {
        if (registryPlatform == null) {
            throw new IllegalStateException("Registry platform is not set");
        }

        LiteCommands<SENDER> commands = new LiteCommandsImpl<>(registryPlatform, executeResultHandler);

        this.injector = this.injector.fork(resources -> {
            resources.on(Object.class).assignHandler(new InjectorHandler<>(typeBinds, contextualBinds));
        });

        if (this.commandStateFactory == null) {
            this.commandStateFactory = new LiteCommandFactory(this.injector, this.argumentsRegistry, this.editorRegistry);
        }

        for (Consumer<CommandStateFactory> editor : this.commandStateFactoryEditors) {
            editor.accept(this.commandStateFactory);
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

    public static <T> LiteCommandsBuilder<T> builder() {
        return new LiteCommandsBuilderImpl<>();
    }

}
