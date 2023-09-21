package dev.rollczi.litecommands.join;

import dev.rollczi.litecommands.argument.ArgumentKey;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Join {

    String separator() default " ";

    int limit() default Integer.MAX_VALUE;

}
