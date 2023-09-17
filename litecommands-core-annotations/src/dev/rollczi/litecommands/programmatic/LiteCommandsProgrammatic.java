package dev.rollczi.litecommands.programmatic;

import dev.rollczi.litecommands.annotation.processor.AnnotationProcessorService;
import dev.rollczi.litecommands.builder.LiteCommandsInternalBuilderApi;
import dev.rollczi.litecommands.builder.LiteCommandsProvider;
import dev.rollczi.litecommands.command.builder.CommandBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class LiteCommandsProgrammatic<SENDER> implements LiteCommandsProvider<SENDER> {

    private final List<LiteCommand<SENDER>> commands = new ArrayList<>();

    public LiteCommandsProgrammatic(Collection<LiteCommand<SENDER>> commands) {
        this.commands.addAll(commands);
    }

    @Override
    public List<CommandBuilder<SENDER>> provide(LiteCommandsInternalBuilderApi<SENDER, ?> builder) {
        builder.getWrapperRegistry();
        AnnotationProcessorService<SENDER> annotationProcessorService = builder.getAnnotationProcessorRegistry();

        ProgrammaticSourceProcessor<SENDER> processor = new ProgrammaticSourceProcessor<>(annotationProcessorService);

        return commands.stream()
            .map(liteCommand -> processor.processBuilder(liteCommand))
            .map(senderCommandBuilder -> builder.getEditorService().edit(senderCommandBuilder))
            .collect(Collectors.toList());
    }

    @SafeVarargs
    public static <SENDER> LiteCommandsProgrammatic<SENDER> of(LiteCommand<SENDER>... commands) {
        return new LiteCommandsProgrammatic<>(Arrays.asList(commands));
    }

    public static <SENDER> LiteCommandsProgrammatic<SENDER> of(Collection<LiteCommand<SENDER>> commands) {
        return new LiteCommandsProgrammatic<>(commands);
    }

}