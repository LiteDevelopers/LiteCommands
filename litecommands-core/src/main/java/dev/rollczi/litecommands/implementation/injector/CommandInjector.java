package dev.rollczi.litecommands.implementation.injector;

import dev.rollczi.litecommands.injector.Inject;
import dev.rollczi.litecommands.injector.InjectException;
import dev.rollczi.litecommands.injector.Injectable;
import dev.rollczi.litecommands.injector.Injector;
import dev.rollczi.litecommands.injector.InjectorSettings;
import dev.rollczi.litecommands.injector.InvokeContext;
import dev.rollczi.litecommands.injector.MissingBindException;
import dev.rollczi.litecommands.shared.ReflectFormat;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;
import panda.std.Result;
import panda.std.function.ThrowingBiFunction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CommandInjector<SENDER> implements Injector<SENDER> {


    private final InjectorContextProcessor<SENDER> processor;

    public CommandInjector(LiteInjectorSettings<SENDER> processor) {
        this.processor = new InjectorContextProcessor<>(processor.duplicate());
    }

    @Override
    public <T> T createInstance(Class<T> type, InvokeContext<SENDER> context) {
        return this.createInstance0(type, context, false)
                .orThrow((exception) -> new InjectException("Can't create new instance of class " + ReflectFormat.singleClass(type), exception))
                .orThrow(() -> new InjectException("Can't create new instance of class " + ReflectFormat.singleClass(type)));
    }

    @Override
    public <T> T createInstance(Class<T> type) {
        return this.createInstance(type, null);
    }

    private <T> Result<Option<T>, Exception> createInstance0(Class<T> type, InvokeContext<SENDER> context, boolean onlyWitInjectAnnotation) {
        LinkedHashSet<Constructor<?>> sortedConstructors = Arrays.stream(type.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!onlyWitInjectAnnotation) {
            List<Constructor<?>> withOutInject = Arrays.stream(type.getDeclaredConstructors())
                    .filter(constructor -> !constructor.isAnnotationPresent(Inject.class))
                    .collect(Collectors.toList());

            sortedConstructors.addAll(withOutInject);
        }

        Constructor<?>[] constructorsArray = sortedConstructors.toArray(new Constructor<?>[0]);

        return this.invokeExecutables(type, constructorsArray, context, (constructor, arg) -> type.cast(constructor.newInstance(arg)));
    }

    @Override
    public Object invokeMethod(Method method, Object instance, InvokeContext<SENDER> context) {
        return this.invokeExecutables(Object.class, new Method[]{ method }, context, (m, arg) -> m.invoke(instance, arg))
                .orThrow((blank) -> new IllegalStateException("The method " + ReflectFormat.docsMethod(method) + " cannot be invoked!"))
                .orNull();
    }

    @Override
    public Object invokeMethod(Method method, Object instance) {
        return this.invokeMethod(method, instance, null);
    }

    @Override
    public InjectorSettings<SENDER> settings() {
        return this.processor.settings().duplicate();
    }

    private <T extends Executable, R> Result<Option<R>, Exception> invokeExecutables(Class<R> type, T[] executables, @Nullable InvokeContext<SENDER> context, Invoker<T, R> invoker) {
        List<Exception> errors = new ArrayList<>();

        for (T executable : executables) {
            Result<Option<R>, Exception> result = this.invokeExecutable(executable, context, invoker);

            if (result.isOk()) {
                return Result.ok(result.get());
            }

            errors.add(result.getError());
        }

        InjectException exception = new InjectException("Can not execute executable");

        for (Exception error : errors) {
            exception.addSuppressed(error);
        }

        return Result.error(exception);
    }

    private <T extends Executable, R> Result<Option<R>, Exception> invokeExecutable(T executable, InvokeContext<SENDER> context, Invoker<T, R> invoker) {
        boolean useContext = context != null;
        Iterator<Object> iterator = useContext ? context.getInjectable().iterator() : Collections.emptyIterator();

        List<Object> parameters = new ArrayList<>();
        List<MissingBindException> missingBindExceptions = new ArrayList<>();

        for (Parameter parameter : executable.getParameters()) {
            Class<?> parameterType = parameter.getType();

            if (useContext && this.isInjectAnnotation(parameter)) {
                if (!iterator.hasNext()) {
                    if (executable instanceof Method) {
                        return Result.error(new MissingBindException(Collections.singletonList(parameterType),
                                "Missing " + ReflectFormat.singleClass(parameterType) + " argument for method: " + ReflectFormat.docsMethod((Method) executable) + " Have you added argument()?"));
                    }

                    return Result.error(new MissingBindException(Collections.singletonList(parameterType), "Argument in constructor? Missing bind's"));
                }

                parameters.add(iterator.next());
                continue;
            }

            Option<?> option = useContext
                    ? this.processor.extract(parameter, context.getInvocation())
                    : this.processor.extract(parameter);

            if (option.isPresent()) {
                parameters.add(option.get());
                continue;
            }

            Result<? extends Option<?>, Exception> result = this.createInstance0(parameterType, context, true);

            if (result.isOk() && result.get().isPresent()) {
                Object createdInstance = result.get().get();

                parameters.add(createdInstance);
                processor.settings().unSafeTypeBind(parameterType, () -> createdInstance);
                continue;
            }

            MissingBindException subBindException = new MissingBindException(
                    Collections.singletonList(parameterType),
                    useContext ? "Have you added typeBind() or contextualBind()?" : "Have you added typeBind()?",
                    result.getError()
            );

            missingBindExceptions.add(subBindException);
        }

        if (!missingBindExceptions.isEmpty()) {
            InjectException bindException = new InjectException("Can not find binds for " + executable.toGenericString());

            for (MissingBindException error : missingBindExceptions) {
                bindException.addSuppressed(error);
            }

            return Result.error(bindException);
        }

        try {
            executable.setAccessible(true);
            return Result.ok(Option.of(invoker.apply(executable, parameters.toArray(new Object[0]))));
        }
        catch (ReflectiveOperationException exception) {
            return Result.error(exception);
        }
    }

    private boolean isInjectAnnotation(Parameter parameter) {
        for (Annotation annotation : parameter.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(Injectable.class)) {
                return true;
            }
        }

        return false;
    }

    private interface Invoker<T extends Executable, R> extends ThrowingBiFunction<T, Object[], R, ReflectiveOperationException> {
    }

}
