package dev.rollczi.litecommands.argument.parser;

import dev.rollczi.litecommands.requirement.RequirementResult;
import dev.rollczi.litecommands.shared.FailedReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ParseResult<EXPECTED> implements RequirementResult<EXPECTED> {

    private final @Nullable EXPECTED successfulResult;
    private final @Nullable FailedReason failedResult;

    private ParseResult(@Nullable EXPECTED successfulResult, @Nullable FailedReason failedResult) {
        if (successfulResult != null && failedResult != null) {
            throw new IllegalArgumentException("Cannot be both successful and failed");
        }

        if (successfulResult == null && failedResult == null) {
            throw new IllegalArgumentException("Cannot be both empty");
        }

        this.successfulResult = successfulResult;
        this.failedResult = failedResult;
    }

    @Override
    public boolean isSuccessful() {
        return this.successfulResult != null;
    }

    @Override
    public boolean isFailed() {
        return this.failedResult != null;
    }

    @Override
    public @NotNull EXPECTED getSuccess() {
        if (this.successfulResult == null) {
            throw new IllegalStateException("Cannot get successful result when it is empty");
        }

        return this.successfulResult;
    }

    @Override
    public @NotNull Object getFailedReason() {
        if (this.failedResult == null) {
            throw new IllegalStateException("Cannot get failed reason when it is empty");
        }

        return this.failedResult.getReason();
    }

    public static <PARSED> ParseResult<PARSED> success(PARSED parsed) {
        return new ParseResult<>(parsed, null);
    }

    public static <EXPECTED> ParseResult<EXPECTED> failure(FailedReason failedReason) {
        return new ParseResult<>(null, failedReason);
    }

    public static <EXPECTED> ParseResult<EXPECTED> failure(Object failedReason) {
        return new ParseResult<>(null, FailedReason.of(failedReason));
    }

    public static <EXPECTED> ParseResult<EXPECTED> failure() {
        return new ParseResult<>(null, FailedReason.empty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParseResult<?> that = (ParseResult<?>) o;
        return Objects.equals(successfulResult, that.successfulResult) && Objects.equals(failedResult, that.failedResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successfulResult, failedResult);
    }

    @Override
    public String toString() {
        return "ParseResult{" +
            "successfulResult=" + successfulResult +
            ", failedResult=" + failedResult +
            '}';
    }
}
