package io.github.java_zengguang.litepress.direction.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Provider {
    String providerName() default "";

}
