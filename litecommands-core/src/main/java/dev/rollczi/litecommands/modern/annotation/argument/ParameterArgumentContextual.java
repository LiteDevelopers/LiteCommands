package dev.rollczi.litecommands.modern.annotation.argument;

import dev.rollczi.litecommands.modern.annotation.contextual.ParameterContextual;
import dev.rollczi.litecommands.modern.argument.ArgumentContextual;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

public class ParameterArgumentContextual<A extends Annotation, EXPECTED> extends ParameterContextual<EXPECTED> implements ArgumentContextual<A, EXPECTED> {

    private final A annotation;
    private final Class<A> annotationType;

    protected ParameterArgumentContextual(Parameter parameter, Method method, A annotation, Class<A> annotationType, Class<EXPECTED> expectedType, Class<?> expectedWrapperType) {
        super(method, parameter, expectedType, expectedWrapperType);
        this.annotation = annotation;
        this.annotationType = annotationType;
    }

    @Override
    public A getDeterminant() {
        return this.annotation;
    }

    @Override
    public Class<A> getDeterminantType() {
        return this.annotationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParameterArgumentContextual)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ParameterArgumentContextual<?, ?> that = (ParameterArgumentContextual<?, ?>) o;
        return this.annotation.equals(that.annotation) && this.annotationType.equals(that.annotationType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.annotation, this.annotationType);
    }

}
