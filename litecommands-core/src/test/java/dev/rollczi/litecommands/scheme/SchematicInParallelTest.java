package dev.rollczi.litecommands.scheme;

import dev.rollczi.litecommands.TestFactory;
import dev.rollczi.litecommands.TestHandle;
import dev.rollczi.litecommands.TestPlatform;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.FindResult;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.section.Section;
import dev.rollczi.litecommands.suggestion.Suggest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static dev.rollczi.litecommands.Assert.assertCollection;

class SchematicInParallelTest {

    TestPlatform testPlatform = TestFactory.withCommands(Command.class);
    SchemeGenerator schemeGenerator = SchemeGenerator.simple();

    @Section(route = "test")
    private static class Command {

        @Execute(required = 1)
        void test(@Suggest("arg") @Arg @Name("argument") String test) {}

        @Execute(route = "add", required = 1)
        void add(@Arg @Name("argument") String test) {}

        @Execute(route = "remove", required = 1)
        void remove(@Arg @Name("argument") String test) {}
    }

    @Test
    void test() {
        FindResult<TestHandle> result = testPlatform.find("test");
        List<String> schemes = schemeGenerator.generate(result, SchemeFormat.ARGUMENT_ANGLED_OPTIONAL_SQUARE);

        assertCollection(3, Arrays.asList(
                "/test <argument>",
                "/test add <argument>",
                "/test remove <argument>"
        ), schemes);
    }

}
