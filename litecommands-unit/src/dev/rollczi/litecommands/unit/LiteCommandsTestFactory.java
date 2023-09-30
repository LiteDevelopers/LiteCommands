package dev.rollczi.litecommands.unit;

import dev.rollczi.litecommands.LiteCommandsFactory;
import dev.rollczi.litecommands.builder.LiteCommandsBuilder;

import java.util.function.UnaryOperator;

public final class LiteCommandsTestFactory {

    private LiteCommandsTestFactory() {
    }

    public static TestPlatform startPlatform(UnaryOperator<LiteCommandsBuilder<TestSender, TestSettings, ?>> operator) {
        TestPlatform testPlatform = new TestPlatform();
        LiteCommandsBuilder<TestSender, TestSettings, ?> builder = LiteCommandsFactory.builder(TestSender.class, testPlatform);

        operator.apply(builder)
            .result(String.class, (invocation, result, chain) -> invocation.sender().sendMessage(result))
            .invalidUsage((invocation, result, chain) -> {})
            .build()
            .register();

        return testPlatform;
    }

}
