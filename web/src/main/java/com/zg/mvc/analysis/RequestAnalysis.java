package com.zg.mvc.analysis;

import com.zg.mvc.annotation.controller.ParamEntity;


public interface RequestAnalysis {
    Object extractParam(ParamEntity paramEntity) throws ClassNotFoundException, IllegalAccessException, InstantiationException;

}
