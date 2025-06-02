package com.zg.litepress.web.servlet.analysis;

import com.zg.litepress.web.annotation.controller.ParamEntity;


public interface RequestAnalysis {
    Object extractParam(ParamEntity paramEntity) throws ClassNotFoundException, IllegalAccessException, InstantiationException;

}
