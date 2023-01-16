package dev.rollczi.litecommands.modern;

import dev.rollczi.litecommands.modern.command.argument.ArgumentContextual;
import dev.rollczi.litecommands.modern.command.argument.ArgumentKey;
import dev.rollczi.litecommands.modern.command.argument.invocation.ArgumentResolver;
import dev.rollczi.litecommands.modern.command.suggestion.SuggestionResolver;
import dev.rollczi.litecommands.modern.extension.LiteCommandsExtension;
import dev.rollczi.litecommands.modern.platform.Platform;

public interface LiteCommandsBuilder<SENDER, B extends LiteCommandsBuilder<SENDER, B>> {

    <T, ARG extends ArgumentResolver<SENDER, Object, T, ArgumentContextual<Object, T>>> LiteCommandsBuilder<SENDER, B> argumentOnly(
        Class<T> type,
        ARG argument
    );

    <T, ARG extends ArgumentResolver<SENDER, Object, T, ArgumentContextual<Object, T>>> LiteCommandsBuilder<SENDER, B> argumentOnly(
        Class<T> type,
        ARG argument,
        ArgumentKey argumentKey
    );

    <T, ARG extends ArgumentResolver<SENDER, Object, T, ArgumentContextual<Object, T>> & SuggestionResolver<SENDER, Object, T, ArgumentContextual<Object, T>>> LiteCommandsBuilder<SENDER, B> argument(
        Class<T> type,
        ARG argument
    );

    <T, ARG extends ArgumentResolver<SENDER, Object, T, ArgumentContextual<Object, T>> & SuggestionResolver<SENDER, Object, T, ArgumentContextual<Object, T>>> LiteCommandsBuilder<SENDER, B> argument(
        Class<T> type,
        ARG argument,
        ArgumentKey argumentKey
    );

    <D, T, CONTEXT extends ArgumentContextual<D, T>, ARG extends ArgumentResolver<SENDER, D, T, CONTEXT>> LiteCommandsBuilder<SENDER, B> argumentOnly(
        Class<D> determinantType,
        Class<T> expectedType,
        Class<CONTEXT> contextType,
        ARG argument
    );

    <D, T, CONTEXT extends ArgumentContextual<D, T>, ARG extends ArgumentResolver<SENDER, D, T, CONTEXT>> LiteCommandsBuilder<SENDER, B> argumentOnly(
        Class<D> determinantType,
        Class<T> expectedType,
        Class<CONTEXT> contextType,
        ARG argument,
        ArgumentKey argumentKey
    );

    <D, T, CONTEXT extends ArgumentContextual<D, T>, ARG extends ArgumentResolver<SENDER, D, T, CONTEXT> & SuggestionResolver<SENDER, D, T, CONTEXT>> LiteCommandsBuilder<SENDER, B> argument(Class<D> determinantType, Class<T> expectedType, Class<CONTEXT> contextType, ARG argument);

    <D, T, CONTEXT extends ArgumentContextual<D, T>, ARG extends ArgumentResolver<SENDER, D, T, CONTEXT> & SuggestionResolver<SENDER, D, T, CONTEXT>> LiteCommandsBuilder<SENDER, B> argument(Class<D> determinantType, Class<T> expectedType, Class<CONTEXT> contextType, ARG argument, ArgumentKey argumentKey);

    <NEW extends LiteCommandsBuilder<SENDER, NEW>> NEW withExtension(LiteCommandsExtension<SENDER, NEW> extension);

    LiteCommandsBuilder<SENDER, B> platform(Platform<SENDER> platform);

    LiteCommands<SENDER> register();

}
