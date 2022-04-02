package com.zg.mvc.servlet;

import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.ApplicationFilterFactory;
import org.apache.catalina.filters.CorsFilter;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.FilterDef;


import javax.naming.NamingException;
import javax.servlet.*;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Set;

public class AServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {

        if (true) {
            System.out.println("加载servlet");
            ServletRegistration.Dynamic servletDynamic = servletContext.addServlet("AdapterServlet", new AdapterServlet());
            servletDynamic.addMapping("/");
        }
        if (true) {
            System.out.println("加载Filter");
            CorsFilter corsFilter = new CorsFilter();  //处理跨域的过滤器
            corsFilter.init(null);//走默认配置
            FilterRegistration.Dynamic filterDynamic = servletContext.addFilter("CorsFilter", corsFilter);
          //  filterDynamic.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.INCLUDE, DispatcherType.FORWARD, DispatcherType.ERROR), false, "/*");
            filterDynamic.addMappingForServletNames( EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC),false, "AdapterServlet" );
        }

    }
}
