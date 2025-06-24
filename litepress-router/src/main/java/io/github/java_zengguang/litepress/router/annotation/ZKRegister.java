package io.github.java_zengguang.litepress.router.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 2018/12/24 0024.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ZKRegister {
    String name();

    String namespace();

    String routerType();
}
