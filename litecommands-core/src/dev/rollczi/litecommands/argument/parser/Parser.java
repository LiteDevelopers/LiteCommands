package dev.rollczi.litecommands.argument.parser;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.input.raw.RawInput;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.range.Rangeable;
import org.jetbrains.annotations.ApiStatus;

public interface Parser<SENDER, PARSED> extends Rangeable<Argument<PARSED>> {

    /**
     * This method is used to parse the input and return the result.
     */
    ParseResult<PARSED> parse(Invocation<SENDER> invocation, Argument<PARSED> argument, RawInput input);

    /**
     * This method is used to check if the argument can be parsed by the parser. (pre-parsing check)
     */
    default boolean canParse(Invocation<SENDER> invocation, Argument<PARSED> argument) {
        return true;
    }

    /**
     * This method is used to check if the input can be parsed by the parser.
     * It is used in the suggestion system.
     * (you can override it to provide custom behavior and improve performance)
     */
    @ApiStatus.Experimental
    default boolean matchParse(Invocation<SENDER> invocation, Argument<PARSED> argument, RawInput input) {
        ParseResult<PARSED> parsed = parse(invocation, argument, input);

        return parsed.isSuccessful() || parsed.isSuccessfulNull();
    }

}
