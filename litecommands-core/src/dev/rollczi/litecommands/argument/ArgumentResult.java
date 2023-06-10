package dev.rollczi.litecommands.argument;

import dev.rollczi.litecommands.wrapper.ValueToWrap;
import org.jetbrains.annotations.Nullable;

public class ArgumentResult<EXPECTED> {

    private final @Nullable SuccessfulResult<EXPECTED> successfulResult;
    private final @Nullable FailedReason failedResult;

    private ArgumentResult(@Nullable SuccessfulResult<EXPECTED> successfulResult, @Nullable FailedReason failedResult) {
        if (successfulResult != null && failedResult != null) {
            throw new IllegalArgumentException("Cannot be both successful and failed");
        }

        if (successfulResult == null && failedResult == null) {
            throw new IllegalArgumentException("Cannot be both empty");
        }

        this.successfulResult = successfulResult;
        this.failedResult = failedResult;
    }

    public boolean isSuccessful() {
        return this.successfulResult != null;
    }

    public boolean isFailed() {
        return this.failedResult != null;
    }

    public SuccessfulResult<EXPECTED> getSuccessfulResult() {
        if (this.successfulResult == null) {
            throw new IllegalStateException("Cannot get successful result when it is empty");
        }

        return this.successfulResult;
    }

    public FailedReason getFailedReason() {
        if (this.failedResult == null) {
            throw new IllegalStateException("Cannot get failed reason when it is empty");
        }

        return this.failedResult;
    }

    public static <PARSED> ArgumentResult<PARSED> successMultilevel(ValueToWrap<PARSED> parsed, int consumedRawArguments) {
        return new ArgumentResult<>(SuccessfulResult.of(parsed), null);
    }

    public static <PARSED> ArgumentResult<PARSED> success(PARSED parsed) {
        return new ArgumentResult<>(SuccessfulResult.of(() -> parsed), null);
    }

    public static <EXPECTED> ArgumentResult<EXPECTED> success(ValueToWrap<EXPECTED> expectedReturn) {
        return new ArgumentResult<>(SuccessfulResult.of(expectedReturn), null);
    }

    public static <EXPECTED> ArgumentResult<EXPECTED> failure(FailedReason failedReason) {
        return new ArgumentResult<>(null, failedReason);
    }

    public static <EXPECTED> ArgumentResult<EXPECTED> failure(Object failedReason) {
        return new ArgumentResult<>(null, FailedReason.of(failedReason));
    }

    public static <EXPECTED> ArgumentResult<EXPECTED> failure() {
        return new ArgumentResult<>(null, FailedReason.empty());
    }

}
