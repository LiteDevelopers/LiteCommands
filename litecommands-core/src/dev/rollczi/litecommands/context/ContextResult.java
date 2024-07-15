package dev.rollczi.litecommands.context;

import dev.rollczi.litecommands.requirement.RequirementCondition;
import dev.rollczi.litecommands.requirement.RequirementResult;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ContextResult<T> implements RequirementResult<T> {

    private final @Nullable Supplier<T> result;
    private final List<RequirementCondition> conditions;
    private final Object error;

    private ContextResult(@Nullable Supplier<T> result, Object error,  List<RequirementCondition> conditions) {
        this.result = result;
        this.error = error;
        this.conditions = conditions;
    }

    @Override
    public @NotNull T getSuccess() {
        if (result == null) {
            throw new IllegalStateException("Cannot get success result from failed result");
        }

        return result.get();
    }

    @Override
    public @NotNull Object getFailedReason() {
        return error;
    }

    @Override
    public @NotNull List<RequirementCondition> getConditions() {
        return conditions;
    }

    @Override
    public boolean isFailed() {
        return error != null;
    }

    @Override
    public boolean isSuccessful() {
        return result != null;
    }

    @Override
    public boolean isSuccessfulNull() {
        return false;
    }

    public static <T> ContextResult<T> ok(Supplier<T> supplier) {
        return new ContextResult<>(supplier, null, Collections.emptyList());
    }

    public static <T> ContextResult<T> error(Object error) {
        return new ContextResult<>(null, error, Collections.emptyList());
    }

    @ApiStatus.Experimental
    public static <T> ContextResult<T> conditional(Supplier<T> supplier, List<RequirementCondition> conditions) {
        return new ContextResult<>(supplier, null, Collections.unmodifiableList(conditions));
    }

}
