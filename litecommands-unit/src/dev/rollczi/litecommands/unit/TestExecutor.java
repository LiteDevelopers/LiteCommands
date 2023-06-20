package dev.rollczi.litecommands.unit;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.parser.Parser;
import dev.rollczi.litecommands.argument.parser.ParserSet;
import dev.rollczi.litecommands.argument.parser.RawInputParser;
import dev.rollczi.litecommands.input.raw.RawInput;
import dev.rollczi.litecommands.command.CommandExecuteResult;
import dev.rollczi.litecommands.command.requirement.RequirementSuccessMatch;
import dev.rollczi.litecommands.command.requirement.ArgumentRequirement;
import dev.rollczi.litecommands.command.requirement.Requirement;
import dev.rollczi.litecommands.command.AbstractCommandExecutor;
import dev.rollczi.litecommands.command.CommandExecutorMatchResult;
import dev.rollczi.litecommands.command.requirement.RequirementResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.argument.parser.input.ParseableInput;
import dev.rollczi.litecommands.range.Range;
import dev.rollczi.litecommands.wrapper.Wrap;
import dev.rollczi.litecommands.wrapper.Wrapper;
import dev.rollczi.litecommands.wrapper.WrapFormat;
import dev.rollczi.litecommands.wrapper.std.ValueWrapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class TestExecutor<SENDER> extends AbstractCommandExecutor<SENDER, Requirement<SENDER, ?>> {

    private final Object result;

    public TestExecutor(Object result) {
        super(Collections.emptyList());
        this.result = result;
    }

    public TestExecutor() {
        super(Collections.emptyList());
        this.result = null;
    }

    public <T> TestExecutor<SENDER> withArg(String name, Class<T> type, BiFunction<Invocation<SENDER>, String, ParseResult<T>> parser) {
        requirements.add(new TestArgumentRequirement<>(new TestArgument<>(name, type), new ValueWrapper(), new ParserSet<SENDER, T>() {
            @Override
            public <INPUT> Optional<Parser<SENDER, INPUT, T>> getParser(Class<INPUT> inType) {
                if (inType != RawInput.class) {
                    return Optional.empty();
                }

                return Optional.of((Parser<SENDER, INPUT, T>) new RawInputParser<SENDER, T>() {
                    @Override
                    public Range getRange() {
                        return Range.of(1);
                    }

                    @Override
                    public ParseResult<T> parse(Invocation<SENDER> invocation, Argument<T> argument, RawInput input) {
                        return parser.apply(invocation, input.next());
                    }
                });
            }
        }));

        return this;
    }

    @Override
    public CommandExecutorMatchResult match(List<RequirementSuccessMatch<SENDER, Requirement<SENDER, ?>, Object>> results) {
        return CommandExecutorMatchResult.success(() -> CommandExecuteResult.success(result));
    }

    private class TestArgumentRequirement<PARSED> implements ArgumentRequirement<SENDER, PARSED> {

        private final Argument<PARSED> argument;
        private final Wrapper wrapper;
        private final ParserSet<SENDER, PARSED> parserSet;

        public TestArgumentRequirement(Argument<PARSED> argument, Wrapper wrapper, ParserSet<SENDER, PARSED> parserSet) {
            this.argument = argument;
            this.wrapper = wrapper;
            this.parserSet = parserSet;
        }

        @Override
        public Argument<?> getArgument() {
            return this.argument;
        }

        @Override
        public boolean isOptional() {
            return wrapper.canCreateEmpty();
        }

        @Override
        public <MATCHER extends ParseableInput.ParsableInputMatcher<MATCHER>> RequirementResult<PARSED> match(Invocation<SENDER> invocation, MATCHER matcher) {
            ParseResult<PARSED> matchArgument = matcher.nextArgument(invocation, argument, parserSet);

            if (matchArgument.isFailed()) {
                return RequirementResult.failure(matchArgument.getFailedReason());
            }

            return RequirementResult.success(() -> new Wrap<PARSED>() {
                @Override
                public Object unwrap() {
                    return matchArgument.getSuccessfulResult();
                }

                @Override
                public Class<PARSED> getExpectedType() {
                    return argument.getWrapperFormat().getParsedType();
                }
            });
        }
    }

    private static class TestArgument<PARSED> implements Argument<PARSED> {

        private final String name;
        private final WrapFormat<PARSED, ?> wrapFormat;

        private TestArgument(String name, Class<PARSED> type) {
            this.name = name;
            this.wrapFormat = WrapFormat.notWrapped(type);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public WrapFormat<PARSED, ?> getWrapperFormat() {
            return this.wrapFormat;
        }
    }

}
