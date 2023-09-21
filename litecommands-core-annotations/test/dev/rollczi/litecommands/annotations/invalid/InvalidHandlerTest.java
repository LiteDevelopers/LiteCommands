package dev.rollczi.litecommands.annotations.invalid;

import dev.rollczi.litecommands.annotations.LiteConfig;
import dev.rollczi.litecommands.annotations.LiteTestSpec;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.Command;
import dev.rollczi.litecommands.execute.Execute;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.unit.TestSender;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InvalidHandlerTest extends LiteTestSpec {

    static AtomicReference<InvalidUsage<TestSender>> invalidUsage = new AtomicReference<>();

    static LiteConfig config = builder -> builder
        .invalidUsage((invocation, result, chain) -> invalidUsage.set(result));

    @Command(name = "test")
    static class TestCommand {

        @Execute
        void execute(@Arg("arg") int test) {}

    }

    @Test
    void test() {
        platform.execute("test a")
            .assertFailedAs(InvalidUsage.class);

        InvalidUsage<TestSender> usage = invalidUsage.get();

        assertNotNull(usage);
        assertEquals(InvalidUsage.Cause.INVALID_ARGUMENT, usage.getCause());
        assertEquals("/test <arg>", usage.getSchematic().first());
    }

}
