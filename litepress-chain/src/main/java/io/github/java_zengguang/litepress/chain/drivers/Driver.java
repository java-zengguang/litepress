package io.github.java_zengguang.litepress.chain.drivers;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Driver {
    String systemFlag();

    String functionFlag();
}
