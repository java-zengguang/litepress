package com.zg.mvc.adapter;

import com.zg.mvc.entity.HttpRequestEntity;
import com.zg.mvc.entity.HttpResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ControllerAdapter {

    HttpResponseEntity dealHttpRequest(HttpRequestEntity requestEntity) throws Exception;

}
