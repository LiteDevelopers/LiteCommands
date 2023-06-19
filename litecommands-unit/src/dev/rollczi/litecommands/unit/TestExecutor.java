package dev.rollczi.litecommands.unit;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.ArgumentResult;
import dev.rollczi.litecommands.argument.parser.ArgumentParser;
import dev.rollczi.litecommands.argument.parser.ArgumentParserSet;
import dev.rollczi.litecommands.argument.parser.ArgumentRawInputParser;
import dev.rollczi.litecommands.argument.input.ArgumentsInputMatcher;
import dev.rollczi.litecommands.argument.input.RawInput;
import dev.rollczi.litecommands.command.requirement.RequirementMatch;
import dev.rollczi.litecommands.command.requirement.CommandArgumentRequirement;
import dev.rollczi.litecommands.command.requirement.CommandRequirement;
import dev.rollczi.litecommands.command.AbstractCommandExecutor;
import dev.rollczi.litecommands.command.CommandExecutorMatchResult;
import dev.rollczi.litecommands.command.requirement.CommandRequirementResult;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.range.Range;
import dev.rollczi.litecommands.wrapper.Wrap;
import dev.rollczi.litecommands.wrapper.Wrapper;
import dev.rollczi.litecommands.wrapper.WrapFormat;
import dev.rollczi.litecommands.wrapper.implementations.ValueWrapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class TestExecutor<SENDER> extends AbstractCommandExecutor<SENDER, CommandRequirement<SENDER, ?>> {

    public TestExecutor() {
        super(Collections.emptyList());
    }

    @Override
    public CommandExecutorMatchResult match(List<RequirementMatch<CommandRequirement<SENDER, ?>>> results) {
        return null;
    }

    public <T> TestExecutor<SENDER> withArg(String name, Class<T> type, BiFunction<Invocation<SENDER>, String, ArgumentResult<T>> parser) {
        requirements.add(new TestArgumentCommandRequirement<>(new TestArgument<>(name, type), new ValueWrapper(), new ArgumentParserSet<SENDER, T>() {
            @Override
            public <INPUT> Optional<ArgumentParser<SENDER, INPUT, T>> getParser(Class<INPUT> inType) {
                if (inType != RawInput.class) {
                    return Optional.empty();
                }

                return Optional.of((ArgumentParser<SENDER, INPUT, T>) new ArgumentRawInputParser<SENDER, T>() {
                    @Override
                    public Range getRange() {
                        return Range.of(1);
                    }

                    @Override
                    public ArgumentResult<T> parse(Invocation<SENDER> invocation, Argument<T> argument, RawInput input) {
                        return parser.apply(invocation, input.next());
                    }
                });
            }
        }));

        return this;
    }

    private class TestArgumentCommandRequirement<PARSED> implements CommandArgumentRequirement<SENDER, PARSED> {

        private final Argument<PARSED> argument;
        private final Wrapper wrapper;
        private final ArgumentParserSet<SENDER, PARSED> parserSet;

        public TestArgumentCommandRequirement(Argument<PARSED> argument, Wrapper wrapper, ArgumentParserSet<SENDER, PARSED> parserSet) {
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
        public <MATCHER extends ArgumentsInputMatcher<MATCHER>> CommandRequirementResult<PARSED> match(Invocation<SENDER> invocation, MATCHER matcher) {
            ArgumentResult<PARSED> matchArgument = matcher.nextArgument(invocation, argument, parserSet);

            if (matchArgument.isFailed()) {
                return CommandRequirementResult.failure(matchArgument.getFailedReason());
            }

            return CommandRequirementResult.success(() -> new Wrap<PARSED>() {
                @Override
                public Object unwrap() {
                    return matchArgument.getSuccessfulResult().getExpectedProvider();
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
