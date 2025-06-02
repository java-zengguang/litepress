package com.zg.litepress.web.annotation.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 2018/12/3 0003.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String value() default "";
}
