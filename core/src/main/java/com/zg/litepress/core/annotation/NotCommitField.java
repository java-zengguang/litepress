package com.zg.litepress.core.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NotCommitField {
    String tableName() default "";
}
