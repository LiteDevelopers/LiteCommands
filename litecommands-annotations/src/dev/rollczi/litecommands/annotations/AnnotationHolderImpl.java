package dev.rollczi.litecommands.annotations;

import dev.rollczi.litecommands.wrapper.WrapFormat;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

class AnnotationHolderImpl<A extends Annotation, PARSED, OUT> implements AnnotationHolder<A, PARSED, OUT> {

    private final Annotation[] annotations;
    private final A annotation;
    private final Supplier<String> name;
    private final WrapFormat<PARSED, OUT> format;

    public AnnotationHolderImpl(Annotation[] annotations, A annotation, Supplier<String> name, WrapFormat<PARSED, OUT> format) {
        this.annotation = annotation;
        this.name = name;
        this.format = format;
        this.annotations = annotations;
    }

    public AnnotationHolderImpl(A annotation, Supplier<String> name, WrapFormat<PARSED, OUT> format) {
        this(new Annotation[0], annotation, name, format);
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.annotations;
    }

    @Override
    public A getAnnotation() {
        return annotation;
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public WrapFormat<PARSED, OUT> getFormat() {
        return format;
    }

}
