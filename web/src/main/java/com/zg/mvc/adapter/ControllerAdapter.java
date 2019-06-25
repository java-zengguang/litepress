package com.zg.mvc.adapter;

import com.zg.init.Config;
import com.zg.mvc.entity.MVCOption;
import com.zg.mvc.entity.ViewObject;
import com.zg.mvc.util.FileUpLoad;
import com.zg.mvc.util.ResolveAnnotation;
import com.zg.mvc.util.io.IOUtils;
import com.zg.util.reflect.FieldUtils;
import com.zg.util.reflect.JsonUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/11/29 0029.
 */
public class ControllerAdapter {

    private static final Logger LOGGER=LoggerFactory.getLogger(ControllerAdapter.class);
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
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        LOGGER.info("MVC初始化中");
        LOGGER.info("methodMap " + methodMap);
        LOGGER.info("classMap " + classMap);
        LOGGER.info("MVC初始化完成");
        return true;
    }


    public static void resovleViewObject(Object viewObject, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (viewObject == null) {
            return;
        }
        if (viewObject instanceof String) {
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
                case "privateURL":{
                    request.getRequestDispatcher(stirngArray[1]).forward(request, response);
                   // response.sendRedirect(stirngArray[1]);
                    break;
                }
                case "json": {
                    response.setHeader("content-type", "application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    out.print(stirngArray[1]);
                    out.flush();
                    out.close();
                    break;
                }
                default: {
                    LOGGER.info(ControllerAdapter.classMap + " 跳转失败");
                }
            }
        }else if(viewObject instanceof ViewObject){

            switch (((ViewObject) viewObject).operation) {
                case "forward": {
                    String url= (String) ((ViewObject) viewObject).url;
                    request.getRequestDispatcher(url).forward(request, response);
                    break;
                }
                case "redirect": {
                    String url= (String) ((ViewObject) viewObject).url;
                    response.sendRedirect(url);
                    break;
                }
                case "staticURL": {
                    String url= (String) ((ViewObject) viewObject).url;
                    response.sendRedirect(url);
                    break;
                }
                case "privateURL": {
                    String url= (String) ((ViewObject) viewObject).url;
                    request.getRequestDispatcher(url).forward(request, response);
                    // response.sendRedirect(stirngArray[1]);
                    break;
                }
                case "json": {
                    String data= (String) ((ViewObject) viewObject).data;
                    String json= null;
                    try {
                        json = JsonUtils.objectToJson(data).toString();
                    } catch (IllegalAccessException e) {
                        LOGGER.error("json转化错误",e);
                    }
                    response.setHeader("content-type", "application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    out.print(json);
                    out.flush();
                    out.close();
                    break;
                }
                default: {
                    LOGGER.info(ControllerAdapter.classMap + " 跳转失败");
                }
            }
        } else if(viewObject instanceof File){
            File file=(File) viewObject;
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition",
                    "attachment;filename="+file.getName());
            OutputStream out=response.getOutputStream();
            int size= IOUtils.inputFile(out,file);
            LOGGER.info(ControllerAdapter.classMap+"文件下载完成");
        }
    }

    //注入参数
    private static Object[] extractParam(HttpServletRequest request, HttpServletResponse response, Method method) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
/*
        Map<String,String[]> map=request.getParameterMap();
*/
        int parameterCount = method.getParameterCount();
        Parameter parameters[] = method.getParameters();
        Object parameterValues[] = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            String paramentName = parameters[i].getName();
            Class paramentType = Class.forName(parameters[i].getType().getName());
            if (HttpServletRequest.class.isAssignableFrom(paramentType) || HttpServletResponse.class.isAssignableFrom(paramentType)) {
                if (HttpServletRequest.class.isAssignableFrom(paramentType)) {
                    parameterValues[i] = request;
                }
                if (HttpServletResponse.class.isAssignableFrom(paramentType)) {
                    parameterValues[i] = response;
                }
            } else {
                if (FieldUtils.isPrimitive(paramentType)) {

                    parameterValues[i] = FieldUtils.translateType(request.getParameter(parameters[i].getName()),paramentType);
                } else if (FieldUtils.isMap(paramentType)) {
                    Map valueMap = new HashedMap();
                    Enumeration paNames = request.getParameterNames();
                    while (paNames.hasMoreElements()) {
                        String paramName = (String) paNames.nextElement();
                        String value = request.getParameter(paramName);
                        valueMap.put(paramName, value);
                    }
                    parameterValues[i] = valueMap;
                } else {
                    Object object = paramentType.newInstance();
                    Field fields[] = object.getClass().getFields();
                    for (Field field : fields) {
                        String value = request.getParameter(paramentName + "." + field.getName());
                        FieldUtils.setField(field, object, value);
                    }
                    parameterValues[i] = object;
                }
            }
        }

        return parameterValues;
    }


    //用于处理文件上传
    private static Object[] getInputStream(HttpServletRequest request, HttpServletResponse response, Method method, String inputFilePath) throws IOException, InterruptedException {
        Object[] objects = new Object[1];
        InputStream inputStream = request.getInputStream();
        String contentType = request.getContentType();
        String targetS = contentType.substring(contentType.lastIndexOf("boundary=") + 9, contentType.length());

        String inputFileName=targetS;

        //生成临时文件
        if(IOUtils.createTemporaryFile(inputStream, inputFilePath,inputFileName)==-1){
            LOGGER.info("文件上传失败");
            objects[0]="上传失败";
            return objects;
        }
        FileUpLoad fileUpLoad = new FileUpLoad();
        String filePath = fileUpLoad.upload(targetS,inputFilePath,targetS, mvcOption.upLoadPath);

        objects[0] = filePath;  //返回文件的路径

        return objects;
    }


    public static void resovleRequest(HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("请求的url "+request.getRequestURL());
        String requestURI = request.getRequestURI();
        Object viewObject = null;
        if (requestURI.endsWith(mvcOption.controllerSuffix) || requestURI.endsWith(mvcOption.upLoadSuffix )) {
            if(mvcOption.projectRoot!=null && !"".equals(mvcOption.projectRoot) && requestURI.contains(mvcOption.projectRoot)) {
                requestURI = requestURI.replaceFirst(mvcOption.projectRoot, "");
            }
            String parentURI = "/" + requestURI.split("/")[1];
            Class classes = classMap.get(parentURI);
            Method method = methodMap.get(requestURI);
            try {
                if (requestURI.endsWith(mvcOption.controllerSuffix)) {
                    Object[] paramArray = extractParam(request, response, method);
                    if(paramArray==null){
                        viewObject = method.invoke(classes.newInstance());
                    }else {
                        viewObject = method.invoke(classes.newInstance(), paramArray);
                    }

                }
                if (requestURI.endsWith(mvcOption.upLoadSuffix)) {
                    Object[] paramArray = getInputStream(request, response, method, mvcOption.temporaryFilePath);
                    viewObject = method.invoke(classes.newInstance(), paramArray);
                }

                if (viewObject != null) {
                    resovleViewObject(viewObject, request, response);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else{
           /* if(mvcOption.projectRoot!=null && !"".equals(mvcOption.projectRoot)){
                int index=requestURI.indexOf("/",6);
                requestURI=requestURI.substring(0,index)+mvcOption.projectRoot+requestURI.substring(index,requestURI.length());
            }*/
        }

    }
}
