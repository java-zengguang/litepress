package com.zg.mvc.adapter;

import com.zg.common.init.Config;
import com.zg.mvc.analysis.RequestAnalysis;
import com.zg.mvc.analysis.SimpleRequestAnalysis;
import com.zg.mvc.annotation.controller.ParamEntity;
import com.zg.mvc.annotation.controller.RequestBody;
import com.zg.mvc.entity.MVCOption;
import com.zg.mvc.util.ResolveAnnotation;
import com.zg.mvc.util.io.ResovleUploadThread;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections.map.HashedMap;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class BaseControllerAdapter implements ControllerAdapterInte {
    private static Set<String> keySet = null;
    private static Map<String, Method> methodMap = new HashedMap();
    private static Map<String, Class> classMap = null;
    private static MVCOption mvcOption = null;

    static {
        initController();
    }

    private static synchronized boolean initController() {

        try {
            mvcOption = (MVCOption) Config.getConfig("MVCOption");
            classMap = ResolveAnnotation.resovleController(mvcOption.controllerPackage);
            keySet = classMap.keySet();
            for (String parentURI : keySet) {
                methodMap.putAll(ResolveAnnotation.resovleResultMap(parentURI, classMap.get(parentURI)));
            }

        } catch (ClassNotFoundException e) {
            Logger.error(e);
        } catch (IllegalAccessException e) {
            Logger.error(e);
        } catch (InstantiationException e) {
            Logger.error(e);
        }
        Logger.info("MVC初始化中");
        Logger.info("methodMap " + methodMap);
        Logger.info("classMap " + classMap);
        Logger.info("MVC初始化完成");
        return true;
    }


    public Object[] getParamter(HttpServletRequest request, HttpServletResponse response, Method method) throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {

        int parameterCount = method.getParameterCount();
        Parameter parameters[] = method.getParameters();
        Type paramGenericityTypes[] = method.getGenericParameterTypes();
        Annotation annotationArrays[][] = method.getParameterAnnotations();
        Object parameterValues[] = new Object[parameterCount];
        List<ParamEntity> paramEntityList = new ArrayList<>();
        for (int i = 0; i < parameterCount; i++) {
            Class paramentType = Class.forName(parameters[i].getType().getName());
            ParamEntity paramEntity = new ParamEntity();
            paramEntity.annotations = annotationArrays[i];
            paramEntity.paramName = parameters[i].getName();
            paramEntity.paramType = paramentType;
            paramEntity.paramGenericityType = paramGenericityTypes[i];
            paramEntity.paramObject = request.getParameter(paramEntity.paramName); //标准表单
            paramEntity.isJson = false;
            Annotation[] annotations = paramEntity.annotations;
            if (annotations != null && annotations.length >= 0) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof RequestBody) {
                        paramEntity.isJson = true;
                        paramEntity.paramObject = request.getInputStream();
                    }
                }
            }
            paramEntityList.add(paramEntity);
        }

        for (int j = 0; j < paramEntityList.size(); j++) {
            RequestAnalysis requestAnalysis = new SimpleRequestAnalysis();
            parameterValues[j] = requestAnalysis.extractParam(paramEntityList.get(j));
        }

        return parameterValues;
    }

    //用于处理文件上传
    private Object[] getInputStream(HttpServletRequest request, HttpServletResponse response, Method method, String inputFilePath) throws IOException, InterruptedException {
        Object[] objects = new Object[1];
        ResovleUploadThread fileUpLoad = new ResovleUploadThread();
        objects[0] = fileUpLoad.execate(request, inputFilePath);

        return objects;
    }


    @Override
    public Object routeRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, InterruptedException {
        String requestURI = request.getRequestURI();
        Logger.info("请求的url " + requestURI);
        Object resultObj = null;
        if (requestURI.endsWith(mvcOption.controllerSuffix) || requestURI.endsWith(mvcOption.upLoadSuffix)) {
            if (mvcOption.projectRoot != null && !"".equals(mvcOption.projectRoot) && requestURI.contains(mvcOption.projectRoot)) {
                requestURI = requestURI.replaceFirst(mvcOption.projectRoot, "");
            }
            String parentURI = "/" + requestURI.split("/")[1];
            Class classes = classMap.get(parentURI);
            Method method = methodMap.get(requestURI);


            if (requestURI.endsWith(mvcOption.controllerSuffix)) {
                Object[] paramArray = getParamter(request, response, method);


                if (paramArray == null) {
                    resultObj = method.invoke(classes.newInstance());
                } else {
                    resultObj = method.invoke(classes.newInstance(), paramArray);
                }
            }
            if (requestURI.endsWith(mvcOption.upLoadSuffix)) {
                Object[] paramArray = getInputStream(request, response, method, mvcOption.temporaryFilePath);
                resultObj = method.invoke(classes.newInstance(), paramArray);
            }


        }

        return resultObj;
    }


    public void doMain(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (!preIntercept(request, response)) {  //前置拦截返回false直接返回
                return;
            }
            Object obj = routeRequest(request, response);
            obj = postIntercept(request, response, obj);
            analysisResponse(request, response, obj);
        } catch (Exception e) {
            Logger.error(e);
        }

    }

    @Override
    public void analysisResponse(HttpServletRequest request, HttpServletResponse response, Object resultObj) throws IOException, ServletException {
        //处理返回参数

        if (resultObj instanceof String) {
            PrintWriter out = response.getWriter();
            out.print(resultObj);
            out.flush();
            out.close();
            return;
        }
    }

}
