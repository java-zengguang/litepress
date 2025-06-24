package io.github.java_zengguang.litepress.chain.components;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Components {
    String name();

    String type();
}
