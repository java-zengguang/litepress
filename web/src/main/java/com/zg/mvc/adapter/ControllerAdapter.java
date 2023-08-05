package com.zg.mvc.adapter;

import com.zg.common.init.Config;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.mvc.analysis.RequestAnalysis;
import com.zg.mvc.analysis.SimpleRequestAnalysis;
import com.zg.mvc.annotation.controller.ParamEntity;
import com.zg.mvc.annotation.controller.RequestBody;
import com.zg.mvc.entity.MVCOption;
import com.zg.mvc.entity.ViewObject;
import com.zg.mvc.util.ResolveAnnotation;
import com.zg.mvc.util.io.IOUtils;
import com.zg.mvc.util.io.ResovleUploadThread;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections.map.HashedMap;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/11/29 0029.
 */
public class ControllerAdapter {

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


    public static String resovleViewJsonObject(Object viewObject, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String result = "";
        if (viewObject != null) {
            response.setHeader("content-type", "application/json");
            response.setCharacterEncoding("UTF-8");
            result = JsonUtils.objectToJsonString(viewObject);
        }

        return result;
    }

    public static String resovleViewString(String viewObject, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String result = "";
        if (viewObject == null) {
            return result;
        }

        String viewString = (String) viewObject;
        String[] stirngArray = viewString.split("::");
        switch (stirngArray[0]) {
            case "forward": {
                request.getRequestDispatcher(stirngArray[1]).forward(request, response);
                break;
            }
            case "redirect": {
                response.sendRedirect(stirngArray[1]);
                break;
            }
            case "staticURL": {
                response.sendRedirect(stirngArray[1]);
                break;
            }
            case "privateURL": {
                request.getRequestDispatcher(stirngArray[1]).forward(request, response);
                // response.sendRedirect(stirngArray[1]);
                break;
            }
            case "json": {
                response.setHeader("content-type", "application/json");
                response.setCharacterEncoding("UTF-8");
                result = stirngArray[1];
                break;

            }
            default: {
                Logger.info(ControllerAdapter.classMap + " 跳转失败");
                break;
            }
        }

        return result;
    }


    public static String resovleViewObject(ViewObject viewObject, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String result = "";
        if (viewObject == null) {
            return result;
        }


        switch (((ViewObject) viewObject).operation) {
            case "forward": {
                String url = (String) ((ViewObject) viewObject).url;
                request.getRequestDispatcher(url).forward(request, response);
                break;
            }
            case "redirect": {
                String url = (String) ((ViewObject) viewObject).url;
                response.sendRedirect(url);
                break;
            }
            case "staticURL": {
                String url = (String) ((ViewObject) viewObject).url;
                response.sendRedirect(url);
                break;
            }
            case "privateURL": {
                String url = (String) ((ViewObject) viewObject).url;
                request.getRequestDispatcher(url).forward(request, response);
                // response.sendRedirect(stirngArray[1]);
                break;
            }
            case "json": {
                String data = (String) ((ViewObject) viewObject).data;
                String json = null;
                try {
                    json = JsonUtils.objectToJson(data).toString();
                } catch (IllegalAccessException e) {
                    Logger.error("json转化错误", e);
                }
                response.setHeader("content-type", "application/json");
                response.setCharacterEncoding("UTF-8");
                result = json;
                break;
            }
            default: {
                Logger.info(ControllerAdapter.classMap + " 跳转失败");
                break;
            }
        }

        return result;
    }


    public static void resovleViewFile(File viewObject, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        File file = (File) viewObject;
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + file.getName());
        OutputStream out = response.getOutputStream();
        int size = IOUtils.inputFile(out, file);
        Logger.info(ControllerAdapter.classMap + "文件下载完成");
    }

    //用于处理文件上传
    private static Object[] getInputStream(HttpServletRequest request, HttpServletResponse response, Method method, String inputFilePath) throws IOException, InterruptedException {
        Object[] objects = new Object[1];

        ResovleUploadThread fileUpLoad = new ResovleUploadThread();
        objects[0] = fileUpLoad.execate(request, inputFilePath);

        return objects;
    }


    public static Object[] getParamter(HttpServletRequest request, HttpServletResponse response, Method method) throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {

        int parameterCount = method.getParameterCount();
        Parameter parameters[] = method.getParameters();
        Type  paramGenericityTypes[] = method.getGenericParameterTypes();
        Annotation annotationArrays[][] = method.getParameterAnnotations();
        Object parameterValues[] = new Object[parameterCount];
        List<ParamEntity> paramEntityList = new ArrayList<>();
        for (int i = 0; i < parameterCount; i++) {
            Class paramentType = Class.forName(parameters[i].getType().getName());
            ParamEntity paramEntity = new ParamEntity();
            paramEntity.annotations = annotationArrays[i];
            paramEntity.paramName = parameters[i].getName();
            paramEntity.paramType = paramentType;
            paramEntity.paramGenericityType=paramGenericityTypes[i];
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

    public static void resovleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String result = "";
        String requestURI = request.getRequestURI();
        Logger.info("请求的url " + requestURI);
        Object viewObject = null;
        if (requestURI.endsWith(mvcOption.controllerSuffix) || requestURI.endsWith(mvcOption.upLoadSuffix)) {
            if (mvcOption.projectRoot != null && !"".equals(mvcOption.projectRoot) && requestURI.contains(mvcOption.projectRoot)) {
                requestURI = requestURI.replaceFirst(mvcOption.projectRoot, "");
            }
            String parentURI = "/" + requestURI.split("/")[1];
            Class classes = classMap.get(parentURI);
            Method method = methodMap.get(requestURI);

            try {
                if (requestURI.endsWith(mvcOption.controllerSuffix)) {
                    Object[] paramArray = getParamter(request, response, method);
                    if (paramArray == null) {
                        viewObject = method.invoke(classes.newInstance());
                    } else {
                        viewObject = method.invoke(classes.newInstance(), paramArray);
                    }
                }
                if (requestURI.endsWith(mvcOption.upLoadSuffix)) {
                    Object[] paramArray = getInputStream(request, response, method, mvcOption.temporaryFilePath);
                    viewObject = method.invoke(classes.newInstance(), paramArray);
                }

            } catch (Exception e) {
                Logger.error(e);
                result = e.getMessage();
            }
        }
        //处理返回参数
        if (viewObject instanceof File) {  //处理文件下载
            resovleViewFile((File) viewObject, request, response);
            return;
        } else {
            if (viewObject instanceof String) {  //String类型
                result = resovleViewString((String) viewObject, request, response);
            } else if (viewObject instanceof ViewObject) { //标准viewObject类型
                result = resovleViewObject((ViewObject) viewObject, request, response);
            } else {
                result = resovleViewJsonObject(viewObject, request, response);

            }
            PrintWriter out = response.getWriter();
            out.print(result);
            out.flush();
            out.close();
            return;
        }

    }
}
