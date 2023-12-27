package com.zg.sso.filter;

import com.zg.common.init.Config;
import com.zg.mvc.entity.MVCOption;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.RequestFacade;
import org.apache.coyote.Request;
import org.apache.tomcat.util.buf.MessageBytes;
import org.tinylog.Logger;

import java.io.IOException;
import java.lang.reflect.Field;


/**
 * Created by Administrator on 2019/2/14 0014.
 */
public class ChangeURIFilter implements Filter {
    private final MVCOption mvcOption = (MVCOption) Config.getConfig("MVCOption");


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }


    private String changeURI(String requestPath) {
        if (mvcOption.projectRoot != null && !"".equals(mvcOption.projectRoot)) {
            if (requestPath.endsWith(mvcOption.controllerSuffix) || requestPath.endsWith(mvcOption.upLoadSuffix)) {
                requestPath = requestPath.replaceFirst(mvcOption.projectRoot, "");
            }
        }
        return requestPath;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        synchronized (this) {
            try {
                Logger.info("原本=" + ((HttpServletRequest) servletRequest).getRequestURI());
                RequestFacade facade = (RequestFacade) servletRequest;
                Class clzz = RequestFacade.class;
                Field field = clzz.getDeclaredField("request");
                field.setAccessible(true);
                org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request) field.get(facade);
                Class requestClass = request.getClass();
                //获取封装org.apache.coyote.Request的字段
                Field coyoteField = requestClass.getDeclaredField("coyoteRequest");
                coyoteField.setAccessible(true);
                Request coyoteRequest = (Request) coyoteField.get(request);
                Class requestClazz = Request.class;
                //获取org.apache.coyote.Request中保存路径的字段
                Field uriMBField = requestClazz.getDeclaredField("uriMB");
                uriMBField.setAccessible(true);
                MessageBytes uriMB = (MessageBytes) uriMBField.get(coyoteRequest);
                //这里就是改变路径的地方
                String path = changeURI(uriMB.getString());
                uriMB.setString(path);
                filterChain.doFilter((ServletRequest) facade, servletResponse);
                //用来打印请求路径
                Logger.info("changeUrl=" + ((RequestFacade) servletRequest).getRequestURL());

            } catch (Exception e) {
                Logger.error(e);
                Logger.info("URL错误");
            }
        }
    }

    @Override
    public void destroy() {

    }
}
