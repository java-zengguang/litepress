package com.zg.mvc.servlet;

import org.apache.catalina.filters.CorsFilter;

import javax.servlet.*;
import java.util.EnumSet;
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
            FilterRegistration.Dynamic corsFilter = servletContext.addFilter("CorsFilter", new CorsFilter());
            corsFilter.setInitParameter("cors.allowed.origins", "*");
            corsFilter.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false, "AdapterServlet");
            corsFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.INCLUDE, DispatcherType.FORWARD, DispatcherType.ERROR), false, "/*");
        }
    }
}
