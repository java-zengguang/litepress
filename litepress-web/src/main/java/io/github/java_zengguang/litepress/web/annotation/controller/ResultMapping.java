package io.github.java_zengguang.litepress.web.annotation.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 2018/12/4 0004.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultMapping {
    String value();
}
