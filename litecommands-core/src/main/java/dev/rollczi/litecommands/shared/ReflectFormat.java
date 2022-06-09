package dev.rollczi.litecommands.shared;

import panda.std.function.ThrowingSupplier;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class ReflectFormat {

    private static final String METHOD = "%s#%s";
    private static final String METHOD_WITH_PARAM = "%s#%s(%s)";
    private static final String METHOD_WITH_ALL = "%s %s %s(%s)";

    private ReflectFormat() {}

    public static String singleClass(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    public static String docksShortMethod(Method method) {
        return String.format(METHOD, singleClass(method.getDeclaringClass()), method.getName());
    }

    public static String docsMethod(Method method) {
        return String.format(METHOD_WITH_PARAM, singleClass(method.getDeclaringClass()), method.getName(), parameters(method));
    }

    public static String method(Method method) {
        return String.format(METHOD_WITH_ALL, modifier(method), singleClass(method.getReturnType()), method.getName(), parameters(method));
    }

    public static String method(ThrowingSupplier<Method, ReflectiveOperationException> supplier) {
        try {
            return method(supplier.get());
        } catch (ReflectiveOperationException argumentException) {
            throw new IllegalArgumentException(argumentException);
        }
    }

    public static String parameters(Method method) {
        return Arrays.stream(method.getParameters()).map(param -> {
            String annotations = Arrays.stream(param.getAnnotations()).map(annotation -> "@" + annotation.annotationType().getSimpleName()).collect(Collectors.joining(" "));
            String type = param.getType().getSimpleName();
            String name = param.getName();

            return annotations.isEmpty() ? type + " " + name : annotations + " " + type + " " + name;
        }).collect(Collectors.joining(", "));
    }

    public static String modifier(Method method) {
        return Modifier.toString(method.getModifiers());
    }

}
