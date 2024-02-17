package com.peanut.diff.gen.annotation;

import com.peanut.diff.runtime.ChangeChecker;
import com.peanut.diff.runtime.checker.Skip;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
        ElementType.METHOD,
        ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings({ "rawtypes"})
public @interface DiffStrategy {
    Class<? extends ChangeChecker> value() default Skip.class;
    String key() default "";

}
