package io.github.java_zengguang.litepress.web.netty.adapter;


import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.core.util.reflect.TypeConverter;
import io.github.java_zengguang.litepress.web.annotation.controller.ParamEntity;
import io.github.java_zengguang.litepress.web.annotation.controller.RequestBody;
import io.github.java_zengguang.litepress.web.entity.HttpRequestEntity;
import io.github.java_zengguang.litepress.web.entity.HttpResponseEntity;
import io.github.java_zengguang.litepress.web.entity.MVCOption;
import io.github.java_zengguang.litepress.web.netty.intercepter.HttpInterceptor;
import io.github.java_zengguang.litepress.web.servlet.adapter.ControllerAdapter;
import io.github.java_zengguang.litepress.web.util.ResolveAnnotation;
import io.netty.handler.codec.http.HttpMethod;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class BaseControllerAdapter implements ControllerAdapter {
    public final MVCOption mvcOption;
    public static final Map<String, Method> methodMap = new HashMap<>();
    public static List<HttpInterceptor> postHttpInterceptors = new ArrayList<>();
    public static List<HttpInterceptor> preHttpInterceptors = new ArrayList<>();
    public static List<String> whiteList = List.of("/favicon.ico");

    public BaseControllerAdapter() {
        this.mvcOption = (MVCOption) Config.getConfig("MVCOption");
        initController();
    }

    private synchronized void initController() {
        try {
            Logger.info("MVC初始化开始");
            Map<String, Class<?>> classMap = ResolveAnnotation.resovleController(mvcOption.projectRoot);
            for (String parentURI : classMap.keySet()) {
                methodMap.putAll(ResolveAnnotation.resovleResultMap(parentURI, classMap.get(parentURI)));
            }
            Logger.info("MVC初始化完成 methodMap " + methodMap);
        } catch (Exception e) {
            Logger.error(e, "MVC初始化失败");
        }
    }


    @Override
    public HttpResponseEntity dealHttpRequest(HttpRequestEntity requestEntity) throws Exception {
        Logger.info("请求的url " + requestEntity.url);
        if (whiteList.contains(requestEntity.url)) {
            HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
            httpResponseEntity.statusCode = 200;
            return httpResponseEntity;
        }

        //构造参数
        Method method = methodMap.get(requestEntity.path);
        if (method == null) {
            HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
            httpResponseEntity.statusCode = 500;
            httpResponseEntity.result = "未找到controller";
            return httpResponseEntity;
        }


        //构建返回
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();

        //执行前置拦截
        for (HttpInterceptor interceptor : preHttpInterceptors) {
            if (!interceptor.doInvoke(requestEntity, httpResponseEntity)) {
                return httpResponseEntity;
            }
        }

        List<Object> methodParamValues = new ArrayList<>();
        List<ParamEntity> methodParams = getParameters(method);

        for (ParamEntity methodParam : methodParams) {
            Object methodParamValue = null;
            //处理文件
            if (methodParam.isJson) {
                methodParamValue = JsonUtil.string2Obj(requestEntity.body, methodParam.paramGenericityType);
            }
            if (methodParam.paramType == File.class && requestEntity.paramMap.containsKey("files")) {
                List files = (List) requestEntity.paramMap.get("files");
                methodParamValue = files.getFirst();
            }
            //处理form中的string变量
            if (requestEntity.paramMap.containsKey(methodParam.paramName)) {
                methodParamValue = TypeConverter.convertStringToType((String) requestEntity.paramMap.get(methodParam.paramName), methodParam.paramType);
            }
            methodParamValues.add(methodParamValue);
        }

        //执行controller方法
        try {
            httpResponseEntity.result = method.invoke(method.getDeclaringClass().newInstance(), methodParamValues.toArray());
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                Logger.error(((InvocationTargetException) e).getTargetException(), "系统内部错误！");
            } else {
                Logger.error(e, "系统内部错误！");
            }
            httpResponseEntity.statusCode = 500;
            httpResponseEntity.result = "系统错误！";
        }
        //执行后置拦截
        for (HttpInterceptor interceptor : postHttpInterceptors) {
            if (!interceptor.doInvoke(requestEntity, httpResponseEntity)) {
                return httpResponseEntity;
            }
        }

        return httpResponseEntity;

    }


    public List<ParamEntity> getParameters(Method method) throws ClassNotFoundException, IOException {
        int parameterCount = method.getParameterCount();
        Parameter[] parameters = method.getParameters();
        Type[] paramGenericTypes = method.getGenericParameterTypes();
        Annotation[][] annotationArrays = method.getParameterAnnotations();
        List<ParamEntity> paramEntityList = new ArrayList<>();
        for (int i = 0; i < parameterCount; i++) {
            Class<?> parameterType = Class.forName(parameters[i].getType().getName());
            ParamEntity paramEntity = new ParamEntity();
            paramEntity.annotations = annotationArrays[i];
            paramEntity.paramName = parameters[i].getName();
            paramEntity.paramType = parameterType;
            paramEntity.paramGenericityType = paramGenericTypes[i];
            paramEntity.isJson = false;
            Annotation[] annotations = paramEntity.annotations;
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof RequestBody) {
                        paramEntity.isJson = true;
                        break;
                    }
                }
            }
            paramEntityList.add(paramEntity);
        }

        return paramEntityList;
    }


}
