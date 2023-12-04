package dev.rollczi.litecommands;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.argument.parser.Parser;
import dev.rollczi.litecommands.argument.parser.ParserRegistry;
import dev.rollczi.litecommands.argument.parser.ParserRegistryImpl;
import dev.rollczi.litecommands.argument.parser.TypedParser;
import dev.rollczi.litecommands.bind.BindProvider;
import dev.rollczi.litecommands.command.CommandMerger;
import dev.rollczi.litecommands.configurator.LiteConfigurator;
import dev.rollczi.litecommands.extension.LiteCommandsProviderExtension;
import dev.rollczi.litecommands.extension.annotations.AnnotationsExtension;
import dev.rollczi.litecommands.extension.annotations.LiteAnnotationsProcessorExtension;
import dev.rollczi.litecommands.processor.LiteBuilderProcessor;
import dev.rollczi.litecommands.command.executor.CommandExecuteService;
import dev.rollczi.litecommands.context.ContextProvider;
import dev.rollczi.litecommands.bind.BindRegistry;
import dev.rollczi.litecommands.extension.LiteExtension;
import dev.rollczi.litecommands.context.ContextRegistry;
import dev.rollczi.litecommands.editor.Editor;
import dev.rollczi.litecommands.handler.exception.ExceptionHandler;
import dev.rollczi.litecommands.handler.result.ResultHandleService;
import dev.rollczi.litecommands.handler.result.ResultHandleServiceImpl;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.message.InvokedMessage;
import dev.rollczi.litecommands.message.Message;
import dev.rollczi.litecommands.message.MessageKey;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import dev.rollczi.litecommands.platform.PlatformSettingsConfigurator;
import dev.rollczi.litecommands.programmatic.LiteCommand;
import dev.rollczi.litecommands.programmatic.LiteCommandsProgrammatic;
import dev.rollczi.litecommands.scheduler.Scheduler;
import dev.rollczi.litecommands.scheduler.SchedulerSameThreadImpl;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.schematic.SchematicGenerator;
import dev.rollczi.litecommands.schematic.SimpleSchematicGenerator;
import dev.rollczi.litecommands.scope.Scope;
import dev.rollczi.litecommands.argument.suggester.Suggester;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import dev.rollczi.litecommands.suggestion.SuggestionService;
import dev.rollczi.litecommands.argument.suggester.TypedSuggester;
import dev.rollczi.litecommands.argument.suggester.SuggesterRegistry;
import dev.rollczi.litecommands.argument.suggester.SuggesterRegistryImpl;
import dev.rollczi.litecommands.shared.Preconditions;
import dev.rollczi.litecommands.command.CommandManager;
import dev.rollczi.litecommands.command.CommandRoute;
import dev.rollczi.litecommands.command.builder.CommandBuilder;
import dev.rollczi.litecommands.command.builder.CommandBuilderCollector;
import dev.rollczi.litecommands.editor.EditorService;
import dev.rollczi.litecommands.platform.PlatformSettings;
import dev.rollczi.litecommands.platform.Platform;
import dev.rollczi.litecommands.validator.Validator;
import dev.rollczi.litecommands.validator.ValidatorService;
import dev.rollczi.litecommands.wrapper.Wrapper;
import dev.rollczi.litecommands.wrapper.WrapperRegistry;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class LiteCommandsBaseBuilder<SENDER, C extends PlatformSettings, B extends LiteCommandsBaseBuilder<SENDER, C, B>> implements
    LiteCommandsBuilder<SENDER, C, B>,
    LiteCommandsInternal<SENDER, C> {

    protected final Class<SENDER> senderClass;
    protected final Platform<SENDER, C> platform;

    protected final Set<LiteBuilderProcessor<SENDER, C>> preProcessors = new LinkedHashSet<>();
    protected final Set<LiteBuilderProcessor<SENDER, C>> postProcessors = new LinkedHashSet<>();

    protected final List<LiteExtension<SENDER, ?>> extensions = new ArrayList<>();
    protected final List<LiteCommandsProviderExtension<SENDER, ?>> commandsProviderExtensions = new ArrayList<>();

    protected final EditorService<SENDER> editorService = new EditorService<>();
    protected final ValidatorService<SENDER> validatorService = new ValidatorService<>();
    protected final ParserRegistry<SENDER> parserRegistry = new ParserRegistryImpl<>();
    protected final SuggesterRegistry<SENDER> suggesterRegistry = new SuggesterRegistryImpl<>();
    protected final BindRegistry bindRegistry = new BindRegistry();
    protected final ContextRegistry<SENDER> contextRegistry = new ContextRegistry<>();
    protected final ResultHandleService<SENDER> resultHandleService = new ResultHandleServiceImpl<>();
    protected final CommandBuilderCollector<SENDER> commandBuilderCollector = new CommandBuilderCollector<>();
    protected final MessageRegistry<SENDER> messageRegistry = new MessageRegistry<SENDER>();
    protected final WrapperRegistry wrapperRegistry = new WrapperRegistry();

    protected Scheduler scheduler = new SchedulerSameThreadImpl();
    protected SchematicGenerator<SENDER> schematicGenerator = new SimpleSchematicGenerator<>(SchematicFormat.angleBrackets(), validatorService, wrapperRegistry);

    /**
     * Constructor for {@link LiteCommandsBaseBuilder}
     *
     * @param senderClass class of sender
     * @param platform platform
     */
    public LiteCommandsBaseBuilder(Class<SENDER> senderClass, Platform<SENDER, C> platform) {
        this.senderClass = senderClass;
        this.platform = platform;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> settings(PlatformSettingsConfigurator<C> configurator) {
        C newConfig = configurator.apply(this.platform.getConfiguration());
        Preconditions.notNull(newConfig, "configuration");

        this.platform.setConfiguration(newConfig);

        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> commands(LiteCommandsProvider<SENDER> commandsProvider) {
        this.preProcessExtensionsOnProvider(commandsProvider);
        this.commandBuilderCollector.add(commandsProvider.toInternalProvider(this));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LiteCommandsBuilder<SENDER, C, B> commands(Object... commands) {
        List<LiteCommandsProvider<SENDER>> providers = new ArrayList<>();
        Collection<LiteCommand<SENDER>> programmatic = new ArrayList<>();
        List<Class<?>> classes = new ArrayList<>();
        List<Object> instances = new ArrayList<>();

        for (Object command : commands) {
            if (command instanceof LiteCommandsProvider) {
                providers.add((LiteCommandsProvider<SENDER>) command);
                continue;
            }

            if (command instanceof LiteCommand) {
                programmatic.add((LiteCommand<SENDER>) command);
                continue;
            }

            if (command instanceof Class) {
                classes.add((Class<?>) command);
                continue;
            }

            instances.add(command);
        }

        for (LiteCommandsProvider<SENDER> provider : providers) {
            this.commands(provider);
        }

        if (!programmatic.isEmpty()) {
            this.commands(LiteCommandsProgrammatic.of(programmatic));
        }

        if (!classes.isEmpty() || !instances.isEmpty()) {
            this.commands(LiteCommandsAnnotations.<SENDER>create()
                .load(instances.toArray(new Object[0]))
                .loadClasses(classes.toArray(new Class<?>[0]))
            );
        }

        return this;
    }

    /**
     * Pre-process extensions on provider before executing {@link CommandBuilderCollector#collectCommands()}
     * Kinda magic, but it works.
     *
     * @param commandsProvider provider of commands.
     */
    private void preProcessExtensionsOnProvider(LiteCommandsProvider<SENDER> commandsProvider) {
        this.preProcessor((builder, internal) -> {
            for (LiteCommandsProviderExtension<SENDER, ?> extension : commandsProviderExtensions) {
                extension.extendCommandsProvider(this, this, commandsProvider);
            }
        });
    }

    @Override
    public <IN, T, PARSER extends Parser<SENDER, IN, T>> LiteCommandsBuilder<SENDER, C, B> argumentParser(Class<T> type, PARSER parser) {
        this.parserRegistry.registerParser(type, ArgumentKey.of(), parser);
        return this;
    }

    @Override
    public <IN, PARSED, PARSER extends Parser<SENDER, IN, PARSED>>
    LiteCommandsBuilder<SENDER, C, B> argumentParser(Class<PARSED> type, ArgumentKey key, PARSER parser) {
        this.parserRegistry.registerParser(type, key, parser);
        return this;
    }

    @Override
    public <IN, T, ARGUMENT extends Argument<T>>
    LiteCommandsBuilder<SENDER, C, B> argumentParser(Class<T> type, TypedParser<SENDER, IN, T, ARGUMENT> parser) {
        this.parserRegistry.registerParser(type, ArgumentKey.typed(parser.getArgumentType()), parser);
        return this;
    }

    @Override
    public <IN, T, ARGUMENT extends Argument<T>>
    LiteCommandsBuilder<SENDER, C, B> argumentParser(Class<T> type, ArgumentKey key, TypedParser<SENDER, IN, T, ARGUMENT> parser) {
        this.parserRegistry.registerParser(type, key.withNamespace(parser.getArgumentType()), parser);
        return this;
    }

    @Override
    public <T> LiteCommandsBuilder<SENDER, C, B> argumentSuggester(Class<T> type, SuggestionResult suggestionResult) {
        this.suggesterRegistry.registerSuggester(type, ArgumentKey.of(), (invocation, argument, context) -> suggestionResult);
        return this;
    }

    @Override
    public <T> LiteCommandsBuilder<SENDER, C, B> argumentSuggester(Class<T> type, ArgumentKey key, SuggestionResult suggestionResult) {
        this.suggesterRegistry.registerSuggester(type, key, (invocation, argument, context) -> suggestionResult);
        return this;
    }

    @Override
    public <T, SUGGESTER extends Suggester<SENDER, T>>
    LiteCommandsBuilder<SENDER, C, B> argumentSuggester(Class<T> type, SUGGESTER suggester) {
        this.suggesterRegistry.registerSuggester(type, ArgumentKey.of(), suggester);
        return this;
    }

    @Override
    public <T, SUGGESTER extends Suggester<SENDER, T>>
    LiteCommandsBuilder<SENDER, C, B> argumentSuggester(Class<T> type, ArgumentKey key, SUGGESTER suggester) {
        this.suggesterRegistry.registerSuggester(type, key, suggester);
        return this;
    }

    @Override
    public <T, ARGUMENT extends Argument<T>>
    LiteCommandsBuilder<SENDER, C, B> argumentSuggester(Class<T> type, TypedSuggester<SENDER, T, ARGUMENT> suggester) {
        this.suggesterRegistry.registerSuggester(type, ArgumentKey.typed(suggester.getArgumentType()), suggester);
        return this;
    }

    @Override
    public <T, ARGUMENT extends Argument<T>>
    LiteCommandsBuilder<SENDER, C, B> argumentSuggester(Class<T> type, ArgumentKey key, TypedSuggester<SENDER, T, ARGUMENT> suggester) {
        this.suggesterRegistry.registerSuggester(type, key.withNamespace(suggester.getArgumentType()), suggester);
        return this;
    }

    @Override
    public <IN, T, RESOLVER extends Parser<SENDER, IN, T> & Suggester<SENDER, T>>
    LiteCommandsBuilder<SENDER, C, B> argument(Class<T> type, RESOLVER resolver) {
        this.argumentParser(type, resolver);
        this.argumentSuggester(type, resolver);
        return this;
    }

    @Override
    public <IN, PARSED, RESOLVER extends Parser<SENDER, IN, PARSED> & Suggester<SENDER, PARSED>>
    LiteCommandsBuilder<SENDER, C, B> argument(Class<PARSED> type, ArgumentKey key, RESOLVER resolver) {
        this.argumentParser(type, key, resolver);
        this.argumentSuggester(type, key, resolver);
        return this;
    }

    @Override
    public <IN, T, ARGUMENT extends Argument<T>, RESOLVER extends TypedParser<SENDER, IN, T, ARGUMENT> & Suggester<SENDER, T>>
    LiteCommandsBuilder<SENDER, C, B> argument(Class<T> type, RESOLVER resolver) {
        this.argumentParser(type, resolver);
        this.argumentSuggester(type, resolver);
        return this;
    }

    @Override
    public <IN, T, ARGUMENT extends Argument<T>, RESOLVER extends TypedParser<SENDER, IN, T, ARGUMENT> & Suggester<SENDER, T>>
    LiteCommandsBuilder<SENDER, C, B> argument(Class<T> type, ArgumentKey key, RESOLVER resolver) {
        this.argumentParser(type, key, resolver);
        this.argumentSuggester(type, key, resolver);
        return this;
    }

    @Override
    public <T> LiteCommandsBuilder<SENDER, C, B> context(Class<T> on, ContextProvider<SENDER, T> bind) {
        this.contextRegistry.registerProvider(on, bind);
        return this;
    }

    @Override
    public <T> LiteCommandsBuilder<SENDER, C, B> bind(Class<T> on, BindProvider<T> bindProvider) {
        this.bindRegistry.bindInstance(on, bindProvider);
        return this;
    }

    @Override
    public <T> LiteCommandsBuilder<SENDER, C, B> bind(Class<T> on, Supplier<T> bind) {
        this.bindRegistry.bindInstance(on, bind);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> bindUnsafe(Class<?> on, Supplier<?> bind) {
        this.bindRegistry.bindInstanceUnsafe(on, bind);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> scheduler(Scheduler scheduler) {
        this.scheduler.shutdown();
        this.scheduler = scheduler;
        return this;
    }

    @Override
    public <T, CONTEXT> LiteCommandsBuilder<SENDER, C, B> message(MessageKey<CONTEXT> key, Message<T, CONTEXT> message) {
        this.messageRegistry.register(key, message);
        return this;
    }

    @Override
    public <T, CONTEXT> LiteCommandsBuilder<SENDER, C, B> message(MessageKey<CONTEXT> key, InvokedMessage<SENDER, T, CONTEXT> message) {
        this.messageRegistry.register(key, message);
        return this;
    }

    @Override
    public <T, CONTEXT> LiteCommandsBuilder<SENDER, C, B> message(MessageKey<CONTEXT> key, T message) {
        this.messageRegistry.register(key, context -> message);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> editorGlobal(Editor<SENDER> editor) {
        this.editorService.editorGlobal(editor);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> editor(Scope scope, Editor<SENDER> editor) {
        this.editorService.editor(scope, editor);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> validatorGlobal(Validator<SENDER> validator) {
        this.validatorService.registerValidatorGlobal(validator);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> validator(Scope scope, Validator<SENDER> validator) {
        this.validatorService.registerValidator(scope, validator);
        return this;
    }

    @Override
    public <T> LiteCommandsBuilder<SENDER, C, B> result(Class<T> resultType, ResultHandler<SENDER, ? extends T> handler) {
        this.resultHandleService.registerHandler(resultType, handler);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> resultUnexpected(ResultHandler<SENDER, Object> handler) {
        this.resultHandleService.registerHandler(Object.class, handler);
        return this;
    }

    @Override
    public <E extends Throwable> LiteCommandsBuilder<SENDER, C, B> exception(Class<E> exceptionType, ExceptionHandler<SENDER, ? extends E> handler) {
        this.resultHandleService.registerHandler(exceptionType, handler);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> exceptionUnexpected(ExceptionHandler<SENDER, Throwable> handler) {
        this.resultHandleService.registerHandler(Throwable.class, handler);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> missingPermission(MissingPermissionsHandler<SENDER> handler) {
        this.resultHandleService.registerHandler(MissingPermissions.class, handler);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> invalidUsage(InvalidUsageHandler<SENDER> handler) {
        this.resultHandleService.registerHandler(InvalidUsage.class, handler);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> schematicGenerator(SchematicGenerator<SENDER> schematicGenerator) {
        this.schematicGenerator = schematicGenerator;
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> schematicGenerator(SchematicFormat format) {
        this.schematicGenerator = new SimpleSchematicGenerator<>(format, validatorService, wrapperRegistry);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> wrapper(Wrapper wrapper) {
        this.wrapperRegistry.registerFactory(wrapper);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> selfProcessor(LiteBuilderProcessor<SENDER, C> processor) {
        processor.process(this, this);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> preProcessor(LiteBuilderProcessor<SENDER, C> preProcessor) {
        this.preProcessors.add(preProcessor);
        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> postProcessor(LiteBuilderProcessor<SENDER, C> postProcessor) {
        this.postProcessors.add(postProcessor);
        return this;
    }

    @Override
    public <CONFIGURATION> LiteCommandsBuilder<SENDER, C, B> extension(LiteExtension<SENDER, CONFIGURATION> extension) {
        return this.extension(extension, configuration -> {});
    }

    @Override
    @SuppressWarnings("unchecked")
    public <CONFIGURATION, E extends LiteExtension<SENDER, CONFIGURATION>> LiteCommandsBuilder<SENDER, C, B> extension(E extension, LiteConfigurator<CONFIGURATION> configurator) {
        extension.configure(configurator);
        extension.extend(this, this);
        extensions.add(extension);

        if (extension instanceof LiteCommandsProviderExtension) {
            commandsProviderExtensions.add((LiteCommandsProviderExtension<SENDER, CONFIGURATION>) extension);
        }

        return this;
    }

    @Override
    public LiteCommandsBuilder<SENDER, C, B> annotations(LiteConfigurator<AnnotationsExtension<SENDER>> configuration) {
        return this.extension(new LiteAnnotationsProcessorExtension<>(), configuration);
    }

    @Override
    public LiteCommands<SENDER> build() {
        return this.build(true);
    }

    @Override
    public LiteCommands<SENDER> build(boolean register) {
        if (this.platform == null) {
            throw new IllegalStateException("No platform was set");
        }

        for (LiteBuilderProcessor<SENDER, C> processor : preProcessors) {
            processor.process(this, this);
        }

        CommandExecuteService<SENDER> commandExecuteService = new CommandExecuteService<>(validatorService, resultHandleService, scheduler, schematicGenerator, parserRegistry, contextRegistry, wrapperRegistry, bindRegistry);
        SuggestionService<SENDER> suggestionService = new SuggestionService<>(parserRegistry, suggesterRegistry, validatorService);
        CommandManager<SENDER> commandManager = new CommandManager<>(this.platform, commandExecuteService, suggestionService);

        CommandMerger<SENDER> commandMerger = new CommandMerger<>();

        for (CommandBuilder<SENDER> collected : this.commandBuilderCollector.collectCommands()) {
            CommandBuilder<SENDER> edited = editorService.edit(collected);

            if (!edited.buildable()) {
                continue;
            }

            for (CommandRoute<SENDER> commandRoute : edited.build(commandManager.getRoot())) {
                commandMerger.merge(commandRoute);
            }
        }

        for (CommandRoute<SENDER> mergedCommand : commandMerger.getMergedCommands()) {
            commandManager.register(mergedCommand);
        }

        for (LiteBuilderProcessor<SENDER, C> processor : postProcessors) {
            processor.process(this, this);
        }

        LiteCommands<SENDER> liteCommand = new LiteCommandsImpl<>(commandManager);

        if (register) {
            liteCommand.register();
        }

        return liteCommand;
    }

    /**
     * Internal API
     */

    @Override
    @ApiStatus.Internal
    public Class<SENDER> getSenderClass() {
        return this.senderClass;
    }

    @Override
    @ApiStatus.Internal
    public Platform<SENDER, C> getPlatform() {
        return this.platform;
    }

    @Override
    @ApiStatus.Internal
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    @ApiStatus.Internal
    public EditorService<SENDER> getEditorService() {
        return this.editorService;
    }

    @Override
    @ApiStatus.Internal
    public ValidatorService<SENDER> getValidatorService() {
        return this.validatorService;
    }

    @Override
    @ApiStatus.Internal
    public ParserRegistry<SENDER> getParserRegistry() {
        return this.parserRegistry;
    }

    @Override
    @ApiStatus.Internal
    public SuggesterRegistry<SENDER> getSuggesterRegistry() {
        return this.suggesterRegistry;
    }

    @Override
    @ApiStatus.Internal
    public BindRegistry getBindRegistry() {
        return this.bindRegistry;
    }

    @Override
    @ApiStatus.Internal
    public ContextRegistry<SENDER> getContextRegistry() {
        return this.contextRegistry;
    }

    @Override
    @ApiStatus.Internal
    public ResultHandleService<SENDER> getResultService() {
        return this.resultHandleService;
    }

    @Override
    @ApiStatus.Internal
    public CommandBuilderCollector<SENDER> getCommandBuilderCollector() {
        return this.commandBuilderCollector;
    }

    @Override
    @ApiStatus.Internal
    public MessageRegistry<SENDER> getMessageRegistry() {
        return this.messageRegistry;
    }

    @Override
    @ApiStatus.Internal
    public WrapperRegistry getWrapperRegistry() {
        return this.wrapperRegistry;
    }

}
