package io.github.java_zengguang.litepress.boot.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface TargetEvn {
    String value() default "";
}

