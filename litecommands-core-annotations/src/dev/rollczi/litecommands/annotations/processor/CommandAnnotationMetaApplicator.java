package dev.rollczi.litecommands.annotations.processor;

import dev.rollczi.litecommands.editor.CommandEditorContext;
import dev.rollczi.litecommands.editor.CommandEditorExecutorBuilder;
import dev.rollczi.litecommands.meta.CommandMetaHolder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface CommandAnnotationMetaApplicator<SENDER, A extends Annotation> extends
    CommandAnnotationClassResolver<SENDER, A>,
    CommandAnnotationMethodResolver<SENDER, A> {

    void apply(Object instance, A annotation, CommandMetaHolder metaHolder);

    @Override
    default CommandEditorContext<SENDER> resolve(Object instance, A annotation, CommandEditorContext<SENDER> context) {
        this.apply(instance, annotation, context.route());
        return context;
    }

    @Override
    default CommandEditorContext<SENDER> resolve(Object instance, Method method, A annotation, CommandEditorContext<SENDER> context, CommandEditorExecutorBuilder<SENDER> executorBuilder) {
        this.apply(instance, annotation, executorBuilder);
        return context;
    }

}
