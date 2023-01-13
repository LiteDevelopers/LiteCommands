package dev.rollczi.litecommands.modern.command;

import dev.rollczi.litecommands.shared.Validation;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class CommandExecuteResult {

    private final @Nullable Object result;
    private final Class<?> resultType;
    private final @Nullable Exception exception;

    private CommandExecuteResult(@Nullable Object result, Class<?> resultType, @Nullable Exception exception) {
        this.result = result;
        this.resultType = resultType;
        this.exception = exception;
    }

    public Option<Object> getResult() {
        Validation.isTrue(this.isSuccessful(), "Cannot get result when command failed");

        return Option.of(result);
    }

    public Exception getException() {
        Validation.isTrue(this.isFailed(), "Cannot get exception when command was successful");

        return exception;
    }

    public boolean isFailed() {
        return exception != null;
    }

    public boolean isSuccessful() {
        return exception == null;
    }

    public static CommandExecuteResult success(@Nullable Object result, Class<?> resultType) {
        return new CommandExecuteResult(result, resultType, null);
    }

    public static CommandExecuteResult failed(Exception exception) {
        Validation.isNotNull(exception, "exception cannot be null");

        return new CommandExecuteResult(null, null, exception);
    }

}
