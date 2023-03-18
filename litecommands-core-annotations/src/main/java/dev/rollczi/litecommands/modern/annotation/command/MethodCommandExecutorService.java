package dev.rollczi.litecommands.modern.annotation.command;

import dev.rollczi.litecommands.modern.command.CommandExecutor;
import dev.rollczi.litecommands.modern.util.ReflectFormatUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MethodCommandExecutorService<SENDER> {

    private final Map<Class<?>, ParameterWithAnnotationResolver<SENDER, ?>> resolvers = new java.util.HashMap<>();
    private ParameterWithoutAnnotationResolver<SENDER> defaultResolver = parameter -> {
        throw new IllegalArgumentException("No annotation found for parameter " + ReflectFormatUtil.parameter(parameter));
    };

    public CommandExecutor<SENDER> create(Object instance, Method method) {
        List<ParameterPreparedArgument<SENDER, ?>> arguments = this.resolveParameters(method);

        return new MethodCommandExecutor<>(method, instance, arguments);
    }

    public <A extends Annotation> void registerResolver(Class<A> type, ParameterWithAnnotationResolver<SENDER, A> annotationResolver) {
        resolvers.put(type, annotationResolver);
    }

    public void defaultResolver(ParameterWithoutAnnotationResolver<SENDER> defaultResolver) {
        this.defaultResolver = defaultResolver;
    }

    private List<ParameterPreparedArgument<SENDER, ?>> resolveParameters(Method method) {
        List<ParameterPreparedArgument<SENDER, ?>> preparedArguments = new ArrayList<>();

        for (Parameter parameter : method.getParameters()) {
            preparedArguments.addAll(this.resolveParameter(parameter));
        }

        return preparedArguments;
    }

    private List<ParameterPreparedArgument<SENDER, ?>> resolveParameter(Parameter parameter) {
        Annotation[] annotations = parameter.getAnnotations();

        if (annotations.length == 0) {
            ParameterPreparedArgument<SENDER, ?> argument = defaultResolver.resolve(parameter);

            return Collections.singletonList(argument);
        }

        List<ParameterPreparedArgument<SENDER, ?>> preparedArguments = new ArrayList<>();

        for (Annotation annotation : annotations) {
            preparedArguments.add(this.resolveParameterWithAnnotation(parameter, annotation));
        }

        return preparedArguments;
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> ParameterPreparedArgument<SENDER, ?> resolveParameterWithAnnotation(Parameter parameter, A annotation) {
        ParameterWithAnnotationResolver<SENDER, A> resolver = (ParameterWithAnnotationResolver<SENDER, A>) resolvers.get(annotation.annotationType());

        if (resolver == null) {
            throw new IllegalArgumentException("Can not resolve " + ReflectFormatUtil.parameter(parameter, annotation));
        }

        return resolver.resolve(parameter, annotation);
    }

}
