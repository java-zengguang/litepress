package com.zg.mvc.adapter;

import com.google.common.io.CharStreams;
import com.zg.common.init.Config;
import com.zg.mvc.analysis.RequestAnalysis;
import com.zg.mvc.analysis.SimpleRequestAnalysis;
import com.zg.mvc.annotation.controller.AsyncMethod;
import com.zg.mvc.annotation.controller.ParamEntity;
import com.zg.mvc.annotation.controller.RequestBody;
import com.zg.mvc.entity.MVCOption;
import com.zg.mvc.intercept.*;
import com.zg.mvc.util.ResolveAnnotation;
import com.zg.mvc.util.io.ResovleUploadThread;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;


public abstract class BaseControllerAdapter implements ControllerAdapterInte {

    public static final List<PostControllerIntercept> postControllerIntercepts = new ArrayList<>();
    public static final List<PreControllerIntercept> preControllerIntercepts = new ArrayList<>();
    private static final Map<String, Method> methodMap = new HashMap<>();
    private static Map<String, Class<?>> classMap = null;
    private static MVCOption mvcOption = null;


    public BaseControllerAdapter() {
        if (mvcOption == null) {
            initController();
        }
    }

    private synchronized void initController() {
        try {
            postControllerIntercepts.addAll(Arrays.asList(new JsonPostIntercept(), new FilePostIntercept()));
            mvcOption = (MVCOption) Config.getConfig("MVCOption");
            classMap = ResolveAnnotation.resovleController(mvcOption.projectRoot);
            Set<String> keySet = classMap.keySet();
            for (String parentURI : keySet) {
                methodMap.putAll(ResolveAnnotation.resovleResultMap(parentURI, classMap.get(parentURI)));
            }

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Logger.error(e);
        }
        Logger.info("MVC初始化中");
        Logger.info("methodMap " + methodMap);
        Logger.info("classMap " + classMap);
        Logger.info("MVC初始化完成");
    }


    public Object[] getParameter(HttpServletRequest request, Method method) throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {

        int parameterCount = method.getParameterCount();
        Parameter[] parameters = method.getParameters();
        Type[] paramGenericityTypes = method.getGenericParameterTypes();
        Annotation[][] annotationArrays = method.getParameterAnnotations();
        Object[] parameterValues = new Object[parameterCount];
        List<ParamEntity> paramEntityList = new ArrayList<>();
        for (int i = 0; i < parameterCount; i++) {
            Class<?> paramentType = Class.forName(parameters[i].getType().getName());
            ParamEntity paramEntity = new ParamEntity();
            paramEntity.annotations = annotationArrays[i];
            paramEntity.paramName = parameters[i].getName();
            paramEntity.paramType = paramentType;
            paramEntity.paramGenericityType = paramGenericityTypes[i];
            paramEntity.paramObject = request.getParameter(paramEntity.paramName); //标准表单
            paramEntity.isJson = false;
            Annotation[] annotations = paramEntity.annotations;
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof RequestBody) {
                        paramEntity.isJson = true;
                        paramEntity.paramObject = CharStreams.toString(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
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
    private Object[] getInputStream(HttpServletRequest request,String inputFilePath) throws IOException, InterruptedException {
        Object[] objects = new Object[1];
        ResovleUploadThread fileUpLoad = new ResovleUploadThread();
        objects[0] = fileUpLoad.execate(request, inputFilePath);

        return objects;
    }


    public Object deal(Class classes, Method method, HttpServletRequest request ) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, InterruptedException {
        String requestURI = request.getRequestURI();
        Object resultObj = null;
        if (requestURI.endsWith(mvcOption.controllerSuffix)) {
            Object[] paramArray = getParameter(request, method);
            if (paramArray == null) {
                resultObj = method.invoke(classes.newInstance());
            } else {
                resultObj = method.invoke(classes.newInstance(), paramArray);
            }
        }
        if (requestURI.endsWith(mvcOption.upLoadSuffix)) {
            Object[] paramArray = getInputStream(request,  mvcOption.temporaryFilePath);
            resultObj = method.invoke(classes.newInstance(), paramArray);
        }

        return resultObj;
    }


    public Method router(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        Logger.info("请求的url " + requestURI);
        if (requestURI.endsWith(mvcOption.controllerSuffix) || requestURI.endsWith(mvcOption.upLoadSuffix)) {
            if (mvcOption.projectRoot != null && !mvcOption.projectRoot.isEmpty() && requestURI.contains(mvcOption.projectRoot)) {
                requestURI = requestURI.replaceFirst(mvcOption.projectRoot, "");
            }
            return methodMap.get(requestURI);
        }

        return null;
    }




    public void synRequest(Method method, HttpServletRequest request, HttpServletResponse response) throws IOException, ClassNotFoundException, InterruptedException, InvocationTargetException, IllegalAccessException, InstantiationException {

            Class<?> clazz = method.getDeclaringClass();
            Object obj = deal(clazz, method, request);
            obj = postIntercept(request, response, obj);
            //处理返回参数
            String resultObj = "";
            if (obj != null && obj instanceof String) {
                resultObj = (String) obj;
            }
            PrintWriter out = response.getWriter();
            out.print(resultObj);
            out.flush();
            out.close();
    }

    public void asynRequest(Method method, HttpServletRequest request, HttpServletResponse response) throws IOException, ClassNotFoundException, InterruptedException, InvocationTargetException, IllegalAccessException, InstantiationException, ServletException {
        Class clazz = method.getDeclaringClass();
        final AsyncContext asyncContext = request.startAsync();
        int timeOut = method.getAnnotation(AsyncMethod.class).timeOut();
        asyncContext.setTimeout(timeOut);  // 6秒超时
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                // 超时处理逻辑
                event.getAsyncContext().getResponse().getWriter().println("Request timed out");
                event.getAsyncContext().complete();
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
                Logger.info("使用异步请求！！");
                // 不需要实现
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                // 错误处理逻辑
                event.getAsyncContext().getResponse().getWriter().println("An error occurred");
                event.getAsyncContext().complete();
            }

            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                // 不需要实现
                Logger.info("使用异步请求完成！！");
            }
        });
        Object obj = deal(clazz, method, request);
        obj = postIntercept(request, response, obj);
        //处理返回参数
        asyncContext.getResponse().getWriter().println(obj);
        // 结束异步处理
        asyncContext.complete();
    }


    public void doMain(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (!preIntercept(request, response)) {  //前置拦截返回false直接返回
                return;
            }
            Method method = router(request);
            if(method!=null) {
                //异步请求走异步方式
                if (method.isAnnotationPresent(AsyncMethod.class)) {
                    asynRequest(method, request, response);
                } else {
                    synRequest(method, request, response);
                }

            }
        } catch (Exception e) {
            Logger.error(e);
            if (e instanceof InvocationTargetException) {
                Logger.error(((InvocationTargetException) e).getTargetException());
            }
        }

    }


}
