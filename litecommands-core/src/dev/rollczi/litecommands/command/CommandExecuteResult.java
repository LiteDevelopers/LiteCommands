package dev.rollczi.litecommands.command;

import dev.rollczi.litecommands.argument.FailedReason;
import dev.rollczi.litecommands.util.Preconditions;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class CommandExecuteResult {

    private final @Nullable Object result;
    private final @Nullable Throwable throwable;
    private final @Nullable Object error;

    private CommandExecuteResult(@Nullable Object result, @Nullable Throwable throwable, @Nullable Object error) {
        this.result = result;
        this.throwable = throwable;
        this.error = error;
    }

    public Object getResult() {
        return this.result;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public Object getError() {
        return error;
    }

    public boolean isSuccessful() {
        return this.result != null;
    }

    public boolean isFailed() {
        return this.error != null;
    }

    public boolean isThrown() {
        return this.throwable != null;
    }

    public static CommandExecuteResult success(Object result) {
        return new CommandExecuteResult(result, null, null);
    }

    public static CommandExecuteResult thrown(Throwable exception) {
        Preconditions.notNull(exception, "exception cannot be null");

        return new CommandExecuteResult(null, exception, null);
    }

    public static CommandExecuteResult failed(Object error) {
        Preconditions.notNull(error, "failed cannot be null");

        return new CommandExecuteResult(null, null, error);
    }

    public static CommandExecuteResult failed(FailedReason failedReason) {
        Preconditions.notNull(failedReason, "failed cannot be null");

        return new CommandExecuteResult(null, null, failedReason.getReasonOr(null));
    }

}
