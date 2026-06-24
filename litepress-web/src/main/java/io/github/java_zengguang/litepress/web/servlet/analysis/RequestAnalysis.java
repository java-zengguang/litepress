package io.github.java_zengguang.litepress.web.servlet.analysis;

import io.github.java_zengguang.litepress.web.annotation.controller.ParamEntity;


public interface RequestAnalysis {
    Object extractParam(ParamEntity paramEntity) throws ClassNotFoundException, IllegalAccessException, InstantiationException;

}
