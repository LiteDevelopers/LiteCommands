package dev.rollczi.litecommands.argument.option;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import panda.std.Option;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OptionUtilsTest {

    @Test
    @DisplayName("test method extractOptionType()")
    void extractOptionTypeTest() throws NoSuchMethodException {
        Option<Class<?>> optionType = OptionUtils.extractOptionType(Example.class.getDeclaredMethod("test", Option.class).getParameters()[0]);

        assertEquals(String.class, optionType.get());
    }

    static class Example {

        void test(Option<String> option) {}

    }

}
