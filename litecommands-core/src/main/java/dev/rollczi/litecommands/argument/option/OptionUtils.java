package dev.rollczi.litecommands.argument.option;

import panda.std.Option;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class OptionUtils {

    static Option<Class<?>> extractOptionType(Parameter parameter) {
        Type parameterizedType = parameter.getParameterizedType();

        if (parameterizedType instanceof ParameterizedType) {
            ParameterizedType parameterized = (ParameterizedType) parameterizedType;
            Type[] arguments = parameterized.getActualTypeArguments();

            if (arguments.length == 0) {
                return Option.none();
            }

            Type type = arguments[0];

            if (!(type instanceof Class)) {
                return Option.none();
            }

            return Option.of((Class<?>) type);
        }

        return Option.none();
    }

}
