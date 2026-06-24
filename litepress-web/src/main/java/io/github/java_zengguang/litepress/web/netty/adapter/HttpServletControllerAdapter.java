package io.github.java_zengguang.litepress.web.netty.adapter;

import com.google.common.io.CharStreams;

import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.web.annotation.controller.ParamEntity;
import io.github.java_zengguang.litepress.web.annotation.controller.RequestBody;
import io.github.java_zengguang.litepress.web.entity.CookieEntity;
import io.github.java_zengguang.litepress.web.entity.HttpRequestEntity;
import io.github.java_zengguang.litepress.web.entity.HttpResponseEntity;
import io.github.java_zengguang.litepress.web.enums.SceneType;
import io.github.java_zengguang.litepress.web.util.io.IOUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpServletControllerAdapter extends BaseControllerAdapter implements HttpServletController {

    private static HttpServletControllerAdapter httpServletControllerAdapter;

    private HttpServletControllerAdapter() {
    }

    public synchronized static HttpServletControllerAdapter getInstance() {
        if (httpServletControllerAdapter == null) {
            httpServletControllerAdapter = new HttpServletControllerAdapter();
        }
        return httpServletControllerAdapter;
    }


    private List<CookieEntity> getCookies(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        return Arrays.stream(cookies).map(cookie -> {
            CookieEntity cookieEntity = new CookieEntity(cookie.getName(), cookie.getValue());
            cookieEntity.domain = cookie.getDomain();
            cookieEntity.path = cookie.getPath();
            cookieEntity.maxAge = (long) cookie.getMaxAge();
            cookieEntity.httpOnly = cookie.isHttpOnly();
            cookieEntity.secure = cookie.getSecure();
            return cookieEntity;
        }).toList();
    }

    private Map<String, List<String>> getHeaders(HttpServletRequest request) {
        Map<String, List<String>> headerMap = new HashMap<>();
        // 获取所有头信息的名字
        Enumeration<String> headerNames = request.getHeaderNames();
        // 遍历所有头信息
        while (headerNames.hasMoreElements()) {

            String headerName = headerNames.nextElement();
            List<String> headerValues = new ArrayList<>();
            // 如果你想获取所有具有相同名字的头信息（例如，Set-Cookie）
            Enumeration<String> values = request.getHeaders(headerName);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                headerValues.add(value);
            }
            if (!headerValues.isEmpty()) {
                headerMap.put(headerName, headerValues);
            }
        }
        return headerMap;
    }

    @Override
    public void dealHttpRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String requestURI = request.getRequestURI();
        HttpRequestEntity httpRequestEntity = new HttpRequestEntity();
        httpRequestEntity.url = requestURI;
        httpRequestEntity.path = requestURI;
        httpRequestEntity.contentType = request.getContentType();
        httpRequestEntity.methodType = request.getMethod();
        httpRequestEntity.headers = getHeaders(request);
        httpRequestEntity.cookies = getCookies(request);

        if ("GET".equals(request.getMethod())) {
            request.getParameterMap().forEach((key, values) -> httpRequestEntity.paramMap.put(key, values[0]));
        }
        if ("POST".equals(request.getMethod())) {
            if ("application/json".equals(httpRequestEntity.contentType)) {
                httpRequestEntity.sceneType = SceneType.JSON.name();
                httpRequestEntity.paramMap.put("body", CharStreams.toString(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8)));
            } else if ("application/x-www-form-urlencoded".equals(httpRequestEntity.contentType)) {
                httpRequestEntity.sceneType = SceneType.FORM.name();
                httpRequestEntity.paramMap.putAll(readBody(request));
            }
        }

        //执行controller
        HttpResponseEntity responseEntity = dealHttpRequest(httpRequestEntity);
        //添加响应 header
        if (responseEntity.headers != null && !responseEntity.headers.isEmpty()) {
            responseEntity.headers.forEach((key, values) -> {
                if (values != null && !values.isEmpty()) {
                    values.forEach(value -> response.addHeader(key, value));
                }
            });
        }
        //添加响应cookie
        if (responseEntity.cookies != null && !responseEntity.cookies.isEmpty()) {
            responseEntity.cookies.forEach((cookieEntity) -> {
                Cookie cookie=new Cookie(cookieEntity.name,cookieEntity.value);
                if(cookieEntity.domain!=null){
                    cookie.setDomain(cookieEntity.domain);
                }
                if(cookieEntity.path!=null){
                    cookie.setPath(cookieEntity.path);
                }
                if(cookieEntity.maxAge!=null){
                    cookie.setMaxAge(Math.toIntExact(cookieEntity.maxAge));
                }
                if(cookieEntity.httpOnly!=null){
                    cookie.setHttpOnly(cookieEntity.httpOnly);
                }
                if(cookieEntity.secure!=null){
                    cookie.setSecure(cookieEntity.secure);
                }
                response.addCookie(cookie);
            });
        }

        //设置响应状态
        response.setStatus(responseEntity.statusCode);
        //转换json和文件
        if (responseEntity.result instanceof Serializable || responseEntity.result instanceof Collection<?> || responseEntity.result instanceof Map<?, ?>) {
            responseEntity.result = JsonUtil.obj2String(responseEntity.result);
            response.setContentType("application/json");
        }

        if (responseEntity.result instanceof String) {
            PrintWriter out = response.getWriter();
            out.print(responseEntity.result);
            out.flush();
            out.close();
        }
        if (responseEntity.result instanceof File file) {
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
            IOUtils.inputFile(response.getOutputStream(), file);
        }

    }


    public List<ParamEntity> getParameters(Method method) throws ClassNotFoundException {
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


    // 从内容头中提取文件名
    private String extractFileName(Part part) {
        String contentDist = part.getHeader("content-disposition");
        if (contentDist != null) {
            for (String item : contentDist.split(";")) {
                if (item.trim().startsWith("filename")) {
                    return item.substring(item.indexOf("=") + 2, item.length() - 1);
                }
            }
        }
        return null;
    }

    //用于处理文件上传
    private Map<String, Object> readBody(HttpServletRequest request) throws IOException, ServletException {
        // 获取所有部分
        Map<String, Object> paramMap = new HashMap<>();
        Collection<Part> parts = request.getParts();
        List<File> files = new ArrayList<>();
        for (Part part : parts) {
            if (part.getSize() > 0) { // 确保部分不是空的
                String fileName = extractFileName(part);
                if (fileName != null && !fileName.isEmpty()) {
                    // 如果是文件部分，则保存文件到服务器
                    File file = File.createTempFile(mvcOption.temporaryFilePath, fileName);
                    files.add(file);
                } else {
                    // 处理普通表单字段
                    String formFieldName = part.getName();
                    String formFieldValue = request.getParameter(formFieldName);
                    paramMap.put(formFieldName, formFieldValue);
                }
            }
        }
        paramMap.put("files", files);
        return paramMap;
    }
}
